package nounou.options

import nounou.analysis.spikes.OptSpikeDetect
import nounou.elements.spikes.OptReadSpikes

/**
  * Created by ktakagaki on 16/01/28.
  */
object Options {


  // <editor-fold defaultstate="collapsed" desc=" OptSpikeDetect ">

  case class SpikeDetectMethod(override val valueString: String) extends OptString
//  case class SpikeDetectMethod(value: Marker) extends OptMarker
//  case object MedianSDThresholdPeak extends Marker("MedianSDThresholdPeak")
  //case object MedianSDThresholdPeak extends OptMarker("MedianSDThresholdPeak") with OptSpikeDetect
  //case object MedianSDThresholdPeak extends OptString("MedianSDThresholdPeak") with OptSpikeDetect
  case class MedianFactor(valueDouble: Double) extends OptSpikeDetect with OptDouble
  case class PeakWindow(valueInt: Int) extends OptSpikeDetect with OptInt

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" OptReadSpikes ">

  case class AlignmentPoint(valueInt: Int) extends Opt with OptReadSpikes with OptInt {

    loggerRequire(valueInt >=0, "Must have a pre-trigger alignment point >=0!")

  }
  /**How many frames of data to record in a spike waveform*/
  case class WaveformFrames(valueInt: Int)
    extends Opt with OptReadSpikes with OptInt {

    loggerRequire(valueInt >=16, "Must have a waveform frame count of at least 16!")

  }
  case class OverlapWindow(valueInt: Int)
    extends Opt with OptReadSpikes with OptInt

  /**Blackout time in frames*/
  case class BlackoutFrames(valueInt: Int) extends OptReadSpikes with OptInt {

    loggerRequire(valueInt>=1, "Must have a blackout frame quantity >=1!")

  }

  // </editor-fold>

}
