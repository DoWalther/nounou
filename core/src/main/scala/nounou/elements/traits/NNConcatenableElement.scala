package nounou.elements.traits

import nounou.elements.NNElement

/**
  * Created by ktakagaki on 16/01/08.
  */
trait NNConcatenableElement extends NNElement {

  /** __'''SHOULD OVERRIDE'''__ Whether an [[nounou.elements.NNElement]] is compatible with another for merging
    * (eg [[nounou.elements.data.NNData]])
    *  or comparison (eg [[nounou.elements.spikes.NNSpike]]) etc.
    */
  def isCompatible(that: NNElement): Boolean

  /** Whether multiple [[nounou.elements.NNElement]]s are compatible with another for merging, etc.
    */
  final def isCompatible(that: Seq[NNElement]): Boolean = that.forall( this.isCompatible(_) )


}
