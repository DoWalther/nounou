package nounou.elements.traits

import nounou.elements.NNElement
import nounou.elements.traits

/**This trait specifies that an [[nounou.elements.NNElement NNElement]] can return an
  * immutable
  * [[nounou.elements.traits.NNTiming NNTiming]] object to specify sampling information for data.
  * Although the NNTiming object is immutable, the immutable return value may change.
  *
 * Created by ktakagaki on 15/03/12.
 */
trait NNTimingElement extends NNElement {

  /**
    * Returns [[NNTiming]] which applies to this NNElement.
    *
    */
  def timing(): NNTiming = throw loggerError(s"timing is null (not overriden)!")

  /**'''[NNTimingElement]''' Alias for [[NNTimingElement.timing]]*/
  final def getTiming(): NNTiming = timing()

//  //ToDO 2: check inheritance with called combination instead of override
//  override def isCompatible(x: NNElement) = x match {
//    //case x: NNTimingElement => x.getTiming().isCompatible(this.getTiming())
//    case _ => ??? //false
//  }


}
