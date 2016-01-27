package nounou.analysis.spikes

import java.math.BigInteger

import breeze.linalg.DenseVector
import breeze.numerics.abs
import breeze.stats.median

import nounou.options._
import nounou.analysis.{ThresholdOpt, Threshold}
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.spikes.NNSpikes
import nounou.ranges.{NNRangeSpecifier, NNRangeValid}
import nounou.util.LoggingExt

import scala.collection.mutable.ArrayBuffer

// <editor-fold defaultstate="collapsed" desc=" options ">

trait SpikeDetectOpt extends Opt

object OptSpikeDetectMethod extends SpikeDetectOpt {

  case object MedianSDThresoldPeak

}

// </editor-fold>

/**
  * @author ktakagaki
  */
object SpikeDetect extends LoggingExt
  with OptHandler {

//  def apply(data: Array[Double], opts: SpikeDetectOpt*): Array[Int] = {
//    val optMethod: OptSpikeDetectMethod =
//      readOptObject[OptSpikeDetectMethod](opts, OptSpikeDetectMethodMEDIANSDTHRESHOLDPEAK)
//    optMethod match {
//      case m: OptSpikeDetectMethodMEDIANSDTHRESHOLDPEAK =>{
//        val optMedianFactor = readOptDouble
//        medianSDThresholdPeakDetect()
//      }
//    }
//  }

  // <editor-fold defaultstate="collapsed" desc=" medianSDThresholdPeakDetect ">

  def medianSDThresholdPeakDetect(data: Array[Double], medianFactor: Double, peakWindow: Int): Array[Int] = {
    val absMedianThreshold = medianFactor * median( abs( DenseVector( data ) ) ) / 0.6745
    val tempTriggers = Threshold(data, absMedianThreshold)
    val tempret = ArrayBuffer[Int]()
    var c = 0
    while( c < tempTriggers.length ){
      val tempTrig = tempTriggers(c)
      val tempTrigLast = tempTrig + peakWindow
      val maxPos =
        if( tempTrigLast < data.length ) {
          maxPosition(data, tempTrig, tempTrigLast)
        } else Int.MaxValue
      if( maxPos < tempTrigLast) tempret.+=( maxPos )
      c += 1
    }

    tempret.toArray
  }

  def medianSDThresholdPeakDetect(dataChannel: NNDataChannel,
                                  range: NNRangeSpecifier,
                                  medianFactor: Double,
                                  peakWindow: Int): Array[BigInteger] = {
    val tempFrameArray = medianSDThresholdPeakDetect( dataChannel.readTrace(range), medianFactor, peakWindow )
    tempFrameArray.map( dataChannel.timing().convertFrsgToTs(_, range.getInstantiatedSegment(dataChannel)).bigInteger)
  }

  // <editor-fold defaultstate="collapsed" desc=" impl ">

  private def maxPosition(array: Array[Double], start: Int, last: Int): Int = {
    loggerRequire(array != null, "input cannot be null!")
    loggerRequire(start < array.length, "start must be within array range!")

    var maxPos = start
    var maxVal = array(start)
    var c = start + 1
    while (c <= last){
      if( array(c) > maxVal ){
        maxPos = c
        maxVal = array(c)
      }
      c += 1
    }
    maxPos
  }

  // </editor-fold>

  def thresholdSpikes(data: NNData,
                      channels: Array[Int],
                      frameRange: NNRangeSpecifier,
                      opts: Opt*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" Option handling ">

    var optWaveformFr = 32
    var optAlignmentPoint  = 8
    var optBlackoutFr = 16

    for( opt <- opts ) opt match {
      case OptWaveformFr(frames: Int) => optWaveformFr = frames
      case OptAlignmentPoint(frames: Int) => optAlignmentPoint = frames
      case OptBlackoutFr(frames: Int) => optBlackoutFr = frames
      case _ => {}
    }

    val tempPosttriggerFr = optWaveformFr -optAlignmentPoint -1
    loggerRequire( tempPosttriggerFr >=0, s"OptWaveformFr ($optWaveformFr) must be strictly larger than OptPretriggerFr ($optAlignmentPoint)!")

    // </editor-fold>

    val pooledSpikes: NNSpikes = new NNSpikes(optAlignmentPoint)
    for( ch <- channels ){
      pooledSpikes.add( spikeThresholdAbsMedianImpl(data, ch, frameRange,  opts: _*) )
    }

    val filteredSpikes: NNSpikes = new NNSpikes(optAlignmentPoint)
    var iterator = pooledSpikes.iterator()
    //If there is at least 1 spike in pooledSpikes
    if( iterator.hasNext ){
      var lastSpike = iterator.next

      while(iterator.hasNext){
        val nextSpike = iterator.next

        //If the spike is within the blackout range from the previous spike
        if( nextSpike.timestamp - lastSpike.timestamp < optBlackoutFr){
          //compare absolute maxima, and advance marker without recording if higher
          if( nextSpike.waveformAbsMax > lastSpike.waveformAbsMax){
            lastSpike = nextSpike
          }
        //If the next spike is outside the blackout range of the previous max spike
        } else {
          //record the last maximum spike and advance
          filteredSpikes.add(lastSpike)
          lastSpike = nextSpike
        }
      }
    }
    val filteredTimestamps: Array[BigInt] = filteredSpikes.readSpikeTimestamps().map( BigInt(_) )
    NNSpikes( data, filteredTimestamps, channels, opts: _* )

  }

  /**Detect spikes from a single channel of data*/
  def spikeThresholdAbsMedianImpl(data: NNData,
                                  channel: Int,
                                  frameRange: NNRangeSpecifier,
                                  opts: Opt*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" Handle options ">

    var optDetectionWindow = 320000
    var optDetectionWindowOverlap = 320
    var optThresholdSDFactor = 3d
    var optAlignmentPoint  = 8

    for (opt <- opts) opt match {
      case OptDetectionWindow(fr) => optDetectionWindow = fr
      case OptDetectionWindowOverlap(fr) => optDetectionWindowOverlap = fr
      case OptThresholdSDFactor(factor: Double) => optThresholdSDFactor = factor
      case OptAlignmentPoint(frames: Int) => optAlignmentPoint = frames
      case _ => {}
    }

    if (optDetectionWindow < 32000) throw loggerError("optDetectionWindow must be 32000 or larger!")
    if (optDetectionWindowOverlap > optDetectionWindow / 10) throw
      loggerError(s"optDetectionWindowOverlap ($optDetectionWindowOverlap) must be smaller than 1/10 of optDetectionWindow  ($optDetectionWindow)!")
//    val tempPosttriggerFr = optWaveformFr -optPretriggerFr -1
//    loggerRequire( tempPosttriggerFr >=0, s"OptWaveformFr ($optWaveformFr) must be strictly larger than OptPretriggerFr ($optPretriggerFr)!")

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" divide data into sub segments for processing">

    var rangeList: List[NNRangeValid] = Nil
    val validRange = frameRange.getValidRange(data)
    loggerRequire(validRange.step == 1, "step size for spike detection must be 1! {} is invalid!", validRange.step.toString)
    var start = validRange.start

    if (validRange.length > optDetectionWindow) {
      //var bufferedData: Array[Array[Int]] = null
      while (start < validRange.last - optDetectionWindow) {
        rangeList.+:(new NNRangeValid(start, start + optDetectionWindow, 1, validRange.segment))
        start += optDetectionWindow - optDetectionWindowOverlap
      }
      if (start < validRange.last) {
        rangeList.+:(new NNRangeValid(start, validRange.last, 1, validRange.segment))
      }
    } else {
        rangeList = List(validRange)
    }

    // </editor-fold>

//    /** Return variable **/
//    val tempret =
//    rangeList.par.flatMap(
//      (r: NNRangeValid) => {
//        val analysisData = data.readTrace(channel, r)
//        //ToDo 2: convert the following median SD estimate into a breeze function
//        val thresholdValue = optThresholdSDFactor * median( DenseVector( abs(analysisData) ) ) /0.6745
//        val tempretInner = Threshold( analysisData, thresholdValue, opts: _* ).map((x: Int) => x + r.start )
//
//        //Delete first trigger if it is the first frame (0)... not a spike
//        if( tempretInner.length > 0 && tempretInner(0) == 0d ) tempretInner.tail else tempretInner
//      }
//    ).seq.toArray//.distinct.sorted  ...NNSpikes TreeSet will take care of distinct/sorted part

//    NNSpikes( data, frames = tempret.map( _ - optPretriggerFr), channel, validRange.segment, opts: _* )
???
  }

}
