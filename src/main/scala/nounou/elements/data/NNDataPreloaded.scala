package nounou.elements.data

import breeze.linalg.{DenseMatrix => DM, DenseVector => DV}
import nounou.elements.traits.{NNTiming, NNScaling}
import nounou.elements.traits.NNTiming
import nounou.ranges.NNRangeValid

/**NNData class with internal representation as data array.
 */
class NNDataPreloaded(val data: Array[DV[Double]], override val timing: NNTiming, scalingInput: NNScaling)
  extends NNData  {

  loggerRequire(data != null, "input data must be non-null")

  setScaling(scalingInput)

  //override val timing = timingInput

  override def getChannelCount: Int = data.length

  override def readPointImpl(channel: Int, frame: Int, segment: Int) =
    data(channel)( timing.cumulativeFrame(frame, segment) )

  override def readTraceDVImpl(channel: Int, rangeFrValid: NNRangeValid) = {
    data( channel )(
      rangeFrValid.toRangeInclusive( timing.segmentStartFrame( rangeFrValid.segment ))
    )

  }

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl(): String = s"channels=$getChannelCount length=${data(0).size}"

  override def toStringFullImpl(): String = ""

  // </editor-fold>

}