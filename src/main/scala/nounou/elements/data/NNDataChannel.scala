package nounou.elements.data

import breeze.linalg.{DenseVector => DV}
import nounou._
import nounou.elements.data.traits.{NNTimingElement, NNScalingElement, NNElementCompatibilityCheck}
import nounou.elements.traits.NNTimingElement
import nounou.ranges.{NNRangeSpecifier, NNRangeValid}
import nounou.elements.NNElement

//ToDo 1: make NNDataChannel a subclass of NNData for easier access to single channels
/**
  * This object represents a single channel of data.
  * Most data formats represent an array of data (with multiple channels)
  * as one entity (e.g. Plexon, imaging formats with multiple pixels).
  * However, other neurophysiology formats represent each channel of data
  * independently (e.g. Neuralynx *.ncs). This trait can be used to represent
  * single channels in the latter paradigm.
  *
  * In order to go back and forth between NNData and NNDataChannel, use the following:
  *
  * + [[NNDataChannelArray]]: Bundle multiple NNDataChannel objects (with compatible timing info) into one NNData object
  * + [[NNDataChannelExtracted]]: Extract a single channel from a NNData object and present it as an NNDataChannel
  *
  */
trait NNDataChannel extends NNElement with NNElementCompatibilityCheck with NNTimingElement with NNScalingElement {

  /**MUST OVERRIDE: name of the given channel.*/
  val channelName: String

  /**
    * MUST OVERRIDE: Read a single point from the data.
    */
  def readPointImpl(frame: Int, segment: Int): Double

  /**
    * CAN OVERRIDE: Read a single data trace from the data, in internal integer scaling.
    * Should return a defensive clone.
    */
  def readTraceDVImpl(range: NNRangeValid): DV[Double] = {
    val res = DV.zeros[Double](range.length)
    nounou.util.forJava(range.start, range.last + 1, range.step, (c: Int) => (res(c) = readPointImpl(c, range.segment)))
    res
  }

  //<editor-fold desc="reading a point">

  /** Read a single point from the data, in internal integer scaling, after checking values.
    * Implement via readPointImpl.
    */
  final def readPoint(frame: Int, segment: Int): Double = {
    //require(isValidFr(frame, segment), "Invalid frame/segment: " + (frame, segment).toString)
    if( timing().isValidFrsg(frame, segment) ) readPointImpl(frame, segment) else 0
  }

  //</editor-fold>

  //<editor-fold desc="reading a trace">

  //  /** Read a single trace from the data, in internal integer scaling.
//    */
//  final def readTraceInt(segment: Int): Array[Int] = {
//    val range = NN.NNRangeAll().getValidRange( timing() )
//    readTraceDVImpl(range).toArray
//  }
//  @deprecated
//  final def readTraceInt(range: NNRangeSpecifier): Array[Int] = scale.convertAbsoluteToInt( readTraceDV(range).toArray )

  def readTrace(range: NNRangeSpecifier): Array[Double] = readTraceDV(range).toArray

  /** Read a single trace (within the span) from the data, in internal integer scaling.
    */
  final def readTraceDV(range: NNRangeSpecifier): DV[Double] = {
    val (preLength, seg, postLength) = range.getRangeValidPrePost( timing() )
    DV.vertcat( DV.zeros[Double]( preLength ), readTraceDVImpl(seg), DV.zeros[Double]( postLength ) )
  }

  //</editor-fold>


  //ToDo 1: this will fail when different traces are scaled differently
  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataChannel => {
        timing == timing && scaling == x.scaling
      }
      case _ => false
    }
  }

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl() = s"${timing().segmentCount} segments, fs=${timing().sampleRate}"

  override def toStringFullImpl() = ""

  // </editor-fold>

}




