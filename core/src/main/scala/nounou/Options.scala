package nounou

import nounou.analysis.spikes.OptSpikeDetect
import nounou.elements.spikes.OptReadSpikes
import nounou.options.{OptInt, Opt}

/**
  * Created by ktakagaki on 16/01/28.
  */
object Options {


  // <editor-fold defaultstate="collapsed" desc=" OptSpikeDetect ">

  case object MedianSDThresholdPeak extends OptSpikeDetect
  case class MedianFactor(value: Double) extends OptSpikeDetect
  case class PeakWindow(value: Double) extends OptSpikeDetect

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
  /**Blackout time in frames*/
  case class BlackoutFrames(value: Int) extends OptReadSpikes with OptInt {

    loggerRequire(value>=1, "Must have a blackout frame quantity >=1!")

  }

  // </editor-fold>

}
