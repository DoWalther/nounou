package nounou.elements.layout

/**This trait marks the element as being associated with a [[NNLayoutNeighbor]] object.
 * Since this code is almost completely copied from [[NNLayoutElement]], update this too when updating
 *
 * Created by ktakagaki on 16/03/28.
 */
trait NNLayoutNeighborElement extends NNLayoutElement {

//  override protected var _layout: NNLayoutNeighbor = null
//
  override def layout(): NNLayoutNeighbor = getLayout()
  override def getLayout(): NNLayoutNeighbor = {
    _layout match {
      case x: NNLayoutNeighbor => x
      case _ => throw loggerError("Set layout is not an NNLayoutNeighborElement instance!")
    }
  }
  override def setLayout(layout: NNLayout): Unit = {
    layout match {
      case x: NNLayoutNeighbor => super.setLayout(layout)
      case _ => throw loggerError("Layout must be an NNLayoutElement instance for NNLayoutNeighborElement!")
    }
  }


}
