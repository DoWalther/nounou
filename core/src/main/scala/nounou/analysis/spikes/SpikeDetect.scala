package nounou.analysis.spikes

import breeze.linalg.DenseVector
import breeze.numerics.abs
import breeze.stats.median

import java.math.BigInteger

import nounou.NNOpt.AlignmentPoint
import nounou.NNOpt.{BlackoutFrames, AlignmentPoint, WaveformFrames}
import nounou.Options.{BlackoutFrames, AlignmentPoint, WaveformFrames, MedianSDThresholdPeak}
import nounou.options._
import nounou.analysis.{Threshold}
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.spikes.{OptReadSpikes, NNSpikes}
import nounou.ranges.{NNRangeInstantiated, NNRangeSpecifier, NNRangeValid}
import nounou.util.LoggingExt

import scala.collection.mutable.ArrayBuffer

trait OptSpikeDetect extends Opt

/**
  * @author ktakagaki
  */
object SpikeDetect extends LoggingExt
  with OptHandler {

  def apply(data: Array[Double], opts: OptSpikeDetect*): Array[Int] = {
    val optMethod: OptSpikeDetect = readOptObject[OptSpikeDetect](opts, MedianSDThresholdPeak)
    optMethod match {
      case MedianSDThresholdPeak =>{
        val optMedianFactor = readOptDouble(opts, 3)
        val optPeakWindow = readOptInt(opts, 32)
        medianSDThresholdPeakDetect(data, optMedianFactor, optPeakWindow)
      }
      case _ => throw loggerError(s"option method $optMethod is not valid")
    }
  }

  def apply(data: NNData,
            range: NNRangeSpecifier,
            channel: Int,
            opts: OptSpikeDetect*): Array[BigInteger] = {
    val optMethod: OptSpikeDetect = readOptObject[OptSpikeDetect](opts, MedianSDThresholdPeak)
    optMethod match {
      case MedianSDThresholdPeak =>{
        val optMedianFactor = readOptDouble(opts, 3)
        val optPeakWindow = readOptInt(opts, 32)
        medianSDThresholdPeakDetect(data, range, channel, optMedianFactor, optPeakWindow)
      }
    }
  }

  def apply(dataChannel: NNDataChannel,
            range: NNRangeSpecifier,
            opts: OptSpikeDetect*): Array[BigInteger] = {
    val optMethod: OptSpikeDetect = readOptObject[OptSpikeDetect](opts, MedianSDThresholdPeak)
    optMethod match {
      case MedianSDThresholdPeak =>{
        val optMedianFactor = readOptDouble(opts, 3)
        val optPeakWindow = readOptInt(opts, 32)
        medianSDThresholdPeakDetect(dataChannel, range, optMedianFactor, optPeakWindow)
      }
    }
  }

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

  def medianSDThresholdPeakDetect(data: NNData,
                                  range: NNRangeSpecifier,
                                  channel: Int,
                                  medianFactor: Double,
                                  peakWindow: Int): Array[BigInteger] = {

    //split range up for calculation
    val rangesInstantiated = range.getInstantiatedRange(data).split(320000, 64).toSet

    rangesInstantiated.flatMap(
      (r: NNRangeInstantiated) => {
        val tempFrames = medianSDThresholdPeakDetect(data.readTrace(channel, r), medianFactor, peakWindow)
        tempFrames.map( (frame: Int) =>
                            data.timing().convertFrsgToTs(
                                                  r.start + frame,
                                                  r.segment
                                                         ).bigInteger
        )
      }
    ).toArray.sorted

  }

  def medianSDThresholdPeakDetect(dataChannel: NNDataChannel,
                                  range: NNRangeSpecifier,
                                  medianFactor: Double,
                                  peakWindow: Int): Array[BigInteger] = {
    //split range up for calculation
    val rangesInstantiated = range.getInstantiatedRange(dataChannel).split(3200000, 64).toSet

    rangesInstantiated.flatMap(
      (r: NNRangeInstantiated) => {
        val tempFrames = medianSDThresholdPeakDetect(dataChannel.readTrace(r), medianFactor, peakWindow)
        tempFrames.map( (frame: Int) =>
          dataChannel.timing().convertFrsgToTs(
            r.start + frame,
            r.segment
          ).bigInteger
        )
      }
    ).toArray.sorted

    //    val tempFrameArray = medianSDThresholdPeakDetect( dataChannel.readTrace(range), medianFactor, peakWindow )
//    val rangeInstantiated = range.getInstantiatedRange(dataChannel)
//    tempFrameArray.map(
//      (frame: Int) =>
//        dataChannel.timing().convertFrsgToTs(
//          rangeInstantiated.start + frame,
//          rangeInstantiated.segment
//        ).bigInteger
//    )
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
                      opts: OptReadSpikes*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" Option handling ">

    var optWaveformFrames = 32
    var optAlignmentPoint  = 8
    var optBlackoutFrames = 16

    for( opt <- opts ) opt match {
      case WaveformFrames(frames: Int) => optWaveformFrames = frames
      case AlignmentPoint(frames: Int) => optAlignmentPoint = frames
      case BlackoutFrames(frames: Int) => optBlackoutFrames = frames
      case _ => {}
    }

    val tempPosttriggerFr = optWaveformFrames -optAlignmentPoint -1
    loggerRequire( tempPosttriggerFr >=0, s"OptWaveformFr ($optWaveformFrames) must be strictly larger than OptPretriggerFr ($optAlignmentPoint)!")

    // </editor-fold>

    val pooledSpikes: NNSpikes = new NNSpikes(optAlignmentPoint, scaling = data, timing = data)
    for( ch <- channels ){
      pooledSpikes.add( spikeThresholdAbsMedianImpl(data, ch, frameRange,  opts: _*) )
    }

    val filteredSpikes: NNSpikes = new NNSpikes(optAlignmentPoint, scaling = data, timing = data)
    var iterator = pooledSpikes.iterator()
    //If there is at least 1 spike in pooledSpikes
    if( iterator.hasNext ){
      var lastSpike = iterator.next

      while(iterator.hasNext){
        val nextSpike = iterator.next

        //If the spike is within the blackout range from the previous spike
        if( nextSpike.timestamp - lastSpike.timestamp < optBlackoutFrames){
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
    NNSpikes.readSpikes( data, filteredTimestamps, channels, opts: _* )

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
      case AlignmentPoint(value: Int) => optAlignmentPoint = value
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
