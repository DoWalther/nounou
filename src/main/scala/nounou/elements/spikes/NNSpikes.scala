package nounou.elements.spikes

import java.math.BigInteger
import nounou.elements.traits.{NNTiming, NNScaling}
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.NN
import nounou.options._
import nounou.util.LoggingExt
import scala.collection.mutable
import scala.collection.mutable.{TreeSet}


trait OptReadSpikes extends Opt

object NNSpikes extends LoggingExt {

  // <editor-fold defaultstate="collapsed" desc=" readSpikes ">

  def readSpikes(data: NNData, timestamps: Array[BigInt], channel: Int, opts: OptReadSpikes*): NNSpikes =
    readSpikes(data, timestamps.map( (ts: BigInt) => data.timing.convertTsToFrsg(ts) ), Array(channel), opts: _*)

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

    val optAlignmentPoint = OptHandler.readOptInt[OptAlignmentPointInt](opts, 8)
    val optWaveformFrames = OptHandler.readOptInt[OptWaveformFramesInt](opts, 32)

    //    var optWaveformFrames = 32
//    var optAlignmentPoint = 8
////    var optOverlapWindow = optWaveformFrames
//    for( opt <- opts ) opt match {
//      case WaveformFrames(value: Int) => optWaveformFrames = value
//      case OptAlignmentPoint(value: Int) => optAlignmentPoint = value
////      case OverlapWindow(value: Int) => optOverlapWindow = value
//      case _ => {}
//    }
//println("optWaveformFrames = " + optWaveformFrames)
//println("optAlignmentPoint = " + optAlignmentPoint)
    // </editor-fold>


    val tempReturn = new NNSpikes(optAlignmentPoint, data.scaling, data.timing)
//println( "tempReturn0 " + tempReturn.size.toString )
//println( "frameSegments " + frameSegments.size.toString )

    frameSegments.foreach( (frsg: (Int, Int) ) => {
        val startFr = frsg._1 - optAlignmentPoint + 1
        val sampleRange = NN.NNRange(startFr, startFr + optWaveformFrames -1, 1, frsg._2)
//        val wf = channels.flatMap( data.readTrace( _, sampleRange ) )
        val wfs = channels
                    .map( data.readTrace( _, sampleRange ) )
                    .map( (d: Array[Double])=> d.map( _ - d(optAlignmentPoint+1) ) )
        tempReturn.add(
          new NNSpike(
            data.timing.convertFrsgToTs(frsg._1, frsg._2),
            wfs.flatten.toVector,
            //wfs.map( (d: Array[Double])=> d.map( _ - d(optAlignmentPoint+1) ) ).flatten.toVector,
            channels = channels.length, unitNo = 0L
          )
        )
        Unit
      })
//println( "tempReturn " + tempReturn.size.toString )
    //Filter spikes which are closer than optOverlapWindow frames apart
    //take spike which has bigger maximum, remove rest
//    if(optOverlapWindow > 0){
//      val filteredReturn = ArrayBuffer[NNSpike]()
//      val iterator = tempReturn._database.iterator
//      var lastSpike: NNSpike = if( iterator.hasNext ) iterator.next() else null
//
//      while( iterator.hasNext ){
//        val nextSpike = iterator.next
//
//        if( nextSpike.timestamp - lastSpike.timestamp > optOverlapWindow ){
//          filteredReturn.+=( lastSpike )
//        }else{
//          if( nextSpike.waveformMax > lastSpike.waveformMax ) lastSpike = nextSpike
//        }
//      }
//      if( filteredReturn.last != lastSpike ) filteredReturn.+=( lastSpike )
//println( "tempReturnFiltered " + tempReturn.size.toString )
//
//      new NNSpikes( filteredReturn.toArray, optAlignmentPoint, data.scaling, data.timing)
//
//    }else{
//      tempReturn
//    }

    tempReturn

  }


  def readSpikes(dataChannel: NNDataChannel, timestamps: Array[BigInt], opts: OptReadSpikes*): NNSpikes =
    readSpikes(dataChannel, timestamps.map((ts: BigInt) => dataChannel.timing.convertTsToFrsg(ts) ), opts: _*)

  def readSpikes(dataChannel: NNDataChannel, frameSegments: Array[(Int, Int)], opts: OptReadSpikes*): NNSpikes = {

    // <editor-fold defaultstate="collapsed" desc=" argument checks ">

    loggerRequire( dataChannel != null, "data input may not be null!")
    loggerRequire( frameSegments != null, "frames input may not be null!")

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" handle options ">

    val optAlignmentPoint = OptHandler.readOptInt[OptAlignmentPointInt](opts, 8)
    val optWaveformFrames = OptHandler.readOptInt[OptWaveformFramesInt](opts, 32)

    // </editor-fold>

    val tempReturn = new NNSpikes(optAlignmentPoint, scaling = dataChannel, timing = dataChannel)

    frameSegments.foreach( (frsg: (Int, Int) ) => {
      val startFr = frsg._1 - optAlignmentPoint  + 1
      val sampleRange = NN.NNRange(startFr, startFr + optWaveformFrames -1, 1, frsg._2)
      val wf = dataChannel.readTrace( sampleRange )
      tempReturn.add(
        new NNSpike(
          dataChannel.timing.convertFrsgToTs(frsg._1, frsg._2)
          /*dataChannel.timing.convertFrToTs(frsg._1)*/,
          wf.toVector, channels = 1, unitNo = 0L)
      )
      Unit
    })

    tempReturn

  }

  // <editor-fold defaultstate="collapsed" desc=" deprecated signatures ">

  //  def apply(data: NNData, frames: Array[Int], channel: Int, segment: Int, opts: Opt*): NNSpikes =
  //    apply(data, frames.map( (fr: Int) => (fr, segment) ), Array(channel), opts: _*)

  //  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int, opts: OptReadSpikes*): NNSpikes =
  //    readSpikes(data, timestamps.map( BigInt(_) ), Array(channel), opts: _*)
  //  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int, opts: Array[OptReadSpikes]): NNSpikes =
  //    readSpikes(data, timestamps.map( BigInt(_) ), Array(channel), opts: _*)

  //  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int], opts: OptReadSpikes*): NNSpikes =
  //    readSpikes(data, timestamps.map( BigInt(_) ), channels, opts: _*)
  //  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int], opts: Array[OptReadSpikes]): NNSpikes =
  //    readSpikes(data, timestamps.map( BigInt(_) ), channels, opts: _*)

  //  def readSpikes(dataChannel: NNDataChannel, timestamps: Array[BigInteger], opts: OptReadSpikes*): NNSpikes =
  //    readSpikes(dataChannel, timestamps.map( BigInt(_) ), opts: _*)

  //  def readSpikes(dataChannel: NNDataChannel, timestamps: Array[BigInteger], opts: Array[OptReadSpikes]): NNSpikes =
  //    readSpikes(dataChannel, timestamps.map( BigInt(_) ), opts: _*)



  // </editor-fold>

  // </editor-fold>

  def waveformAlign( nnSpikes: NNSpikes ): NNSpikes = {
    val newSpikes = new TreeSet[NNSpike]()(Ordering.by[NNSpike, BigInt]((x: NNSpike) => x.timestamp))
    nnSpikes._database.foreach(
      (s: NNSpike) => {
        val newWf = new Array[Double](s.waveform.length)
        for(ch <- 0 until s.channels; sample <- 0 until s.singleWaveformLength){
          newWf(ch * s.singleWaveformLength + sample) =
            s.waveform(ch * s.singleWaveformLength + sample) -
            s.waveform(ch * s.singleWaveformLength + nnSpikes.alignmentPoint-1 )
        }
        //val newWFs = s.readWaveform().map( (arr: Array[Double]) => arr.map( _ - arr(nnSpikes.alignmentPoint-1) ))
        //val newWF = s.waveform.map( _ - s.waveform(nnSpikes.alignmentPoint-1))
        newSpikes.+=( new NNSpike(s.timestamp, newWf.toVector/*flatten(newWFs)*/, s.channels, s.unitNo) )
      }
    )
    new NNSpikes(newSpikes, nnSpikes.alignmentPoint, nnSpikes.scaling, nnSpikes.timing )
  }

//  private def flatten( arr: Array[Array[Double]] ): Array[Double] = {
//    var tempReturn = new ArrayBuffer[Double](32*4)
//    var i = 0
//    for(c <- 0 until arr.length){
//      for( cc <- 0 until arr(c).length ) {
//        tempReturn(i) = arr(c)(cc)
//        i += 1
//      }
//    }
//    tempReturn.toArray
//  }

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
class NNSpikes( _database: mutable.SortedSet[NNSpike],
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