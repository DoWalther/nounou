package nounou

//import breeze.linalg.DenseVector

import java.math.BigInteger

import breeze.linalg.DenseVector
import breeze.numerics.sin
import nounou.elements.NNElement
import nounou.elements.data.NNData
import nounou.elements.data.filters.NNDataFilterMedianSubtract
import nounou.ranges._
import nounou.io.{FileLoader, FileSaver}
//import nounou.io.FileLoader._
import nounou.util.{LoggingExt, NNGit}


/** The static convenience frontend to use all main functionality of nounou from Mathematica/MatLab/Java
  * (with avoidance of Java-unfriendly Scala constructs).
  *
  * In idiomatic Scala, Many of these functions would be put in
  * companion objects as an apply method (i.e. SampleRange.apply(start, last, step, segment). These are
  * consolidated here instead to facilitate access through Java.
  *
 * @author ktakagaki
 */
object NN extends LoggingExt {

  //ToDo 2: this is currently not using active git info. update
  override final def toString(): String =
      "Welcome to nounou, a Scala/Java adapter for neurophysiological data.\n" +
      NNGit.infoPrintout

  /**Test method for DW*/
  final def testArray(): Array[Double] = sin( breeze.linalg.DenseVector.tabulate(100)( _.toDouble/50d * 2d * math.Pi )).toArray

  //ToDo SL
  def popUpSFXWindow(): Unit ={
    //Popup up hello world here with a JavaFX Canvas
    ???
  }

  // <editor-fold defaultstate="collapsed" desc=" file loading/saving ">

  /**Load a file into appropriate subtypes of [[nounou.elements.NNElement]]
    * @return an array of [[nounou.elements.NNElement]] objects
    */
  final def load(fileName: String): Array[NNElement] = FileLoader.load(fileName)
  /**Load a list of files into appropriate subtypes of [[nounou.elements.NNElement]].
    * If multiple files are compatible (e.g. multiple Neuralynx channel data files with compatible timings),
    * they will be joined.
    * @return an array of [[nounou.elements.NNElement]] objects
    */
  final def load(fileNames: Array[String]): Array[NNElement] = FileLoader.load(fileNames)
  /**Save an [[nounou.elements.NNElement]] object into the given file.
    *File type will be inferred from the filename extension.
    *
    * @return an array of [[nounou.elements.NNElement]] objects
    */
  final def save(fileName: String, data: NNElement): Unit  = FileSaver.save( fileName, data)
  /**Save an array of [[nounou.elements.NNElement]] object into the given file.
    *This allows you to specify multiple types of data Array(NNData, NNEvents, NNSpikes)
    *for saving into compound file formats (e.g. NEX).
    */
  final def save(fileName: String, data: Array[NNElement]): Unit  = FileSaver.save(fileName, data)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" options ">

  def OptNull() = nounou.OptNull

  /**Option to be used in [[nounou.analysis.threshold]]
    */
  case class OptThresholdBlackout(frames: Int) extends Opt {
    loggerRequire(frames > 0, "blackout must be >0")
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" sample ranges ">

  /**
    * This is the full signature for creating a [[nounou.ranges.NNRange SampleRange]].
    */
  final def NNRange(start: Int, last: Int, step: Int, segment: Int) = new NNRange(start, last, step, segment)

//The following two to be used internally only:
//  final def SampleRangeInstantiated(start: Int, last: Int, step: Int, segment: Int) = new SampleRangeInstantiated(start, last, step, segment)
//  final def SampleRangeValid(start: Int, last: Int, step: Int, segment: Int) = new SampleRangeValid(start, last, step, segment)

//The following are deprecated due to the ambiguity between step and segment variables
//  final def SampleRange(start: Int, last: Int, step: Int)               = new SampleRange(start, last, step, -1)
//  final def SampleRange(start: Int, last: Int, segment: Int)            = new SampleRange(start, last, -1,   segment)
//  final def SampleRange(start: Int, last: Int)                          = new SampleRange(start,    last,     -1,       -1)

  // <editor-fold defaultstate="collapsed" desc=" array based aliases for SampleRange ">

//  /** Scala Tuple-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
//    *
//    * @param range Tuple containing start and end. segment=-1 is assumed.
//    */
//  final def SampleRange( range: (Int, Int) )                            = new SampleRange(start = range._1, last = range._2, step = -1, segment = -1)
//
//  /**
//    * Scala Tuple-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
//    *
//    * @param range Tuple containing start and end.
//    * @param segment Which segment to read from
//    */
//  final def SampleRange( range: (Int, Int), segment: Int)               = new SampleRange(start = range._1, last = range._2, step = -1, segment)
//
//  /**
//    * Scala Tuple-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
//    *
//    * @param range Tuple containing start, end, and step. segment=-1 is assumed.
//    */
//  final def SampleRange( range: (Int, Int, Int) )                       = new SampleRange(start = range._1, last = range._2, step = range._3, segment = -1)
//
//  /**
//    * Scala Tuple-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
//    *
//    * @param range Tuple containing start, end, and step
//    * @param segment Which segment to read from
//    */
//  final def SampleRange( range: (Int, Int, Int), segment: Int )         = new SampleRange(start = range._1, last = range._2, step = range._3, segment)

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

  /**Constructor method for a sample range specifying a whole segment.
    */
  final def NNRangeAll(step: Int, segment: Int) = new NNRangeAll(step, segment)
  /**Constructor method for a sample range specifying a whole segment
    */
  final def NNRangeAll() = new NNRangeAll(1, -1)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" RangeTs ">

  /**
    * This is the full signature for creating a [[nounou.ranges.NNRangeTs SampleRangeTs]].
    */
  final def NNRangeTs(startTs: BigInteger, endTs: BigInteger, stepTs: BigInteger): NNRangeTs =
    new NNRangeTs(startTs, endTs, stepTs)
  final def NNRangeTs(startTs: BigInteger, endTs: BigInteger): NNRangeTs =
    new NNRangeTs(startTs, endTs, -1L)


  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" filters ">

  def filterMedianSubtract(data: NNData) = new NNDataFilterMedianSubtract(data)

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" toArray methods ">

  def toArray(denseVector: DenseVector[Long]) = breeze.util.JavaArrayOps.dvToArray(denseVector)
//  def toArray(xSpike: XSpike) = XSpike.toArray( xSpike )
//  def toArray(xSpikes: Array[XSpike]) = XSpike.toArray( xSpikes )

  // </editor-fold>

//  def readSpikes(xData: XData, channels: Array[Int], xFrames: Array[Frame], length: Int, trigger: Int) =
//    data.XSpike.readSpikes(xData, channels, xFrames, length, trigger)
//  def readSpike(xData: XData, channels: Array[Int], xFrame: Frame, length: Int, trigger: Int) =
//    data.XSpike.readSpike(xData, channels, xFrame, length, trigger)

//
//  //final def XTrodes( trodeGroup: Array[Array[Int]] ): XTrodes = data.XTrodes( trodeGroup )
//  final def XTrodeN( trodeGroup: Array[Int] ): NNTrodeN = new elements.NNTrodeN( trodeGroup.toVector )


}



//final def XSpikes(waveformLength: Int, xTrodes: XTrodes ) = new XSpikes(waveformLength, xTrodes)
//  final def newNNData: NNData = new NNData
