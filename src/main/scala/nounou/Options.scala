package nounou

import nounou.analysis.spikes.OptSpikeDetect
import nounou.elements.spikes.OptReadSpikes
import nounou.options._

/**
  * Created by ktakagaki on 16/01/28.
  */
object Options {


  // <editor-fold defaultstate="collapsed" desc=" OptSpikeDetect ">

  case class SpikeDetectMethod(override val value: String) extends OptString
//  case class SpikeDetectMethod(value: Marker) extends OptMarker
//  case object MedianSDThresholdPeak extends Marker("MedianSDThresholdPeak")
  //case object MedianSDThresholdPeak extends OptMarker("MedianSDThresholdPeak") with OptSpikeDetect
  //case object MedianSDThresholdPeak extends OptString("MedianSDThresholdPeak") with OptSpikeDetect
  case class MedianFactor(value: Double) extends OptSpikeDetect with OptDouble
  case class PeakWindow(value: Int) extends OptSpikeDetect with OptInt

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" OptReadSpikes ">

  case class AlignmentPoint(value: Int) extends Opt with OptReadSpikes with OptInt {

    loggerRequire(value >=0, "Must have a pre-trigger alignment point >=0!")

  }
  /**How many frames of data to record in a spike waveform*/
  case class WaveformFrames(value: Int)
    extends Opt with OptReadSpikes with OptInt {

    loggerRequire(value >=16, "Must have a waveform frame count of at least 16!")

  }
  case class OverlapWindow(value: Int)
    extends Opt with OptReadSpikes with OptInt

  /**Blackout time in frames*/
  case class BlackoutFrames(value: Int) extends OptReadSpikes with OptInt {

    loggerRequire(value>=1, "Must have a blackout frame quantity >=1!")

  }

  // </editor-fold>

}
