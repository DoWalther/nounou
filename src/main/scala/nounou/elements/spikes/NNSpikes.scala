package nounou.elements.spikes

import java.math.BigInteger
import nounou.{NN, Opt}
import nounou.analysis.spikes.OptWaveformFr
import nounou.elements.data.NNData
import nounou.elements.traits.{NNConcatenableElement, NNTimingElement, NNTiming}
import nounou.elements.NNElement
import nounou.util.LoggingExt

import scala.collection.mutable.TreeSet

object NNSpikes extends LoggingExt {

  def apply(data: NNData, frames: Array[Int], channel: Int, segment: Int, opts: Opt*): NNSpikes =
    apply(data, frames.map( (fr: Int) => (fr, segment) ), Array(channel), opts: _*)

  def apply(data: NNData, timestamps: Array[BigInt], channel: Int, opts: Opt*): NNSpikes =
    apply(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), Array(channel), opts: _*)

  def apply(data: NNData, timestamps: Array[BigInt], channels: Array[Int], opts: Opt*): NNSpikes =
    apply(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), channels, opts: _*)

  def apply(data: NNData, frameSegments: Array[(Int, Int)], channels: Array[Int], opts: Opt*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" argument checks ">

      loggerRequire( data != null, "data input may not be null!")
      loggerRequire( frameSegments != null, "frames input may not be null!")
      loggerRequire( channels != null, "channels input may not be null!")
      loggerRequire( channels.length > 0, "channels array length must not be zero!")

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" handle options ">

    var optWaveformFr = 32

    for( opt <- opts ) opt match {
      case OptWaveformFr(frames: Int) => optWaveformFr = frames
      case _ => {}
    }

    // </editor-fold>

    val tempret = new NNSpikes()

    frameSegments.foreach( (frsg: (Int, Int) ) => {
      //val startFr = frsg._1  - optPretriggerFr
      val sampleRange = NN.NNRange(frsg._1 , frsg._1 + optWaveformFr -1 /*+ tempPosttriggerFr*/, 1, frsg._2)
      val wf = channels.flatMap(data.readTraceInt(_, sampleRange ))
      tempret.add( new NNSpike( data.timing.convertFrToTs(frsg._1), wf, channels = 1, unitNo = 0L) )
      Unit
    })

    tempret

  }

  def join( spikes: NNSpikes*): NNSpikes = {
    loggerRequire( spikes != null, "Input may not be null!")
    loggerRequire( spikes.length >= 1, "Must give 1 or more NNSpikes objects!")

    val tempRet: NNSpikes = spikes(0).copy()
    if(spikes.length > 1){
      for( newSpikes <- spikes.tail ) tempRet.add(newSpikes)
    }
    tempRet
  }

}

/** A mutable database of [[nounou.elements.spikes.NNSpike NNSpike]] objects for display and processing.
  * Based on a [scala.collection.mutable.TreeSet[A] mutable.TreeSet], with enforcing of NNSpike compatiblity.
    *
    */
class NNSpikes( private val _database: TreeSet[NNSpike] )//val trodeLayout: NNDataLayoutTrode, val waveFormLength: Int)
  extends NNConcatenableElement /*with NNDataTimingElement with NNDataScaleElement*/ {

  override def toStringImpl() = s"no.=${_database.size}, "
  override def toStringFullImpl() = ""

  def this() {
    this( new TreeSet[NNSpike]()(Ordering.by[NNSpike, BigInt]((x: NNSpike) => x.timestamp))  )
  }

  private var prototypeSpike: NNSpike = {
    if( size > 0 ) _database.head else null
  }

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
  def add(spikes: NNSpikes): Boolean = {
    if(this.isCompatible(spikes)){
      _database.++=( spikes._database )
      true
    } else {
      //if more than one spike has already been loaded, and they are incompatible with new spike
      throw loggerError(s"Tried to add incompatible NNSpikes object: ${spikes}")
      false
    }
  }


  //ToDo: defensive copy BigInt/BigInteger
  def spikeTimestamps(): Array[BigInt] = _database.map( _.timestamp ).toArray
  def getSpikeTimestamps(): Array[BigInteger] = _database.map( _.getTimestamp() ).toArray
  def getSpikeFrameSegment(data: NNTimingElement): Array[Array[Int]] = getSpikeFrameSegment(data.timing)
  def getSpikeFrameSegment(data: NNTiming): Array[Array[Int]] = {
    _database.map( (sp: NNSpike) => {
      val temp = data.convertTsToFrsg( sp.timestamp ); Array( temp._1, temp._2 )
    } ).toArray
  }

  /** Number of spikes contained in object
    */
  def size(): Int = _database.size

  def copy(): NNSpikes = new NNSpikes( _database.clone() )

  def iterator(): Iterator[NNSpike] = _database.iterator

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNSpikes => {
        prototypeSpike == null || x.prototypeSpike == null || prototypeSpike.isCompatible(x.prototypeSpike)
      }
      case _ => false
    }


}

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
