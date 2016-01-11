package nounou.elements.data

import breeze.linalg.{DenseVector => DV}
import nounou.elements.traits.{NNScaling, NNTiming}
import nounou.ranges.{NNRangeValid, NNRangeSpecifier}

/**
  * The constructor should not be used directly, instead call
  * [[nounou.elements.data.NNData.getNNDataChannel(* NNData.getNNDataChannel(Int)]].
  * This will trigger the constructor for this class in most cases, but for example,
  * NNDataChannelArray will return the originally bundled NNDataChannel objects.
  *
  * @see NNDataChannel
  *
  */
class NNDataChannelExtracted(val parent: NNData, val channel: Int) extends NNDataChannel {

  def getParent(): NNData = parent

  override val channelName: String = parent.channelName(channel)

  override def readPointImpl(frame: Int, segment: Int): Double =
    parent.readPointImpl( channel, frame, segment )

  override def readTraceDVImpl(range: NNRangeValid): DV[Double] =
    parent.readTraceDVImpl( channel, range )

  override def timing(): NNTiming = parent.timing()

  override def scale(): NNScaling = parent.scale()

}
