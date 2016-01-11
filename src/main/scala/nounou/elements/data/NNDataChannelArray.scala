package nounou.elements.data

import nounou.elements.NNElement
import nounou.ranges.NNRangeValid

/**
  * Immutable data object to encapsulate arrays of [[NNDataChannel]] objects
  *
  * @see NNDataChannel
  */
 class NNDataChannelArray(val array: Seq[NNDataChannel]) extends NNData {

  def this( dataChannel: NNDataChannel ) = this( Array(dataChannel).toVector )
  def this( array: Array[NNDataChannel] ) = this( array.toVector )

  //enforce channel compatibility
  loggerRequire( array != null && array.length > 0, "input Vector must be non-negative, non-empty" )
  loggerRequire( array.length == 1 || array(0).isCompatible( array.tail ), "input Array must have compatible components")

  override val timing = array(0).timing
//  setTiming( array(0).timing() )
  setScale( array(0).scale() )

  override def toStringImpl() = s"${timing.segmentCount} segments, fs=${timing.sampleRate},  "
  override def toStringFullImpl() = ""

  def apply(channel: Int) = array(channel)

  override def readPointImpl(channel: Int, frame: Int, segment: Int) =
    array(channel).readPointImpl(frame, segment)

  override def readTraceDVImpl(channel: Int, range: NNRangeValid) =
    array(channel).readTraceDVImpl(range)

  def loadDataChannel(dataChannel: NNDataChannel): NNDataChannelArray = {
    if(array(0).isCompatible(dataChannel)){
      new NNDataChannelArray( array :+ dataChannel )
    } else {
      sys.error("New data channel "+dataChannel+" is incompatible with the prior channels. Ignoring loadDataChannel()!")
      this
    }
  }

  // <editor-fold defaultstate="collapsed" desc=" getNNDataChannel ">

  override def getNNDataChannel(channel: Int): NNDataChannel = array(channel)

  // </editor-fold>

  override def isCompatible(that: NNElement): Boolean = {
    that match {
        //ToDo 3: is this advisable?
      case x: NNDataChannel => this(0).isCompatible(x)
      case x: NNDataChannelArray => this(0).isCompatible(x(0))
      case _ => false
    }
  }

  // </editor-fold>

  override def getChannelCount: Int = array.length

}


// <editor-fold desc="XConcatenatable">

//   override def :::(that: NNElement): NNDataChannelArray = {
//    that match {
//      case t: NNDataChannelArray => {
//        if(this.isCompatible(t)){
//          val oriThis = this
//          new NNDataChannelArray(oriThis.array ++ t.array)
//        } else {
//          throw new IllegalArgumentException("the two XDataChannelArray types are not compatible, and cannot be concatenated.")
//        }
//      }
//      case t: NNDataChannel => {
//        if(this(0).isCompatible(t)){
//          new NNDataChannelArray( this.array :+ t)
//        } else {
//          throw new IllegalArgumentException("the XDataChannelArray type and XDataChannel type are not compatible, and cannot be concatenated.")
//        }
//      }
//      case _ => throw new IllegalArgumentException("the two X types are not compatible, and cannot be concatenated.")
//    }
//  }

