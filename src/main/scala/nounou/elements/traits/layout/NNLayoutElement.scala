package nounou.elements.traits.layout

import nounou.elements.NNElement

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNLayoutElement extends NNElement {

  private var _layout: NNLayout = null

  final def layout(): NNLayout = getLayout()
  def getLayout(): NNLayout = {
    if( _layout == null ) throw loggerError(
      s"Cannot use timing-related functions in ${this.getClass.getCanonicalName} without first calling setTiming()")
    else _layout
  }
  def setLayout(layout: NNLayout) = {
    _layout= layout
    //ToDo 2: child change hierarchy in NNElement
  }

//  override def isCompatible(x: NNElement) = x match {
//    case x: NNLayoutElement => x.getLayout().isCompatible(this.getLayout())
//    case _ => false
//  }


}
