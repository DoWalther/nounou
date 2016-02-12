package nounou.elements.spikes

import java.math.BigInteger

import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.traits._
import nounou.elements.NNElement
import nounou.NN
import nounou.Options.{AlignmentPoint, WaveformFrames}
import nounou.options.Opt
import nounou.util.LoggingExt

import scala.collection.mutable.TreeSet


trait OptReadSpikes extends Opt

object NNSpikes extends LoggingExt {

//  def apply(data: NNData, frames: Array[Int], channel: Int, segment: Int, opts: Opt*): NNSpikes =
//    apply(data, frames.map( (fr: Int) => (fr, segment) ), Array(channel), opts: _*)

  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int, opts: OptReadSpikes*): NNSpikes =
    readSpikes(data, timestamps.map( BigInt(_) ), Array(channel), opts: _*)
//  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int, opts: Array[OptReadSpikes]): NNSpikes =
//    readSpikes(data, timestamps.map( BigInt(_) ), Array(channel), opts: _*)
  def readSpikes(data: NNData, timestamps: Array[BigInt], channel: Int, opts: OptReadSpikes*): NNSpikes =
    readSpikes(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), Array(channel), opts: _*)

  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int], opts: OptReadSpikes*): NNSpikes =
    readSpikes(data, timestamps.map( BigInt(_) ), channels, opts: _*)
//  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int], opts: Array[OptReadSpikes]): NNSpikes =
//    readSpikes(data, timestamps.map( BigInt(_) ), channels, opts: _*)
  def readSpikes(data: NNData, timestamps: Array[BigInt], channels: Array[Int], opts: OptReadSpikes*): NNSpikes =
    readSpikes(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), channels, opts: _*)

  def readSpikes(data: NNData, frameSegments: Array[(Int, Int)], channel: Int, opts: OptReadSpikes*): NNSpikes =
    readSpikes(data, frameSegments, Array(channel), opts: _*)

  def readSpikes(data: NNData, frameSegments: Array[(Int, Int)], channels: Array[Int], opts: OptReadSpikes*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" argument checks ">

      loggerRequire( data != null, "data input may not be null!")
      loggerRequire( frameSegments != null, "frames input may not be null!")
      loggerRequire( channels != null, "channels input may not be null!")
      loggerRequire( channels.length > 0, "channels array length must not be zero!")

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" handle options ">

    var optWaveformFrames = 32
    var optAlignmentPoint = 8

    for( opt <- opts ) opt match {
      case WaveformFrames(value: Int) => optWaveformFrames = value
      case AlignmentPoint(value: Int) => optAlignmentPoint = value
      case _ => {}
    }


    // </editor-fold>

    val tempReturn = new NNSpikes(optAlignmentPoint, data.scaling, data.timing)

    frameSegments.foreach( (frsg: (Int, Int) ) => {
        val startFr = frsg._1 - optAlignmentPoint + 1
        val sampleRange = NN.NNRange(startFr, startFr + optWaveformFrames -1, 1, frsg._2)
        val wf = channels.flatMap(data.readTrace(_, sampleRange ))
        tempReturn.add( new NNSpike( data.timing.convertFrToTs(frsg._1), wf, channels = channels.length, unitNo = 0L) )
        Unit
      })

    tempReturn

  }


  def readSpikes(dataChannel: NNDataChannel, timestamps: Array[BigInteger], opts: OptReadSpikes*): NNSpikes =
    readSpikes(dataChannel, timestamps.map( BigInt(_) ), opts: _*)
//  def readSpikes(dataChannel: NNDataChannel, timestamps: Array[BigInteger], opts: Array[OptReadSpikes]): NNSpikes =
//    readSpikes(dataChannel, timestamps.map( BigInt(_) ), opts: _*)
  def readSpikes(dataChannel: NNDataChannel, timestamps: Array[BigInt], opts: OptReadSpikes*): NNSpikes =
    readSpikes(dataChannel, timestamps.map((ts: BigInt) => dataChannel.timing.convertTsToFrsg(ts) ), opts: _*)

  def readSpikes(dataChannel: NNDataChannel, frameSegments: Array[(Int, Int)], opts: OptReadSpikes*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" argument checks ">

    loggerRequire( dataChannel != null, "data input may not be null!")
    loggerRequire( frameSegments != null, "frames input may not be null!")

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" handle options ">

    var optWaveformFrames = 32
    var optAlignmentPoint = 8

    for( opt <- opts ) opt match {
      case WaveformFrames(value: Int) => optWaveformFrames = value
      case AlignmentPoint(value: Int) => optAlignmentPoint = value
      case _ => {}
    }


    // </editor-fold>

    val tempReturn = new NNSpikes(optAlignmentPoint, scaling = dataChannel, timing = dataChannel)

    frameSegments.foreach( (frsg: (Int, Int) ) => {
      val startFr = frsg._1 - optAlignmentPoint + 1
      val sampleRange = NN.NNRange(startFr, startFr + optWaveformFrames -1, 1, frsg._2)
      val wf = dataChannel.readTrace( sampleRange )
      tempReturn.add( new NNSpike( dataChannel.timing.convertFrToTs(frsg._1), wf, channels = 1, unitNo = 0L) )
      Unit
    })

    tempReturn

  }



  def join( spikes: NNSpikes* ): NNSpikes = {
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
class NNSpikes( private val _database: TreeSet[NNSpike],
                val alignmentPoint: Int,
                override val scaling: NNScaling,
                override val timing: NNTiming)
  extends NNConcatenableElement with NNScalingElement with NNTimingElement {

  def this(alignmentPoint: Int, scaling: NNScaling, timing: NNTiming) {
    this(
      new TreeSet[NNSpike]()(Ordering.by[NNSpike, BigInt]((x: NNSpike) => x.timestamp)),
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


  def copy(): NNSpikes = new NNSpikes( _database.clone(), alignmentPoint, this.scaling, this.timing )

  def iterator(): Iterator[NNSpike] = _database.iterator

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNSpikes => {
        prototypeSpike == null || x.prototypeSpike == null || prototypeSpike.isCompatible(x.prototypeSpike)
      }
      case _ => false
    }


}