package nounou.elements.data.traits

import nounou.elements.NNElement

/**
  * Specifies that an NNElement can check another NNElement object for compatibility.
  *
  * Created by ktakagaki on 16/01/08.
  */
trait NNElementCompatibilityCheck {

  //self type infers that this trait assumes mixin to NNData
  //this construct allows a mixin trait to use class functionality without inheriting the parent class
  this: NNElement =>


  /**
    * __'''SHOULD OVERRIDE'''__ Whether an [[nounou.elements.NNElement]] is compatible with another for merging
    * (eg [[nounou.elements.data.NNData]])
    *  or comparison (eg [[nounou.elements.spikes.NNSpike]]) etc.
    */
  def isCompatible(that: NNElement): Boolean

  /**
    * Whether multiple [[nounou.elements.NNElement]]s are compatible with another for merging, etc.
    */
  final def isCompatible(that: Seq[NNElement]): Boolean = that.forall( this.isCompatible(_) )


}
