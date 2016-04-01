package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV}
import nounou.elements.traits.NNScaling
import nounou.elements.layout.NNLayout
import nounou.elements.data.NNData
import nounou.ranges.{NNRangeSpecifier, NNRangeValid}
import nounou.elements.NNElement

/** A passthrough object, which is inherited by various NNDataFilter
  * objects to create a filter block for the filter chain.
 *
  * @param parentVar the parent data object
  */
abstract class NNFilter(private var parentVar: NNData ) extends NNData {

  //Note: this constructor statement will be run before all inheriting child constructor statements!
  setParent(parentVar, true)

  // <editor-fold defaultstate="collapsed" desc=" set/getParent ">

  def setParent(parent: NNData, constructorCall: Boolean = false): Unit = {
    parentVar.clearChild(this)
    parentVar = parent
    parentVar.setChild(this)

    if(constructorCall){
      //Do not run these update functions when constructing class
      changedData()
      changedTiming()
      changedLayout()
    }
  }

  def getParent(): NNData = parentVar

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" setActive/getActive ">

  protected[NNFilter] var _active = true

  /** Sets whether the filter is active or not. When not active, just passes
    * data through unchanged.
    */
  final def setActive(active: Boolean) = if(_active != active){
    _active = active
    changedData()
  }

  /** See [[nounou.elements.data.filters.NNFilter.setActive]]
    */
  def getActive() = _active

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" adjust reading functions for active state ">

  override final def readPoint(channel: Int, frame: Int, segment: Int): Double =
    if(_active){
      super.readPoint(channel, frame, segment)
    }else{
      parentVar.readPoint(channel, frame, segment)
    }

  override final def readTraceDV(channel: Int, range: NNRangeSpecifier): DV[Double] =
    if(_active){
      super.readTraceDV(channel, range)
    }else{
      parentVar.readTraceDV(channel, range)
    }
    // </editor-fold>

//  override def channelNames: scala.Vector[String] = _parent.channelNames
  override def getChannelCount = parentVar.channelCount

  //passthrough implementations to be overridden in real filters
  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double =
    parentVar.readPointImpl(channel, frame, segment: Int)

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DV[Double] =
    parentVar.readTraceDVImpl(channel, range)

  // <editor-fold defaultstate="collapsed" desc=" timing, scale, layout ">

  //passthrough definitions, only override for changes in sampling rate, layout, etc.
  override def timing() = parentVar.getTiming()
  override def scaling() =  parentVar.getScale()
  override def layout() = parentVar.getLayout()

//  final override def setTiming( timing: NNDataTiming ) =
//    throw loggerError("Cannot set timing for a data filter manually")
  final override def setScaling(scale: NNScaling ) =
    throw loggerError("Cannot set scale for data filter for a data filter manually")
  final override def setLayout( layout: NNLayout ) =
    throw loggerError("Cannot set layout for data filter for a data filter manually")

  // </editor-fold>

  override def isCompatible(target: NNElement) = false

}