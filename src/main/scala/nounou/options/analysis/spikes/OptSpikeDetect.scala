package nounou.options

/**
  * Created by ktakagaki on 16/03/13.
  */
trait OptSpikeDetect extends Opt

// <editor-fold defaultstate="collapsed" desc=" OptSpikeDetect ">

trait OptSpikeDetectMethod extends OptSpikeDetect
case class OptSpikeDetectMethodString(override val value: String)/*(override implicit val tpe: TypeTag[OptSpikeDetectMethod])*/
  extends OptString(value) with OptSpikeDetectMethod

trait OptMedianFactor extends OptSpikeDetect
case class OptMedianFactorDouble(override val value: Double)/*(override implicit val tpe: TypeTag[OptMedianFactorDouble])*/
  extends OptDouble(value) with OptMedianFactor

trait OptPeakWindow extends OptSpikeDetect
case class OptPeakWindowInt(override val value: Int)/*(override implicit val tpe: TypeTag[OptPeakWindowInt])*/
  extends OptInt(value) with OptPeakWindow

//  case class SpikeDetectMethod(value: Marker) extends OptMarker
//  case object MedianSDThresholdPeak extends Marker("MedianSDThresholdPeak")
//case object MedianSDThresholdPeak extends OptMarker("MedianSDThresholdPeak") with OptSpikeDetect
//case object MedianSDThresholdPeak extends OptString("MedianSDThresholdPeak") with OptSpikeDetect
//case class MedianFactor(valueDouble: Double) (override implicit val tpe: TypeTag[OptMedianFactorDouble])
//  extends OptSpikeDetect with OptDouble[OptMedianFactorDouble]
//case class PeakWindow(valueInt: Int)(override implicit val tpe: TypeTag[PeakWindow])
//  extends OptSpikeDetect with OptInt[PeakWindow]

// </editor-fold>


@deprecated
trait OptAnalysisUnits extends Opt

/**How many SDs away to take as the spike threshold?*/
trait OptThresholdSDFactor extends OptSpikeDetect
case class OptThresholdSDFactorDouble(override val value: Double)
  extends OptDouble(value) with OptThresholdSDFactor {
    loggerRequire( value>=1, "Must have a threshold SD factor bigger than 1!")
}

/**Detection window*/
trait OptDetectionWindow extends OptSpikeDetect
case class OptDetectionWindowInt(override val value: Int)
  extends OptInt(value) with OptDetectionWindow

/**Detection window overlap*/
trait OptDetectionWindowOverlap extends OptSpikeDetect
case class OptDetectionWindowOverlapInt(override val value: Int)
  extends OptInt(value) with OptDetectionWindowOverlap

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
