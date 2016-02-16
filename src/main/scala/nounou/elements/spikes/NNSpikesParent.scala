package nounou.elements.spikes

import java.math.BigInteger
import nounou.elements.NNElement
import nounou.elements.traits._
import scala.collection._

/**
  * Parent class for a mutable database of [[nounou.elements.spikes.NNSpike NNSpike]] objects.
  * Based on a [scala.collection.mutable.TreeSet[A] mutable.TreeSet], with enforcing of element compatiblity.
  *
  * The bulk of functionality is defined here (in an abstract parent class)
  * so that implementations can deal transparently with generic NNSpike children types
  * (such as NNSpikeNlx for Neuralynx files), without the API itself being generic.
  *
  * @param alignmentPoint sample number at which the timestamp is read
  */
abstract class NNSpikesParent[S <: NNSpike]( protected[nounou] val _database: mutable.SortedSet[S],
                                             val alignmentPoint: Int,
                                             override val scaling: NNScaling,
                                             override val timing: NNTiming)
  extends NNConcatenableElement with NNScalingElement with NNTimingElement {


  def this(alignmentPoint: Int, scaling: NNScaling, timing: NNTiming) {
    this(
      new mutable.TreeSet[S]()(Ordering.by[S, BigInt]((x: S) => x.timestamp)),
      alignmentPoint,
      scaling,
      timing
    )
  }
  //  def this(alignmentPoint: Int) = this(alignmentPoint, null, null)

  override def toStringImpl() = s"no.=${size()}"
  override def toStringFullImpl() = ""

  // <editor-fold defaultstate="collapsed" desc=" database accessors ">

  /**
    * Number of spikes contained in object
    */
  def size(): Int = _database.size

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" prototype spike accessors ">

  protected[nounou] var prototypeSpike: S
  //  = {
  //    if( size <= 0 ) NNSpikes.NULL_SPIKE else _database.head
  //  }

  def channels(): Int = {
    if( size == 0 ) -1
    else{
      if( prototypeSpike == null ) prototypeSpike = _database.head
      prototypeSpike.channels
    }
  }

  def singleWaveformLength(): Int = prototypeSpike.singleWaveformLength

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" add spikes ">

  def add(elem: S): Boolean = {
    if( prototypeSpike == null ){
      //if this is the first spike added to the database
      prototypeSpike = elem
    } else if(!prototypeSpike.isCompatible(elem)) {
      //if more than one spike has already been loaded, and they are incompatible with new spike
      throw loggerError(s"Tried to add an incompatible spike: ${elem}")
    }
    _database.add(elem)
  }

  def add(spikes: NNSpikesParent[S]): Boolean = {
    if(this.isCompatible(spikes)){
      _database.++=( spikes._database )
      true
    } else {
      //if more than one spike has already been loaded, and they are incompatible with new spike
      throw loggerError(s"Tried to add incompatible NNSpikes object: ${spikes}")
      false
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" read methods ">

  //ToDo: defensive copy BigInt/BigInteger
  def readSpikeTimestamps(): Array[BigInteger] = _database.map( _.timestamp.bigInteger ).toArray
  def readSpikeFrameSegments(data: NNTimingElement): Array[Array[Int]] = readSpikeFrameSegments(data.timing)
  def readSpikeFrameSegments(data: NNTiming): Array[Array[Int]] = {
    _database.map( (sp: NNSpike) => {
      val temp = data.convertTsToFrsg( sp.timestamp ); Array( temp._1, temp._2 )
    } ).toArray
  }
  def readSpikeAbsoluteMaximumValue(): Double = {
    val it = this.iterator()
    var absMax: Double = Double.NegativeInfinity
    while( it.hasNext ){
      val spikeAbsMax = it.next.waveformAbsMax
      if( absMax < spikeAbsMax ) absMax = spikeAbsMax
    }
    absMax
  }
  def readSpikeWaveformsFlat(): Array[Array[Double]] = iterator.map( _.readWaveformFlat() ).toArray
  def readSpikeWaveforms(): Array[Array[Array[Double]]] = iterator.map( _.readWaveform() ).toArray

  // </editor-fold>


  //  def copy(): NNSpikes = new NNSpikes( _database.clone(), alignmentPoint, this.scaling, this.timing )

  def iterator(): Iterator[S] = _database.iterator

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNSpikesParent[S] => {
        prototypeSpike == null || x.prototypeSpike == null || prototypeSpike.isCompatible(x.prototypeSpike)
      }
      case _ => false
    }


}