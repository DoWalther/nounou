package nounou

import nounou.Options.{BlackoutFrames, WaveformFrames, AlignmentPoint}
import nounou.options._

/**
  * Convenience access class for various option objects in nounou
  *
  * Created by ktakagaki on 16/01/28.
  */
object NNOpt {

  // <editor-fold defaultstate="collapsed" desc=" OptReadSpike ">

  /** At which frame in a spike waveform to record the peak.
    * If this option value is 8, the peak will be at sample number 8.*/
  def AlignmentPoint(value: Int) = new AlignmentPoint(value)

  /**How many frames of data to record in a spike waveform*/
  def WaveformFrames(value: Int) = new WaveformFrames(value)

  /**Blackout time in frames*/
  def BlackoutFrames(value: Int) = new BlackoutFrames(value)

  // </editor-fold>

}
