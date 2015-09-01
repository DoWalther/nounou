package nounou.elements.data.filters

import nounou._
import nounou.elements.{NNDataScale, NNDataTiming, NNElement}
import nounou.elements.data.NNData
import nounou.elements.ranges.{SampleRangeSpecifier, SampleRangeValid}
import nounou.elements.layouts.NNDataLayout
import breeze.linalg.{DenseVector => DV}

/** A passthrough object, which is inherited by various NNDataFilter
  * objects to create a filter block for the filter chain.
  * @param parenVar the parent data object
  */
abstract class NNDataFilter( private var parenVar: NNData ) extends NNData {

  setParent(parenVar)

  // <editor-fold defaultstate="collapsed" desc=" set/getParent ">

  def setParent(parent: NNData): Unit = {
    parenVar.clearChild(this)
    parenVar = parent
    parenVar.setChild(this)
    changedData()
    changedTiming()
    changedLayout()
  }

  def getParent(): NNData = parenVar

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" setActive/getActive ">

  private var _active = true

  /** Sets whether the filter is active or not. When not active, just passes
    * data through unchanged.
    */
  final def setActive(active: Boolean) = if(_active != active){
    _active = active
    changedData()
  }

  /** See [[nounou.elements.data.filters.NNDataFilter.setActive]]
    */
  def getActive() = _active

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" adjust reading functions for active state ">

  override final def readPoint(channel: Int, frame: Int, segment: Int): Int = if(_active){
    super.readPoint(channel, frame, segment)
  }else{
    parenVar.readPoint(channel, frame, segment)
  }

  override final def readTraceDV(channel: Int, range: SampleRangeSpecifier): DV[Int] =
    if(_active){
      super.readTraceDV(channel, range)
    }else{
      parenVar.readTraceDV(channel, range)
    }
    // </editor-fold>

//  override def channelNames: scala.Vector[String] = _parent.channelNames
  override def getChannelCount = parenVar.channelCount

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Int = parenVar.readPointImpl(channel, frame, segment: Int)
  override def readTraceDVImpl(channel: Int, range: SampleRangeValid): DV[Int] = parenVar.readTraceDVImpl(channel, range)
//  override def readFrameImpl(frame: Int): DV[Int] = _parent.readFrameImpl(frame)
//  override def readFrameImpl(frame: Int, channels: Array[Int]): DV[Int] = _parent.readFrameImpl(frame, channels)

  // <editor-fold defaultstate="collapsed" desc=" timing, scale, layout ">

  override def getTiming() = parenVar.getTiming()
  override def getScale() = parenVar.getScale()
  override def getLayout() = parenVar.getLayout()

  override def setTiming( timing: NNDataTiming ) =
    throw loggerError(s"Cannot set timing for data filter ${this.getClass.getCanonicalName}.")
  override def setScale( scale: NNDataScale ) =
    throw loggerError(s"Cannot set scale for data filter ${this.getClass.getCanonicalName}.")
  override def setLayout( layout: NNDataLayout ) =
    throw loggerError(s"Cannot set layout for data filter ${this.getClass.getCanonicalName}.")

  // </editor-fold>

  override def isCompatible(target: NNElement) = false

}