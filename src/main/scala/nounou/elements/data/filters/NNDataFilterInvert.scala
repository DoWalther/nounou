package nounou.elements.data.filters

import breeze.linalg.DenseVector
import nounou.elements.data.NNData
import nounou.ranges.NNRangeValid

/**
 * @author ktakagaki
 * //@date 04/16/2014.
 */
class NNDataFilterInvert(private var _parent: NNData ) extends NNDataFilter( _parent ) {

  def this(upstream: NNData, inverted: Boolean) = {
    this(upstream)
    setInverted(inverted)
  }

  var inverted = true
  def setInverted(trueFalse: Boolean) = { inverted = trueFalse }
  def setInverted(trueFalse: Int) = trueFalse match {
    case 1 => inverted = false
    case -1 => inverted = true
    case _ => throw loggerError("argument for setInverted() must be 1 or -1")
  }
  def setInverted(trueFalse: Double) = trueFalse match {
    case 1d => inverted = false
    case -1d => inverted = true
    case _ => throw loggerError("argument for setInverted() must be 1 or -1")
  }

  override def toStringImpl() = {
    if(inverted) "on/inverted, "
    else "off/not inverted, "
  }
  override def toStringFullImpl() = ""

  override def readPointIntImpl(channel: Int, frame: Int, segment: Int): Int =
    if(inverted){
      - _parent.readPointIntImpl(channel, frame, segment)
    } else {
      _parent.readPointIntImpl(channel, frame, segment)
    }

  override def readTraceIntDVImpl(channel: Int, range: NNRangeValid): DenseVector[Int] =
    if(inverted){
      - _parent.readTraceIntDVImpl(channel, range)
    } else {
      _parent.readTraceIntDVImpl(channel, range)
    }

}

