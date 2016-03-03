package nounou.elements.spikes

import java.math.BigInteger
import nounou.elements.data.traits.{NNTiming, NNScaling}
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.traits._
import nounou.NN
import nounou.options.{Options, Opt}
import Options.{OverlapWindow, AlignmentPoint, WaveformFrames}
import nounou.options.Opt
import nounou.util.LoggingExt
import scala.collection.mutable.{ArrayBuffer, TreeSet}


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
    var optOverlapWindow = optWaveformFrames

    for( opt <- opts ) opt match {
      case WaveformFrames(value: Int) => optWaveformFrames = value
      case AlignmentPoint(value: Int) => optAlignmentPoint = value
      case OverlapWindow(value: Int) => optOverlapWindow = value
      case _ => {}
    }

    // </editor-fold>

    val tempReturn = new NNSpikes(optAlignmentPoint, data.scaling, data.timing)

    frameSegments.foreach( (frsg: (Int, Int) ) => {
        val startFr = frsg._1 - optAlignmentPoint + 1
        val sampleRange = NN.NNRange(startFr, startFr + optWaveformFrames -1, 1, frsg._2)
        val wf = channels.flatMap(data.readTrace(_, sampleRange ))
        tempReturn.add(
          new NNSpike( data.timing.convertFrToTs(frsg._1), wf.toVector, channels = channels.length, unitNo = 0L)
        )
        Unit
      })

    //Filter spikes which are closer than optOverlapWindow frames apart
    //take spike which has bigger maximum, remove rest
    if(optOverlapWindow > 0){
      val filteredReturn = ArrayBuffer[NNSpike]()
      val iterator = tempReturn._database.iterator
      var lastSpike: NNSpike = if( iterator.hasNext ) iterator.next() else null

      while( iterator.hasNext ){
        val nextSpike = iterator.next

        if( nextSpike.timestamp - lastSpike.timestamp > optOverlapWindow ){
          filteredReturn.append( lastSpike )
        }else{
          if( nextSpike.waveformMax > lastSpike.waveformMax ) lastSpike = nextSpike
        }
      }
      if( filteredReturn.last != lastSpike ) filteredReturn.append( lastSpike )

      new NNSpikes( filteredReturn.toArray, optAlignmentPoint, data.scaling, data.timing)

    }else{
      tempReturn
    }

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
      val startFr = frsg._1 - optAlignmentPoint  + 1
      val sampleRange = NN.NNRange(startFr, startFr + optWaveformFrames -1, 1, frsg._2)
      val wf = dataChannel.readTrace( sampleRange )
      tempReturn.add( new NNSpike( dataChannel.timing.convertFrToTs(frsg._1), wf.toVector, channels = 1, unitNo = 0L) )
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

  val NULL_SPIKE = null //new NNSpike(BigInt(0), Vector[Double](), 0, 0L)

}

/**
  * The most generic implementation of [[nounou.elements.spikes.NNSpikesParent NNSpikesParent]]
  * containing [[nounou.elements.spikes.NNSpike NNSpike]] objects.
  *
  */
class NNSpikes( _database: TreeSet[NNSpike],
                alignmentPoint: Int,
                scaling: NNScaling,
                timing: NNTiming)
  extends NNSpikesParent[NNSpike](_database, alignmentPoint, scaling, timing) {

  // <editor-fold defaultstate="collapsed" desc=" alternate constructor ">

  /**
    * Alternate constructor with empty database.
    */
  def this(alignmentPoint: Int, scaling: NNScaling, timing: NNTiming) {
    this(
      new TreeSet[NNSpike]()(Ordering.by[NNSpike, BigInt]((x: NNSpike) => x.timestamp)),
      alignmentPoint,
      scaling,
      timing
    )
  }

  def this(database: Array[NNSpike], alignmentPoint: Int, scaling: NNScaling, timing: NNTiming) {
    this(
      (new TreeSet[NNSpike]()(Ordering.by[NNSpike, BigInt]((x: NNSpike) => x.timestamp))).++=(database),
      alignmentPoint,
      scaling,
      timing
    )
  }

  // </editor-fold>

  def copy(): NNSpikes = new NNSpikes( _database.clone(), alignmentPoint, this.scaling, this.timing )

  override var prototypeSpike: NNSpike = null //if( size <= 0 ) NNSpikes.NULL_SPIKE else _database.head

}