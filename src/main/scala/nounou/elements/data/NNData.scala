package nounou.elements.data

import breeze.linalg.{DenseVector => DV}
import nounou._
import nounou.elements.NNElement
import nounou.elements.data.traits.NNDataNode
import nounou.elements.traits.layout.{NNLayoutElement, NNLayout}
import nounou.elements.traits.{NNConcatenableElement, NNTimingElement, NNScalingElement, NNChannelsElement}
import nounou.ranges.{NNRange, NNRangeSpecifier, NNRangeValid}

/** Base trait for data encoded as Int arrays, this is the main data element for an experiment,
  * whether it be electrophysiolgical or high-sampling-rate imaging.
  *
  * This object is mutable, to allow inheritance by [[nounou.elements.data.filters.NNFilter]].
  * For that class, output results may change, depending upon _parent changes.
  * Each trace of data must share the following variables:
  * sampling, start, length, xBits, absGain, absOffset, absUnit
  *
  * @see NNDataChannel on how to extract single channels and present an NNData object as a NNDataChannel object
  *
  */
trait NNData extends NNDataNode
  with NNConcatenableElement
  with NNChannelsElement
  with NNLayoutElement
  with NNScalingElement
  with NNTimingElement
  with NNDataSpikeReader {

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl(): String// = s"${channelCount} ch, ${timing().segmentCount} seg, fs=${timing().sampleRate}, "

  override final def toStringFull() = super.toStringFull() + "\n" + timing.toString() + "\n\n" + toStringChain(0)

  override def toStringFullImpl(): String

  // </editor-fold>

  /**
    *  '''__MUST OVERRIDE__''' Read a single point from the data, in internal integer scaling.
    * Assumes that channel, frame, and segment are all valid and within range.
    *
    */
  def readPointImpl(channel: Int, frame: Int, segment: Int): Double

  //<editor-fold defaultstate="collapsed" desc="reading a point">

  /**
    * Only intended to be overridden by [[nounou.elements.data.filters.NNFilter]]
    */
  def readPoint(channel: Int, frame: Int, segment: Int): Double = {

    val realSegment = timing.getRealSegment(segment)
    loggerRequire( timing.isRealisticFrsg(frame, realSegment), s"Unrealistic frame/segment: ${frame}/${segment})" )
    loggerRequire(isValidChannel(channel), s"Invalid channel: " + channel.toString)

    if( timing.isValidFrsg(frame, realSegment) ) readPointImpl(channel, frame, realSegment)
    else 0
  }
  //scale.convertIntToAbsolute( readPointInt(channel, frame, segment) )

  /**
    * [[nounou.elements.data.NNData.readPoint(channel:Int,frame:Int,segm* readPoint]]
    * but assuming default segment (only allowed for data types with just one segment).
    *
    */
  final def readPoint(channel: Int, frame: Int): Double = readPoint(channel, frame, -1)

//  /** Read a single point from the data, in internal integer scaling, after checking values.
//    * Implement via readPointImpl. Prefer
//    * [[nounou.elements.data.NNData.readTraceDV(channel:Int,range* readTraceDV]]
//    * and readFrame()
//    * when possible, as these will avoid repeated function calling overhead.
//    */
//  @deprecated
//  final def readPointInt(channel: Int, frame: Int, segment: Int): Int =
//    scale.convertAbsoluteToInt( readPoint(channel, frame, segment) )
//
//  @deprecated
//  final def readPointInt(channel: Int, frame: Int): Int = readPointInt(channel, frame, -1)
//
//  /** '''__MUST OVERRIDE__''' Read a single point from the data, in internal integer scaling.
//    * Assumes that channel, frame, and segment are all valid and within range.
//    */
//  @deprecated
//  final def readPointIntImpl(channel: Int, frame: Int, segment: Int): Int =
//    scale.convertAbsoluteToInt( readPointImpl(channel, frame, segment) )

  // </editor-fold>


  /**
    * Read a single trace from the data.
    * Should return a defensive clone.
    * Returns overhangs if data is not covered.
    * Only intended to be overridden by [[nounou.elements.data.filters.NNFilter]]
    */
  def readTraceDV(channel: Int, range: NNRangeSpecifier): DV[Double] = {

    loggerRequire(isValidChannel(channel), "Invalid channel: " + channel.toString)

    range match {
      case ran: NNRangeValid => readTraceDVImpl(channel, ran)
      case _ => {
        loggerRequire(timing.isRealisticRange(range), "Unrealistic frame/segment: " + range.toString)
        val preValidPost = range.getRangeValidPrePost(this)
        readTraceDVPVPImpl(channel, preValidPost)
      }
    }

  }

  /** '''__CAN OVERRIDE__'''  Read a single data trace from the data.
    * Internal implementation for [[nounou.elements.data.NNData.readTraceDV(channel:Int, ran* readTraceDV]]
    * to return a breeze.linalg.DenseVector.
    * Should return a defensive clone. Assumes that channel and range are within the data range!
    */
  def readTraceDVImpl(channel: Int, rangeFrValid: NNRangeValid): DV[Double] = {
    val res = DV.zeros[Double]( rangeFrValid.length )
    nounou.util.forJava(rangeFrValid.start, rangeFrValid.last + 1, rangeFrValid.step, (c: Int) => (res(c) = readPointImpl(channel, c, rangeFrValid.segment)))
    res
  }

  /**
    */

  //<editor-fold defaultstate="collapsed" desc=" readTraceIntDV deprecated ">

//  /** '''__CAN OVERRIDE__''' Read a single trace from the data, in internal integer scaling.
//    */
//  @deprecated
//  def readTraceIntDV(channel: Int, range: NNRangeSpecifier): DV[Int] = {
//
//    loggerRequire(isValidChannel(channel), "Invalid channel: " + channel.toString)
//
//    range match {
//      case ran: NNRangeValid => readTraceIntDVImpl(channel, ran)
//      case _ => {
//        loggerRequire(timing.isRealisticRange(range), "Unrealistic frame/segment: " + range.toString)
//        val preValidPost = range.getRangeValidPrePost(this)
//        readTraceIntDVPVPImpl(channel, preValidPost)
//      }
//    }
//
//  }

//  /** '''__CAN OVERRIDE__''' by default, calls
//    * [[nounou.elements.data.NNData.readTraceIntDV(channel:Int,range* readTraceDV ]]
//    */
//  @deprecated
//  def readTraceIntDV(channels: Array[Int], range: NNRangeSpecifier): Array[DV[Int]] = {
//    val preValidPost = range.getRangeValidPrePost(this)
//    channels.map( readTraceIntDVPVPImpl(_, preValidPost) )
//  }

//  @deprecated
//  private final def readTraceIntDVPVPImpl(channel: Int, preValidPost: (Int, NNRangeValid, Int)): DV[Int] = {
//    val validTrace = readTraceIntDVImpl(channel, preValidPost._2)
//    preValidPost match {
//      case (0, rfv, 0) => validTrace
//      case (pre, rfv, 0) => DV.vertcat(DV.zeros[Int](pre), validTrace)
//      case (0, rfv, post) => DV.vertcat(validTrace, DV.zeros[Int](post))
//      case (pre, rfv, post) => DV.vertcat(DV.zeros[Int](pre), validTrace, DV.zeros[Int](post))
//    }
//  }

//  @deprecated
//  protected[nounou] final def readTraceIntDV(channel: Int): DV[Int] =
//    scale().convertAbsoluteToInt( readTraceDV(channel, NN.NNRangeAll()) )

  // </editor-fold>

  private final def readTraceDVPVPImpl(channel: Int, preValidPost: (Int, NNRangeValid, Int)): DV[Double] = {
    val validTrace = readTraceDVImpl(channel, preValidPost._2)
    preValidPost match {
      case (0, rfv, 0) => validTrace
      case (pre, rfv, 0) => DV.vertcat(DV.zeros[Double](pre), validTrace)
      case (0, rfv, post) => DV.vertcat(validTrace, DV.zeros[Double](post))
      case (pre, rfv, post) => DV.vertcat(DV.zeros[Double](pre), validTrace, DV.zeros[Double](post))
    }
  }


  // <editor-fold defaultstate="collapsed" desc=" convenience readTrace variations ">


  // <editor-fold defaultstate="collapsed" desc=" readTraceInt (deprecated) ">

//  /** Read a single trace (whole range) in internal integer scaling.
//    * This should only be used for data with only 1 segment.
//    */
//  @deprecated
//  protected[nounou] final def readTraceInt(channel: Int) =
//                          readTraceIntDV(channel).toArray
//  @deprecated
//  protected[nounou] final def readTraceInt(channel: Int, range: NNRangeSpecifier) =
//                          readTraceIntDV(channel, range).toArray
//  @deprecated
//  protected[nounou] final def readTraceInt(channels: Array[Int], range: NNRangeSpecifier): Array[Array[Int]] =
//                          readTraceIntDV(channels, range).map(_.toArray)
//  @deprecated
//  protected[nounou] final def readTraceInt(channel: Int, range: Array[Int]): Array[Int] =
//                          readTraceInt(channel, NN.NNRange(range))
//  @deprecated
//  protected[nounou] final def readTraceInt(channel: Int, range: Array[Int], segment: Int): Array[Int] =
//                          readTraceInt(channel, NN.NNRange(range, segment))

  // <editor-fold defaultstate="collapsed" desc=" readTrace ">

  /** Read a single trace in absolute unit scaling (as recorded).
    * This should only be used for data with only 1 segment.
    */
  final def readTrace(channel: Int): Array[Double] =
      readTraceDV( channel, NN.NNRangeAll() ).toArray

  final def readTrace(channel: Int, range: NNRangeSpecifier): Array[Double] =
      readTraceDV( channel, range ).toArray

  final def readTrace(channels: Array[Int], range: NNRangeSpecifier): Array[Array[Double]] =
    channels.map( readTraceDV( _, range ).toArray )

  final def readTrace(channel: Int, range: Array[Int], segment: Int): Array[Double] =
      readTrace(channel, NNRange.convertArrayToSampleRange(range, segment) )

  final def readTrace(channel: Int, range: Array[Int]): Array[Double] =
      readTrace(channel, NNRange.convertArrayToSampleRange(range, -1) )

  //</editor-fold>


//  //<editor-fold defaultstate="collapsed" desc="reading a frame">
//
//  /** Read a single frame from the data, in internal integer scaling, for just the specified channels.
//    */
//  final def readFrame(frame: Int, channels: Array[Int], segment: Int): DV[Int] = {
//    loggerRequire(isRealisticFr(frame/*, segment*/), "Unrealistic frame/segment: " + (frame, segment).toString)
//    loggerRequire(channels.forall(isValidChannel), "Invalid channels: " + channels.toString)
//
//    if( isValidFr(frame/*, segment*/) ) readFrameImpl(frame, channels/*, segment*//*(currentSegment = segment)*/ ) else DV.zeros[Int]( channels.length )
//
//  }
//
//  /** Read a single frame from the data, in internal integer scaling.
//    */
//  final def readFrame(frame: Int, segment: Int): DV[Int] = {
//    loggerRequire(isRealisticFr(frame, segment), "Unrealistic frame/segment: " + (frame, segment).toString)
//    if( isValidFr(frame, segment) ) readFrameImpl(frame , segment ((currentSegment = segment)*/ ) else DV.zeros[Int]( channelCount )
//  }
////  final def readFrame(frame: Int): DV[Int] = readFrame(frame, 0)
//
//
//  /** CAN OVERRIDE: Read a single frame from the data, in internal integer scaling.
//    * Should return a defensive clone. Assumes that frame is within the data range!
//    */
//  def readFrameImpl(frame: Int): DV[Int] = {
//    val res = DV.zeros[Int](channelCount)
//    nounou.util.forJava(0, channelCount, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame))
//    res
//  }
//  /** CAN OVERRIDE: Read a single frame from the data, for just the specified channels, in internal integer scaling.
//    * Should return a defensive clone. Assumes that frame and channels are within the data range!
//    */
//  def readFrameImpl(frame: Int, channels: Array[Int]): DV[Int] = {
//    val res = DV.zeros[Int]( channels.length )
//    nounou.util.forJava(0, channels.length, 1, (channel: Int) => res(channel) = readPointImpl(channel, frame))
//    res
//  }
//
  //  //</editor-fold>

  //  // </editor-fold>

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" getNNDataChannel ">

  def getNNDataChannel(channel: Int): NNDataChannel = {
    new NNDataChannelExtracted( this, channel )
  }

  // </editor-fold>

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNData => {
        ( //This is not enforced, may append objects with separate channel counts: channelCount() == x.channelCount()  &&
          timing() == timing() &&
          scale() == x.scale() )
        //&& this.layout.isCompatible(x.layout)
        //not channel info
      }
      case _ => false
    }
  }

//  override def :::(x: NNElement): NNData

}



//  // <editor-fold defaultstate="collapsed" desc=" NNLayout, channel count ">
//
//  private var varLayout: NNLayout = null
//  def layout(): NNLayout = varLayout
//  final def getLayout(): NNLayout = layout()
//  def setLayout(layout: NNLayout): Unit = {
//    loggerRequire( layout.channelCount == this.channelCount(),
//      s"Channel count ${layout.channelCount} of new layout does not match channel count ${this.channelCount()} for ${this.getClass.toString}" )
//  }
//
//  // </editor-fold>



////<editor-fold defaultstate="collapsed" desc="reading a point">
//
///** Read a single point from the data, in internal integer scaling, after checking values.
//  * Implement via readPointImpl. Prefer
//  * [[nounou.elements.data.NNData.readTraceIntDV(channel:Int,range* readTraceDV]]
//  * and readFrame()
//  * when possible, as these will avoid repeated function calling overhead.
//  */
//def readPointInt(channel: Int, frame: Int, segment: Int /*optSegment: OptSegment*/): Int = {
//val realSegment = timing.getRealSegment(segment)
//loggerRequire( timing.isRealisticFrsg(frame, realSegment), s"Unrealistic frame/segment: ${frame}/${segment})" )
//loggerRequire(isValidChannel(channel), s"Invalid channel: " + channel.toString)
//
//if( timing.isValidFrsg(frame, realSegment) ) readPointIntImpl(channel, frame, realSegment)
//else 0
//}
//
//final def readPointInt(channel: Int, frame: Int): Int = readPointInt(channel, frame, -1)
//
//// <editor-fold defaultstate="collapsed" desc=" convenience readPoint variations ">
//
///** [[nounou.elements.data.NNData.readPointInt(channel:Int,frame:Int,segment:Int)* readPoint]] but in physical units.
//  */
//final def readPoint(channel: Int, frame: Int, segment: Int): Double = scale.convertIntToAbsolute( readPointInt(channel, frame, segment) )
//
///** [[nounou.elements.data.NNData.readPointInt(channel:Int,frame:Int)* readPoint]]
//  * but in physical units.
//  */
//final def readPoint(channel: Int, frame: Int): Double = readPoint(channel, frame, -1)//OptSegmentAutomatic)
//
//// </editor-fold>
//
////</editor-fold>


////  final def readTraceDV(channel: Int): DV[Double]
////    = scale.convertIntToAbsolute(readTraceIntDV(channel))
//  /** Read a single trace in absolute unit scaling (as recorded).*/
//  protected[nounou] final def readTraceDV(channel: Int, range: NNRangeSpecifier = NN.NNRangeAll()): DV[Double]
//    = scale.convertIntToAbsolute(readTraceIntDV(channel, range))
//  /** Read multiple traces in absolute unit scaling (as recorded).*/
//  protected[nounou] final def readTraceDV(channels: Array[Int], range: NNRangeSpecifier): Array[DV[Double]]
//    = readTraceIntDV(channels, range).map( scale.convertIntToAbsolute(_) )

//  def readTraceIntDVImpl(channel: Int, rangeFrValid: NNRangeValid): DV[Int] = {
//    val res = DV.zeros[Int]( rangeFrValid.length )
//    nounou.util.forJava(rangeFrValid.start, rangeFrValid.last + 1, rangeFrValid.step, (c: Int) => (res(c) = readPointInt(channel, c, rangeFrValid.segment)))
//    res
//  }
