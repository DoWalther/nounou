package nounou

import breeze.linalg.DenseVector
import breeze.numerics.sin
import java.math.BigInteger
import nounou.options.{Options, Opt}
import Options.{AlignmentPoint, WaveformFrames}
import nounou.analysis.spikes.OptSpikeDetect
import nounou.elements.NNElement
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.data.filters.NNFilterMedianSubtract
import nounou.elements.spikes.{OptReadSpikes, NNSpikes}
import nounou.options.Opt
import nounou.ranges._
import nounou.io.{FileLoader, FileSaver}
import nounou.util.{LoggingExt, NNGit}

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag


/**
  * The static convenience frontend to use all main functionality of nounou from Mathematica/MatLab/Java
  * (with avoidance of Java-unfriendly Scala constructs). Methods in this static frontend
  * will feature standard Java classes such as BigInteger (instead of the scala BigInt)
  * and Array (instead of internal immutable representations such as scala Vector or breeze DenseVector).
  *
  * In idiomatic Scala, Many of these functions would be put in
  * companion objects as an apply method (i.e. SampleRange.apply(start, last, step, segment). These are
  * consolidated here instead to facilitate access through Java.
  *
 * @author ktakagaki
  *
 */
object NN extends LoggingExt {

  //ToDo SL: this is currently not using active git info. update through reading of .git folder of
  override final def toString(): String =
      "Welcome to nounou, a Scala/Java adapter for neurophysiological data.\n" +
      NNGit.infoPrintout

  /**Test method for DW*/
  final def testArray(): Array[Double] = sin( breeze.linalg.DenseVector.tabulate(100)( _.toDouble/50d * 2d * math.Pi )).toArray

  // <editor-fold defaultstate="collapsed" desc=" convert array of options ">

  def convertOpt[T <: Opt]( arr: Array[Opt] )(implicit tag: ClassTag[T]): Array[T] = {
    val tempReturn = ArrayBuffer[T]()
    arr.foreach( (o: Opt) => o match {
      case x: T => tempReturn.+=( x )
      case _ => throw loggerError(s"incompatible option $o was given!")
      }
    )
    tempReturn.toArray
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" file loading/saving ">

  /**
    * Load a file into appropriate subtypes of [[nounou.elements.NNElement]]
    *
    * @return an array of [[nounou.elements.NNElement]] objects
    */
  final def load(fileName: String): Array[NNElement] = FileLoader.load(fileName)

  /**
    * Load a list of files into appropriate subtypes of [[nounou.elements.NNElement]].
    * If multiple files are compatible (e.g. multiple Neuralynx channel data files with compatible timings),
    * they will be joined.
    *
    * @return an array of [[nounou.elements.NNElement]] objects
    */
  final def load(fileNames: Array[String]): Array[NNElement] = FileLoader.load(fileNames)
  /**
    * Save an [[nounou.elements.NNElement]] object into the given file.
    *File type will be inferred from the filename extension.
    *
    * @return an array of [[nounou.elements.NNElement]] objects
    */
  final def save(fileName: String, data: NNElement): Unit  = FileSaver.save( fileName, data)
  /**
    * Save an array of [[nounou.elements.NNElement]] object into the given file.
    *This allows you to specify multiple types of data Array(NNData, NNEvents, NNSpikes)
    *for saving into compound file formats (e.g. NEX).
    */
  final def save(fileName: String, data: Array[NNElement]): Unit  = FileSaver.save(fileName, data)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" NNRangeSpecifier related ">

    // <editor-fold defaultstate="collapsed" desc=" NNRange ">

    /**
      * This is the full signature for creating a [[nounou.ranges.NNRange SampleRange]].
      */
    final def NNRange(start: Int, last: Int, step: Int, segment: Int) = new NNRange(start, last, step, segment)

  //The following two to be used internally only:
  //  final def NNRangeInstantiated(start: Int, last: Int, step: Int, segment: Int) = new NNRangeInstantiated(start, last, step, segment)
  //  final def NNRangeValid(start: Int, last: Int, step: Int, segment: Int) = new NNRangeValid(start, last, step, segment)

  //The following are deprecated due to the ambiguity between step and segment variables
  //  final def NNRange(start: Int, last: Int, step: Int)               = new NNRange(start, last, step, -1)
  //  final def NNRange(start: Int, last: Int, segment: Int)            = new NNRange(start, last, -1,   segment)
  //  final def NNRange(start: Int, last: Int)                          = new NNRange(start,    last,     -1,       -1)


    // <editor-fold defaultstate="collapsed" desc=" array based aliases for SampleRange ">

    /**
      * Java Array-based signature alias for [[NNRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
      *
      * @param range Array containing start, end, and optionally, step
      * @param segment Which segment to read from
      */
    final def NNRange(range: Array[Int], segment: Int ): NNRangeSpecifier =
      nounou.ranges.NNRange.convertArrayToSampleRange(range, segment)

    /** Java Array-based signature alias for [[NNRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
      *
      * @param range Array containing start, end, and optionally, step. segment = -1 is assumed.
      */
    final def NNRange(range: Array[Int] ): NNRangeSpecifier = NNRange( range, -1 )

    // </editor-fold>

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" NNRangeAll ">

    /**Constructor method for a sample range specifying a whole segment.
      */
    final def NNRangeAll(step: Int, segment: Int) = new NNRangeAll(step, segment)
    /**Constructor method for a sample range specifying a whole segment
      */
    final def NNRangeAll() = new NNRangeAll(1, -1)

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" NNRangeTs ">

    /**
      * This is the full signature for creating a [[nounou.ranges.NNRangeTs SampleRangeTs]].
      */
    final def NNRangeTs(startTs: BigInteger, endTs: BigInteger, stepTs: BigInteger): NNRangeTs =
      new NNRangeTs(startTs, endTs, stepTs)

    final def NNRangeTs(startTs: BigInteger, endTs: BigInteger): NNRangeTs =
      new NNRangeTs(startTs, endTs, -1L)

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" NNRangeTsEvent ">

  final def NNRangeTsEvent(triggerTs: BigInteger, startOffset: Int, lastOffset: Int): NNRangeTsEvent =
    new NNRangeTsEvent(BigInt( triggerTs ), startOffset, lastOffset, 1)
  final def NNRangeTsEvent(triggerTs: BigInteger, startOffset: Int, lastOffset: Int, step: Int): NNRangeTsEvent =
    new NNRangeTsEvent(BigInt( triggerTs ), startOffset, lastOffset, step)

  // </editor-fold>

  // </editor-fold>

//  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int): NNSpikes = {
//    NNSpikes.readSpikes(data, timestamps, channel )
//  }
//  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int]): NNSpikes = {
//    NNSpikes.readSpikes(data, timestamps, channels)
//  }
//  def readSpikes(dataChannel: NNDataChannel , timestamps: Array[BigInteger]): NNSpikes = {
//    NNSpikes.readSpikes(dataChannel, timestamps)
//  }
  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int,
                 waveformFrames: Int, alignmentPoint: Int): NNSpikes = {
    NNSpikes.readSpikes(data, timestamps, channel, WaveformFrames(waveformFrames), AlignmentPoint(alignmentPoint))
  }
  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int],
                 waveformFrames: Int, alignmentPoint: Int): NNSpikes = {
    NNSpikes.readSpikes(data, timestamps, channels, WaveformFrames(waveformFrames), AlignmentPoint(alignmentPoint) )
  }
  def readSpikes(dataChannel: NNDataChannel , timestamps: Array[BigInteger],
                 waveformFrames: Int, alignmentPoint: Int): NNSpikes = {
    NNSpikes.readSpikes(dataChannel, timestamps, WaveformFrames(waveformFrames), AlignmentPoint(alignmentPoint) )
  }

  def readSpikes(data: NNData, timestamps: Array[BigInteger], channel: Int, opts: OptReadSpikes*): NNSpikes = {
    NNSpikes.readSpikes(data, timestamps, channel, opts: _* )
  }
  def readSpikes(data: NNData, timestamps: Array[BigInteger], channels: Array[Int], opts: OptReadSpikes*): NNSpikes = {
    NNSpikes.readSpikes(data, timestamps, channels, opts: _* )
  }
  def readSpikes(dataChannel: NNDataChannel , timestamps: Array[BigInteger], opts: OptReadSpikes*): NNSpikes = {
    NNSpikes.readSpikes(dataChannel, timestamps, opts: _* )
  }


  // <editor-fold defaultstate="collapsed" desc=" Analysis ">

  // <editor-fold defaultstate="collapsed" desc="  spikeDetect ">

  def spikeDetect(data: Array[Double], opts: OptSpikeDetect*): Array[Int] =
    nounou.analysis.spikes.SpikeDetect(data, opts: _*)

  def spikeDetect(data: NNData,
                  range: NNRangeSpecifier,
                  channel: Int,
                  opts: OptSpikeDetect*): Array[BigInteger] =
    nounou.analysis.spikes.SpikeDetect(data, range, channel, opts: _*)

  def spikeDetect(dataChannel: NNDataChannel,
                  range: NNRangeSpecifier,
                  opts: OptSpikeDetect*) =
    nounou.analysis.spikes.SpikeDetect.apply(dataChannel, range, opts: _*)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" filters ">

  def filterMedianSubtract(data: NNData) = new NNFilterMedianSubtract(data)

  // </editor-fold>

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toArray methods ">

  def toArray(denseVector: DenseVector[Long]) = breeze.util.JavaArrayOps.dvToArray(denseVector)
//  def toArray(xSpike: XSpike) = XSpike.toArray( xSpike )
//  def toArray(xSpikes: Array[XSpike]) = XSpike.toArray( xSpikes )

  // </editor-fold>


//
//  //final def XTrodes( trodeGroup: Array[Array[Int]] ): XTrodes = data.XTrodes( trodeGroup )
//  final def XTrodeN( trodeGroup: Array[Int] ): NNTrodeN = new elements.NNTrodeN( trodeGroup.toVector )


}
