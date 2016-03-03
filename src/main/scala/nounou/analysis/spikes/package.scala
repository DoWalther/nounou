package nounou.analysis

import nounou.options.Opt

/**
 * @author ktakagaki
 */
package object spikes {

  @deprecated
  abstract class OptAnalysisUnits extends Opt


  /**How many SDs away to take as the spike threshold?*/
  case class OptThresholdSDFactor(factor: Double) extends OptAnalysisUnits{
    loggerRequire(factor>=1, "Must have a threshold SD factor bigger than 1!")
//    override val name: String = "ThresholdSDFactor"
//    override val shortDescription: String = "Threshold SD median factor. "
//    override val longDescriptionImpl: String = "Must be bigger than 1."
  }

  /**Detection window*/
  case class OptDetectionWindow(frames: Int) extends OptAnalysisUnits{
//    override val name: String = "DetectionWindow"
//    override val shortDescription: String = "."
//    override val longDescriptionImpl: String = "."
  }

  /**Detection window overlap*/
  case class OptDetectionWindowOverlap(frames: Int) extends OptAnalysisUnits{
//    override val name: String = "DetectionWindowOverlap"
//    override val shortDescription: String = "."
//    override val longDescriptionImpl: String = "."
  }

//  object OptBlackout {
//    abstract class tpe
//    case object Frames
//    case object Ms
//  }
//  case class OptBlackout(quantity: Double, tpe: OptBlackout.tpe) extends OptAnalysisUnits

  case class OptPeakHalfWidthMaxFr(frames: Int) extends OptAnalysisUnits{
//    override val name: String = "PeakHalfWidthMaxFr"
//    override val shortDescription: String = "."
//    override val longDescriptionImpl: String = "."
  }
  case class OptPeakHalfWidthMinFr(frames: Int) extends OptAnalysisUnits{
//    override val name: String = "PeakHalfWidthMinFr"
//    override val shortDescription: String = "."
//    override val longDescriptionImpl: String = "."
  }
//  case class OptTraceSDReadLengthFr(frames: Int) extends OptAnalysisUnits

}
