package nounou.elements

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNDataTimingElement extends NNElement {

  def timing(): NNDataTiming

//  /**  '''[NNDataTimingElement]''' Alias for [[nounou.elements.NNDataTimingElement.getTiming]].*/
//  final lazy val timing: NNDataTiming = _timing
  /**'''[NNDataTimingElement]''' Alias for [[nounou.elements.NNDataTimingElement.timing]]*/
  final def getTiming(): NNDataTiming = {
    if( timing == null ) throw loggerError(
      s"Cannot use timing-related functions in ${this.getClass.getCanonicalName} without setting 'val timing')" )
    else timing
  }
//  /**'''[NNDataTimingElement]''' */
//  def setTiming(timing: NNDataTiming) = {
//    _timing= timing
//
//    //ToDo 2: child change hierarchy in NNElement
//    logger.trace("child hierarchy update has not been implemented yet!")
//  }

  override def isCompatible(x: NNElement) = x match {
    case x: NNDataTimingElement => x.getTiming().isCompatible(this.getTiming())
    case _ => false
  }


}
