package nounou.options

import nounou.elements.spikes.OptReadSpikes

/**
  * Created by ktakagaki on 16/03/13.
  */

trait OptAlignmentPoint extends OptReadSpikes
case class OptAlignmentPointInt(override val value: Int)/*(override implicit val tpe: TypeTag[OptAlignmentPoint])*/
  extends OptInt(value) with OptAlignmentPoint {

  loggerRequire(value >=0, "Must have a pre-trigger alignment point >=0!")

}
/**How many frames of data to record in a spike waveform*/
trait OptWaveformFrames extends OptReadSpikes
case class OptWaveformFramesInt(override val value: Int)/*(override implicit val tpe: TypeTag[WaveformFrames])*/
  extends OptInt(value) with OptWaveformFrames {
  loggerRequire(value >=16, "Must have a waveform frame count of at least 16!")
}
trait OptOverlapWindow extends OptReadSpikes
case class OptOverlapWindowInt(override val value: Int)/*(override implicit val tpe: TypeTag[OverlapWindow])*/
  extends OptInt(value) with OptOverlapWindow

/**Blackout time in frames*/
trait OptBlackoutFrames extends OptReadSpikes
case class OptBlackoutFramesInt(override val value: Int)/*(override implicit val tpe: TypeTag[OptBlackoutFrames])*/
  extends OptInt(value) with OptBlackoutFrames {
  loggerRequire(value >= 1, "Must have a blackout frame quantity >=1!")
}
