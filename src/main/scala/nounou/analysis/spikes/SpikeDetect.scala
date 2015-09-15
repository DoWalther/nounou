package nounou.analysis.spikes

import breeze.linalg.{max, convert, DenseVector}
import breeze.numerics.{abs}
import breeze.stats.{median}
import nounou.Opt
import nounou.analysis.threshold
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.ranges.{SampleRangeValid, SampleRangeSpecifier}
import nounou.elements.spikes.NNSpikes
import nounou.util.LoggingExt

import scala.collection.mutable.ArrayBuffer

/**
* @author ktakagaki
*/
object SpikeDetect extends LoggingExt {

//  def it() = this

  def thresholdSpikes(data: NNData,
                          channels: Array[Int],
                          frameRange: SampleRangeSpecifier,
                          opts: Seq[Opt]): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" Option handling ">

    var optWaveformFr = 32
    var optPrepeakFr  = 8
    var optBlackoutFr = 16

    for( opt <- opts ) opt match {
      case OptWaveformFr(frames: Int) => optWaveformFr = frames
      case OptPrepeakFr(frames: Int) => optPrepeakFr = frames
      case OptBlackoutFr(frames: Int) => optBlackoutFr = frames
      case _ => {}
    }

    // </editor-fold>

    val pooledSpikes = channels.flatMap( spikeThresholdAbsMedian(data, _, frameRange, opts) ).distinct.sorted
    var tempRetIndexes: List[Int] = Nil

    var currentIndex = 0
    var triggered = false
    var lastMaxSpikeIndex = 0
    var lastMaxSpikeValue = Integer.MIN_VALUE
    while(currentIndex < pooledSpikes.length) {
      if(triggered) {
        //If the current spike within the trigger is within optBlackoutFr away from the last spike
        if( pooledSpikes(currentIndex) - pooledSpikes(currentIndex-1) > optBlackoutFr ) {
          val currentMax = max(channels.map((ch: Int) => data.readPoint(ch, pooledSpikes(currentIndex))))
          if (currentMax > lastMaxSpikeValue) {
            lastMaxSpikeIndex = currentIndex
            lastMaxSpikeValue = currentMax
          }
        } else {
          tempRetIndexes.+:(lastMaxSpikeIndex)
          triggered = false
        }
      } else {
        triggered = true
        lastMaxSpikeIndex = currentIndex
        lastMaxSpikeValue = max(channels.map((ch: Int) => data.readPoint(ch, pooledSpikes(currentIndex))))
      }
      currentIndex += 1
    }

    val tempRet = new NNSpikes()
    tempRetIndexes.toArray.reverse.map(
      (index: Int) =>
        tempRet.add(
          data.readSpike(
              channels, index,
              segment = frameRange.getRealSegment(data),
              spikeLength = optWaveformFr)
          )
    )

    tempRet
  }

  /**Detect spikes from a single channel of data*/
  def spikeThresholdAbsMedian(data: NNData,
                      channel: Int,
                      frameRange: SampleRangeSpecifier,
                      opts: Opt*): Array[Int] = {

    // <editor-fold defaultstate="collapsed" desc=" Handle options ">

    var optDetectionWindow = 3200000
    var optDetectionWindowOverlap = 320
    var optThresholdSDFactor = 3d

    for (opt <- opts) opt match {
      case OptDetectionWindow(fr) => optDetectionWindow = fr
      case OptDetectionWindowOverlap(fr) => optDetectionWindowOverlap = fr
      case OptThresholdSDFactor(factor: Double) => optThresholdSDFactor = factor
      case _ => {}
    }

    if (optDetectionWindow < 32000) throw loggerError("optDetectionWindow must be 32000 or larger!")
    if (optDetectionWindowOverlap > optDetectionWindow / 10) throw
      loggerError(s"optDetectionWindowOverlap ($optDetectionWindowOverlap) must be smaller than 1/10 of optDetectionWindow  ($optDetectionWindow)!")

    // </editor-fold>

    var rangeList: List[SampleRangeValid] = Nil
    val validRange = frameRange.getSampleRangeValid(data)
    loggerRequire(validRange.step == 1, "step size for spike detection must be 1! {} is invalid!", validRange.step.toString)
    var start = validRange.start

    if (validRange.length > optDetectionWindow) {
      //var bufferedData: Array[Array[Int]] = null
      while (start < validRange.last - optDetectionWindow) {
        rangeList.+:(new SampleRangeValid(start, start + optDetectionWindow, 1, validRange.segment))
        start += optDetectionWindow - optDetectionWindowOverlap
      }
      if (start < validRange.last) {
        rangeList.+:(new SampleRangeValid(start, validRange.last, 1, validRange.segment))
      }
    } else {
        rangeList = List(validRange)
    }

    /** Return variable **/
    val tempret =
    rangeList.par.flatMap(
      (r: SampleRangeValid) => {
        val analysisData = data.readTrace(channel, r)
        val thresholdValue = convert(optThresholdSDFactor * median(abs(analysisData)) /0.6745, Int)
        threshold(analysisData, thresholdValue, opts).map( (x: Int) => x + r.start )
      }
    ).seq.toArray.distinct.sorted

    tempret
  }

}
