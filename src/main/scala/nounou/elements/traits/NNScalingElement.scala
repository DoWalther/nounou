package nounou.elements.traits

import nounou.elements.NNElement

/**
  * Trait to allow an [[nounou.elements.NNElement NNElement]] to handle
  * data scaling information, which carries value units,
  * and can include scaling for writing to files.
  *
  */
trait NNScalingElement extends NNElement {

  private var _scale: NNScaling = null //  NNDataScale.raw

  /**
    * '''[NNScalingElement]''' Get physical scaling information for data.
    * This is not made final, because it will be overriden by some filters which pass through
    * upstream scale information.
    *
    * This function can return null if the scaling is not set e.g. [[nounou.elements.events.NNEvents]].
    * make sure to handle that.
    */
  def scaling(): NNScaling = {
    if( _scale == null ) throw loggerError(s"Scale is null!")
    else _scale
  }

  /**
    * '''[NNScalingElement]''' Java alias for [[NNScalingElement.scaling]].
    * */
  final def getScale(): NNScaling = scaling()

  /**
    * '''[NNScalingElement]''' Set physical scaling information for data.
    * */
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
