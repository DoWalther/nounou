package nounou.analysis.spikes

import breeze.linalg.DenseVector
import breeze.numerics.abs
import breeze.stats.median
import java.math.BigInteger

import nounou.options._
import nounou.analysis.{Threshold}
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.spikes.{OptReadSpikes, NNSpikes}
import nounou.ranges.{NNRangeInstantiated, NNRangeSpecifier, NNRangeValid}
import nounou.util.LoggingExt

import scala.collection.mutable.ArrayBuffer

/**
  * @author ktakagaki
  */
object SpikeDetect extends LoggingExt /*with OptHandler*/ {

  // <editor-fold defaultstate="collapsed" desc=" apply ">

  def apply(data: Array[Double], opts: OptSpikeDetect*): Array[Int] = {
    val optMethod = OptHandler.readOptString[OptSpikeDetectMethodString](opts: Seq[Opt], "MedianSDThresholdPeak")

    optMethod match {
      case "MedianSDThresholdPeak" => {
        val optMedianFactor = OptHandler.readOptDouble[OptMedianFactorDouble](opts: Seq[Opt], 3d)
        val optPeakWindow = OptHandler.readOptInt[OptPeakWindowInt](opts: Seq[Opt], 32)
        medianSDThresholdPeakDetect(data, optMedianFactor, optPeakWindow)
      }
      case _ => throw new IllegalArgumentException("only MedianSDThresholdPeak is supported")
    }

  }

  def apply(data: NNData,
            range: NNRangeSpecifier, channel: Int,
            opts: OptSpikeDetect*): Array[BigInteger] = {
    //val optMethod: OptSpikeDetect = readOptObject[OptSpikeDetect](opts, MedianSDThresholdPeak)
    val optMethod = OptHandler.readOptString[OptSpikeDetectMethodString](opts, "MedianSDThresholdPeak")

    optMethod match {
      case "MedianSDThresholdPeak" => {
        val optMedianFactor = OptHandler.readOptDouble[OptMedianFactorDouble](opts: Seq[Opt], 3d)
        val optPeakWindow = OptHandler.readOptInt[OptPeakWindowInt](opts: Seq[Opt], 32)
        medianSDThresholdPeakDetect(data, range, channel, optMedianFactor, optPeakWindow)
      }
      case _ => throw new IllegalArgumentException("only MedianSDThresholdPeak is supported")
    }

  }

  def apply(data: NNData,
            range: NNRangeSpecifier,
            channels: Array[Int],
            opts: OptSpikeDetect*): Array[BigInteger] = {
    val optMethod = OptHandler.readOptString[OptSpikeDetectMethodString](opts, "MedianSDThresholdPeak")

    optMethod match {
      case "MedianSDThresholdPeak" => {
        val optMedianFactor = OptHandler.readOptDouble[OptMedianFactorDouble](opts: Seq[Opt], 3d)
        val optPeakWindow = OptHandler.readOptInt[OptPeakWindowInt](opts: Seq[Opt], 32)
        medianSDThresholdPeakDetect(data, range, channels, optMedianFactor, optPeakWindow)
      }
      case _ => throw new IllegalArgumentException("only MedianSDThresholdPeak is supported")
    }
  }

  def apply(dataChannel: NNDataChannel,
            range: NNRangeSpecifier,
            opts: OptSpikeDetect*): Array[BigInteger] = {
    //val optMethod: OptSpikeDetect = readOptObject[OptSpikeDetect](opts, MedianSDThresholdPeak)
    val optMethod = OptHandler.readOptString[OptSpikeDetectMethodString](opts, "MedianSDThresholdPeak")

    optMethod match {
      case "MedianSDThresholdPeak" => {
        val optMedianFactor = OptHandler.readOptDouble[OptMedianFactorDouble](opts: Seq[Opt], 3d)
        val optPeakWindow = OptHandler.readOptInt[OptPeakWindowInt](opts: Seq[Opt], 32)
        medianSDThresholdPeakDetect(dataChannel, range, optMedianFactor, optPeakWindow)
      }
      case _ => throw new IllegalArgumentException("only MedianSDThresholdPeak is supported")
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" medianSDThresholdPeakDetect ">

  def medianSDThresholdPeakDetect(data: Array[Double], medianFactor: Double, peakWindow: Int): Array[Int] = {
    val absData = abs( DenseVector( data ) )
    val absMedianThreshold = medianFactor * median( absData ) / 0.6745
    val tempTriggers = Threshold(absData.toArray, absMedianThreshold)
    val tempret = ArrayBuffer[Int]()
    var c = 0
    while( c < tempTriggers.length ){
      val tempTrig = tempTriggers(c)
      val tempTrigLast = tempTrig + peakWindow -1
      val maxPos =
        if( tempTrigLast < absData.length ) {
          //if threshold trigger value is over zero, abs threshold is positive
          if( data(tempTrig) > 0) maxPosition(data, tempTrig, tempTrigLast)
          //if threshold trigger value is less than zero, abs threshold is negative
          else minPosition(data, tempTrig, tempTrigLast)
        } else Int.MaxValue
      if( maxPos < tempTrigLast) tempret.+=( maxPos )
      c += 1
    }

    tempret.toArray
  }

  def medianSDThresholdPeakDetect(data: Array[Array[Double]], medianFactor: Double, peakWindow: Int): Array[Int] = {
    val absData: Array[DenseVector[Double]] = data.map((a: Array[Double]) => abs( DenseVector( a ) ) )
    val absMedianThreshold: Array[Double] = absData.map( medianFactor * median( _ ) / 0.6745 )
    val tempTriggers: Array[Int] = (absData zip absMedianThreshold).flatMap(
                          (t: (DenseVector[Double], Double)) => Threshold(t._1.toArray, t._2 )
                       ).toSet[Int].toArray[Int].sorted
    val tempReturn = ArrayBuffer[Int]()
    var c = 0
    while( c < tempTriggers.length ){
      val tempTrig = tempTriggers(c)
      val tempTrigLast = tempTrig + peakWindow -1
      val maxPos = if( tempTrigLast < absData(0).length ) {
                      maxPosition(absData, tempTrig, tempTrigLast)
                    } else Int.MaxValue
      if( maxPos < tempTrigLast ) tempReturn.+=( maxPos )
      c += 1
    }

    tempReturn.toArray
  }

  def medianSDThresholdPeakDetect(data: NNData,
                                  range: NNRangeSpecifier,
                                  channel: Int,
                                  medianFactor: Double,
                                  peakWindow: Int): Array[BigInteger] = {

    //split range up for calculation
    val rangesInstantiated = range.getInstantiatedRange(data).split(16000, 64).toSet

    rangesInstantiated.flatMap(
      (r: NNRangeInstantiated) => {
        val tempFrames = medianSDThresholdPeakDetect( data.readTrace(channel, r), medianFactor, peakWindow )
        tempFrames.map( (frame: Int) =>
            data.timing().convertFrsgToTs( r.start + frame, r.segment ).bigInteger
        )
      }
    ).toArray.sorted

  }

  def medianSDThresholdPeakDetect(data: NNData,
                                  range: NNRangeSpecifier,
                                  channels: Array[Int],
                                  medianFactor: Double,
                                  peakWindow: Int): Array[BigInteger] = {

    //split range up for calculation
    val rangesInstantiated = range.getInstantiatedRange(data).split(16000, 64).toSet

    rangesInstantiated.flatMap(
      (r: NNRangeInstantiated) => {
        val tempFrames = medianSDThresholdPeakDetect( data.readPage(channels, r), medianFactor, peakWindow )
        tempFrames.map( (frame: Int) =>
          data.timing().convertFrsgToTs( r.start + frame, r.segment ).bigInteger
        )
      }
    ).toArray.sorted

  }

  def medianSDThresholdPeakDetect(dataChannel: NNDataChannel,
                                  range: NNRangeSpecifier,
                                  medianFactor: Double,
                                  peakWindow: Int): Array[BigInteger] = {
    //split range up for calculation
    val rangesInstantiated = range.getInstantiatedRange(dataChannel).split(16000, 64).toSet

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

  /**
    * Finds the index of the maximum within the range
    *
    */
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
  private def maxPosition(array: Array[DenseVector[Double]], start: Int, last: Int): Int = {
    //loggerRequire(array != null, "input cannot be null!")
    //loggerRequire(start < array.length, "start must be within array range!")

    var maxPos = start
    var maxVal = Double.NegativeInfinity
    var c = start
    while (c <= last){
      for(ch <- 0 until array.length ){
        if( array(ch)(c) > maxVal ) {
          maxPos = c
          maxVal = array(ch)(c)
        }
      }
      c += 1
    }
    maxPos
  }
  private def minPosition(array: Array[Double], start: Int, last: Int): Int = {
    loggerRequire(array != null, "input cannot be null!")
    loggerRequire(start < array.length, "start must be within array range!")

    var minPos = start
    var minVal = array(start)
    var c = start + 1
    while (c <= last){
      if( array(c) < minVal ){
        minPos = c
        minVal = array(c)
      }
      c += 1
    }
    minPos
  }

  // </editor-fold>

  // </editor-fold>


  def thresholdSpikes(data: NNData,
                      channels: Array[Int],
                      frameRange: NNRangeSpecifier,
                      opts: OptReadSpikes*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" Option handling ">

    val optWaveformFrames = OptHandler.readOptInt[OptWaveformFramesInt](opts, 32)
    val optAlignmentPoint  = OptHandler.readOptInt[OptAlignmentPointInt](opts, 8)
    val optBlackoutFrames = OptHandler.readOptInt[OptBlackoutFramesInt](opts, 16)

    //    var optWaveformFrames = 32
//    var optAlignmentPoint  = 8
//    var optBlackoutFrames = 16
//
//    for( opt <- opts ) opt match {
//      case WaveformFrames(frames: Int) => optWaveformFrames = frames
//      case OptAlignmentPoint(frames: Int) => optAlignmentPoint = frames
//      case OptBlackoutFrames(frames: Int) => optBlackoutFrames = frames
//      case _ => {}
//    }

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

    val optDetectionWindow = OptHandler.readOptInt[OptDetectionWindowInt](opts, 3200000)
    val optAlignmentPoint  = OptHandler.readOptInt[OptAlignmentPointInt](opts, 8)
    val optBlackoutFrames = OptHandler.readOptInt[OptBlackoutFramesInt](opts, 16)
    var optDetectionWindowOverlap = OptHandler.readOptInt[OptDetectionWindowOverlapInt](opts, 320)
    var optThresholdSDFactor = OptHandler.readOptDouble[OptThresholdSDFactorDouble](opts, 3d)

//    //    var optDetectionWindow = 320000
//    //var optAlignmentPoint  = 8
//    for (opt <- opts) opt match {
//      case OptDetectionWindow(fr) => optDetectionWindow = fr
//      case OptDetectionWindowOverlap(fr) => optDetectionWindowOverlap = fr
//      case OptThresholdSDFactor(factor: Double) => optThresholdSDFactor = factor
//      case OptAlignmentPoint(value: Int) => optAlignmentPoint = value
//      case _ => {}
//    }

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
