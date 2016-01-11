package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV}
import nounou.elements.traits.NNScaling
import nounou.elements.data.NNData
import nounou.ranges.{NNRangeSpecifier, NNRangeValid}
import nounou.elements.NNElement
import nounou.elements.traits.layout.NNLayout

/** A passthrough object, which is inherited by various NNDataFilter
  * objects to create a filter block for the filter chain.
  * @param parenVar the parent data object
  */
abstract class NNDataFilter( private var parenVar: NNData ) extends NNData {

  //Note: this constructor statement will be run before all inheriting child constructor statements!
  setParent(parenVar, true)

  // <editor-fold defaultstate="collapsed" desc=" set/getParent ">

  def setParent(parent: NNData, constructorCall: Boolean = false): Unit = {
    parenVar.clearChild(this)
    parenVar = parent
    parenVar.setChild(this)

    if(constructorCall){
      //Do not run these update functions when constructing class
      changedData()
      changedTiming()
      changedLayout()
    }
  }

  def getParent(): NNData = parenVar

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" setActive/getActive ">

  protected[NNDataFilter] var _active = true

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

  override final def readPoint(channel: Int, frame: Int, segment: Int): Double =
    if(_active){
      super.readPoint(channel, frame, segment)
    }else{
      parenVar.readPoint(channel, frame, segment)
    }

  override final def readTraceDV(channel: Int, range: NNRangeSpecifier): DV[Double] =
    if(_active){
      super.readTraceDV(channel, range)
    }else{
      parenVar.readTraceDV(channel, range)
    }
    // </editor-fold>

//  override def channelNames: scala.Vector[String] = _parent.channelNames
  override def getChannelCount = parenVar.channelCount

  //passthrough implementations to be overridden in real filters
  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double =
    parenVar.readPointImpl(channel, frame, segment: Int)

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DV[Double] =
    parenVar.readTraceDVImpl(channel, range)

  // <editor-fold defaultstate="collapsed" desc=" timing, scale, layout ">

  //passthrough definitions, only override for changes in sampling rate, layout, etc.
  override def timing() = parenVar.getTiming()
  override def scale() =  parenVar.getScale()
  override def layout() = parenVar.getLayout()

//  final override def setTiming( timing: NNDataTiming ) =
//    throw loggerError("Cannot set timing for a data filter manually")
  final override def setScale( scale: NNScaling ) =
    throw loggerError("Cannot set scale for data filter for a data filter manually")
  final override def setLayout( layout: NNLayout ) =
    throw loggerError("Cannot set layout for data filter for a data filter manually")

  // </editor-fold>

  override def isCompatible(target: NNElement) = false

}