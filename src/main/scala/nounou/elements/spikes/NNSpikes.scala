package nounou.elements.spikes

import java.math.BigInteger

import nounou.analysis.spikes.{OptAlignmentPoint, OptWaveformFr}
import nounou.elements.data.NNData
import nounou.elements.traits.{NNScalingElement, NNConcatenableElement, NNTimingElement, NNTiming}
import nounou.elements.NNElement
import nounou.NN
import nounou.options.Opt
import nounou.util.LoggingExt

import scala.collection.mutable.TreeSet

object NNSpikes extends LoggingExt {

//  def apply(data: NNData, frames: Array[Int], channel: Int, segment: Int, opts: Opt*): NNSpikes =
//    apply(data, frames.map( (fr: Int) => (fr, segment) ), Array(channel), opts: _*)

  def apply(data: NNData, timestamps: Array[BigInt], channel: Int, opts: Opt*): NNSpikes =
    apply(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), Array(channel), opts: _*)

  def apply(data: NNData, timestamps: Array[BigInt], channels: Array[Int], opts: Opt*): NNSpikes =
    apply(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), channels, opts: _*)

  def apply(data: NNData, frameSegments: Array[(Int, Int)], channel: Int, opts: Opt*): NNSpikes =
    apply(data, frameSegments, Array(channel), opts: _*)

  def apply(data: NNData, frameSegments: Array[(Int, Int)], channels: Array[Int], opts: Opt*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" argument checks ">

      loggerRequire( data != null, "data input may not be null!")
      loggerRequire( frameSegments != null, "frames input may not be null!")
      loggerRequire( channels != null, "channels input may not be null!")
      loggerRequire( channels.length > 0, "channels array length must not be zero!")

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" handle options ">

    var optWaveformFr = 32
    var optAlignmentPoint = 8

    for( opt <- opts ) opt match {
      case OptWaveformFr(frames: Int) => optWaveformFr = frames
      case OptAlignmentPoint(frames: Int) => optAlignmentPoint = frames
      case _ => {}
    }


    // </editor-fold>

    val tempReturn = new NNSpikes(optAlignmentPoint)

    frameSegments.foreach( (frsg: (Int, Int) ) => {
        //val startFr = frsg._1  - optPretriggerFr
        val sampleRange = NN.NNRange(frsg._1, frsg._1 + optWaveformFr -1, 1, frsg._2)
        val wf = channels.flatMap(data.readTrace(_, sampleRange ))
        tempReturn.add( new NNSpike( data.timing.convertFrToTs(frsg._1), wf, channels = channels.length, unitNo = 0L) )
        Unit
      })

    tempReturn

  }

  def join( spikes: NNSpikes*): NNSpikes = {
    loggerRequire( spikes != null, "Input may not be null!")
    loggerRequire( spikes.length >= 1, "Must give 1 or more NNSpikes objects!")

    val tempReturn: NNSpikes = spikes(0).copy()
    if(spikes.length > 1){
      for( newSpikes <- spikes.tail ) tempReturn.add(newSpikes)
    }
    tempReturn
  }

}

/** A mutable database of [[nounou.elements.spikes.NNSpike NNSpike]] objects for display and processing.
  * Based on a [scala.collection.mutable.TreeSet[A] mutable.TreeSet], with enforcing of NNSpike compatiblity.
    *
    */
class NNSpikes( private val _database: TreeSet[NNSpike], val alignmentPoint: Int  )
  extends NNConcatenableElement with NNScalingElement/*with NNDataTimingElement*/ {


  def this(alignmentPoint: Int) {
    this( new TreeSet[NNSpike]()(Ordering.by[NNSpike, BigInt]((x: NNSpike) => x.timestamp)), alignmentPoint )
  }

  override def toStringImpl() = s"no.=${size()}, "
  override def toStringFullImpl() = ""

  // <editor-fold defaultstate="collapsed" desc=" database accessors ">

  /**
    * Number of spikes contained in object
    */
  def size(): Int = _database.size

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" prototype spike accessors ">

  private var prototypeSpike: NNSpike = {
    if( size > 0 ) _database.head else null
  }

  def channels(): Int = prototypeSpike.channels
  def singleWaveformLength(): Int = prototypeSpike.singleWaveformLength

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" add spikes ">

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

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" read methods ">

  //ToDo: defensive copy BigInt/BigInteger
  def readSpikeTimestamps(): Array[BigInteger] = _database.map( _.getTimestamp() ).toArray
  def readSpikeFrameSegments(data: NNTimingElement): Array[Array[Int]] = readSpikeFrameSegments(data.timing)
  def readSpikeFrameSegments(data: NNTiming): Array[Array[Int]] = {
    _database.map( (sp: NNSpike) => {
      val temp = data.convertTsToFrsg( sp.timestamp ); Array( temp._1, temp._2 )
    } ).toArray
  }

  // </editor-fold>


  def copy(): NNSpikes = new NNSpikes( _database.clone(), alignmentPoint )

  def iterator(): Iterator[NNSpike] = _database.iterator

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNSpikes => {
        prototypeSpike == null || x.prototypeSpike == null || prototypeSpike.isCompatible(x.prototypeSpike)
      }
      case _ => false
    }


}