package nounou.analysis

import nounou.Opt
import nounou.analysis.spikes.SpikeDetect._

/**
 * @author ktakagaki
 */
package object spikes {

  abstract class OptAnalysisUnits extends Opt


  /**How many frames of data to record in a spike waveform before the peak.
    * If this option value is 8, the peak will be at sample number 9.*/
  case class OptPrepeakFr(frames: Int) extends OptAnalysisUnits {
    loggerRequire(frames>=0, "Must have a non-negative prepeak frame count!")
  }
  /**How many frames of data to record in a spike waveform*/
  case class OptWaveformFr(frames: Int) extends OptAnalysisUnits {
    loggerRequire(frames>=0, "Must have a non-negative frame count!")
  }
  /**Blackout time in frames*/
  case class OptBlackoutFr(frames: Int) extends OptAnalysisUnits{
    loggerRequire(frames>=0, "Must have a non-negative blackout frame quantity!")

  }
  /**How many SDs away to take as the spike threshold?*/
  case class OptThresholdSDFactor(factor: Double) extends OptAnalysisUnits{
    loggerRequire(factor>=1, "Must have a threshold SD factor bigger than 1!")

  }

  /**Detection window*/
  case class OptDetectionWindow(frames: Int) extends OptAnalysisUnits

  /**Detection window overlap*/
  case class OptDetectionWindowOverlap(frames: Int) extends OptAnalysisUnits

//  object OptBlackout {
//    abstract class tpe
//    case object Frames
//    case object Ms
//  }
//  case class OptBlackout(quantity: Double, tpe: OptBlackout.tpe) extends OptAnalysisUnits

  case class OptPeakHalfWidthMaxFr(frames: Int) extends OptAnalysisUnits
  case class OptPeakHalfWidthMinFr(frames: Int) extends OptAnalysisUnits
//  case class OptTraceSDReadLengthFr(frames: Int) extends OptAnalysisUnits

}
