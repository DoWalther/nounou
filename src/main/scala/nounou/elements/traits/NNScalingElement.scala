package nounou.elements.traits

import nounou.elements.NNElement

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNScalingElement extends NNElement {

  private var _scale: NNScaling = null //  NNDataScale.raw

  /**'''[NNDataScaleElement]''' Get physical scaling information for data.
    * This is not made final, because it will be overriden by some filters which pass through
    * upstream scale information.
    */
  def scale(): NNScaling = {
    if( _scale == null ) throw loggerError(s"Scale is null!")
    else _scale
  }

  /**'''[NNDataScaleElement]''' Java alias for [[nounou.elements.traits.NNScalingElement.scale]].*/
  final def getScale(): NNScaling = scale()

  /**'''[NNDataScaleElement]''' Set physical scaling information for data.*/
  def setScale(scale: NNScaling) = {
    _scale = scale

    //ToDo 2: child change hierarchy in NNElement
    logger.trace("child hierarchy update has not been implemented yet!")
  }

  //ToDo must look up super hierarchy to implement?
//  override def isCompatible(x: NNElement) = x match {
//  override def equals(x: Any) = x match {
//    case x: NNScalingElement => x.getScale().isCompatible(this.getScale())
//    //case x: NNScalingElement => x.getScale().isCompatible(this.getScale())
//    case _ => false
//  }

}
