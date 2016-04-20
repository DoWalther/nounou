package nounou.io.neuralynx


import nounou.elements.traits.{NNTiming, NNScaling}
import nounou.elements.spikes.{NNSpikes, NNSpikesParent, NNSpike}
import nounou.elements.traits.NNTiming
import scala.collection._

object NNSpikesNeuralynx {

  /**
    * Alternate constructor from generic NNSpikes object.
    */
  implicit def convertNNSpikesToNNSpikesNeuralynx( nnSpikes: NNSpikes) = {
    val temp =  new mutable.TreeSet[NNSpikeNeuralynx]()(
                      Ordering.by[NNSpikeNeuralynx, BigInt]((x: NNSpikeNeuralynx) => x.timestamp)
                )
    temp.++=(
      nnSpikes._database.map( (s: NNSpike) => NNSpikeNeuralynx.convertNNSpikeToNNSpikeNeuralynx(s) )
    )
    new NNSpikesNeuralynx(
      temp,
      nnSpikes.alignmentPoint,
      nnSpikes.scaling,
      nnSpikes.timing
    )
  }

}

/**
  * An implementation of [[nounou.elements.spikes.NNSpikesParent NNSpikesParent]]
  * containing [[nounou.io.neuralynx.NNSpikeNeuralynx NNSpikeNeuralynx]] spike objects.
  *
  */
class NNSpikesNeuralynx(
                         _database: mutable.SortedSet[NNSpikeNeuralynx],
                         alignmentPoint: Int,
                         scaling: NNScaling,
                         timing: NNTiming)
  extends NNSpikesParent[NNSpikeNeuralynx](_database, alignmentPoint, scaling, timing) {

  // <editor-fold defaultstate="collapsed" desc=" alternate constructor ">

  /**
    * Alternate constructor with empty database.
    */
  def this(alignmentPoint: Int, scaling: NNScaling, timing: NNTiming) {
    this(
      new mutable.TreeSet[NNSpikeNeuralynx]()(Ordering.by[NNSpikeNeuralynx, BigInt]((x: NNSpikeNeuralynx) => x.timestamp)),
      alignmentPoint,
      scaling,
      timing
    )
  }

//  /**
//    * Alternate constructor from generic NNSpikes object.
//    */
//  def this(nnSpikes: NNSpikes) {
//    this(
//      nnSpikes._database.map( NNSpikeNeuralynx.convertNNSpikeToNNSpikeNeuralynx(_)),
////      {val temp =  new mutable.TreeSet[NNSpikeNeuralynx]()(
////          Ordering.by[NNSpikeNeuralynx, BigInt]((x: NNSpikeNeuralynx) => x.timestamp)
////      )
////        temp.++:(
////          nnSpikes._database.map( (s: NNSpike) => NNSpikeNeuralynx.convertNNSpikeToNNSpikeNeuralynx(s) )
////        )
////        temp},
//      nnSpikes.alignmentPoint,
//      nnSpikes.scaling,
//      nnSpikes.timing
//    )
//  }


    // </editor-fold>

  def copy(): NNSpikesNeuralynx = new NNSpikesNeuralynx( _database.clone(), alignmentPoint, this.scaling, this.timing )

  /**
    * Returns a prototype spike for compatibility testing.
    * Implemented here to ensure
    *
    */
  override var prototypeSpike: NNSpikeNeuralynx = null

}
