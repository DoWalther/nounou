package nounou.elements.spikes

import nounou.elements.{NNDataScaleElement, NNDataTimingElement, NNElement}
import nounou.util.LoggingExt

import scala.collection.mutable.TreeSet

object NNSpikes extends LoggingExt {

}

/** A mutable database of [[nounou.elements.spikes.NNSpike]] objects for display and processing.
  * Based on a [[scala.collection.mutable.TreeSet]], with enforcing of NNSpike compatiblity.
    *
    */
class NNSpikes()//val trodeLayout: NNDataLayoutTrode, val waveFormLength: Int)
  extends NNElement with NNDataTimingElement with NNDataScaleElement {

  override def toStringFullImplParams() = s"no.=${_database.size}, "
  override def toStringFullImplTail() = ""

  private val _database = new TreeSet[NNSpike]()( Ordering.by[NNSpike, BigInt]( (x: NNSpike) => x.timestamp) )
  private var prototypeSpike: NNSpike = null

  def add(elem: NNSpike): Boolean = {
    if( prototypeSpike == null ){
      //if this is the first spike added to the database
      prototypeSpike = elem
    } else if(!prototypeSpike.isCompatible(elem)) {
      //if more than one spike has already been loaded, and they are incompatible with new spike
      throw loggerError(s"Tried to add an incompatible spike: ${elem}")
    }

    _database.add(elem)
  }

  /** Number of spikes contained in object
    */
  def size(): Int = _database.size

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNSpikes => {
        prototypeSpike == null || x.prototypeSpike == null || prototypeSpike.isCompatible(x.prototypeSpike)
      }
      case _ => false
    }


}

//  def readSpikeTs(trode: Int): Array[Long] = {
//    loggerRequire(trodeLayout.isValidTrode(trode), "trode={} is invalid", trode.toString, spikes.length.toString)
//    spikes(trode).map( p => p._1 ).toArray
//  }
//
//
//  // <editor-fold desc="readSpikes/readSpikeTimes">
//
////  def readSpikes(trode: Int): Array[Array[Array[Int]]] = {
////    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
//////    val tempret = new ArrayBuffer[Array[Array[Int]]]()
//////    tempret.sizeHint( spikeCount(trode ))
////    spikes(trode).map( p => p._2.waveform ).toArray
////  }
//
//
//  // </editor-fold>
//
////  // <editor-fold desc="XConcatenatable">
////
////  override def :::(that: NNElement): NNSpikes = {
////    that match {
////      case x: NNSpikes => {
////        if( this.isCompatible(x) ) {
////          val temp = new NNSpikes( waveformLength, x.xTrodes, x.xData )
////          temp.spikes ++: this.spikes
////          temp.spikes ++: x.spikes
////          temp
////        }
////        else throw new IllegalArgumentException("cannot concatenate spikes with different waveform lengths")
////      }
////      case _ => {
////        require(false, "cannot concatenate different types!")
////        this
////      }
////    }
////  }
////
////
