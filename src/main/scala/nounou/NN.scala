package nounou

//import breeze.linalg.DenseVector

import breeze.linalg.DenseVector
import nounou.elements.NNElement
import nounou.elements.data.NNData
import nounou.elements.data.filters.NNDataFilterMedianSubtract
import nounou.elements.ranges._
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

  override final def toString(): String =
      "Welcome to nounou, a Scala/Java adapter for neurophysiological data.\n" +
      NNGit.infoPrintout

  // <editor-fold defaultstate="collapsed" desc=" file loading/saving ">

  final def load(fileName: String): Array[NNElement] = FileLoader.load(fileName)
  final def load(fileNames: Array[String]): Array[NNElement] = FileLoader.load(fileNames)
  final def save(fileName: String, data: NNElement): Unit  = FileSaver.save( fileName, data)
  final def save(fileName: String, data: Array[NNElement]): Unit  = FileSaver.save(fileName, data)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" options ">

  def OptNull() = nounou.OptNull
  case class OptThresholdBlackout(frames: Int) extends Opt {
    loggerRequire(frames > 0, "blackout must be >0")
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" sample ranges ">

  /**This is the full signature for creating a [[nounou.elements.ranges.SampleRange SampleRange]].*/
  final def SampleRange(start: Int, last: Int, step: Int, segment: Int) = new SampleRange(start, last, step, segment)
  final def SampleRangeReal(start: Int, last: Int, step: Int, segment: Int) = new SampleRangeReal(start, last, step, segment)
  final def SampleRangeValid(start: Int, last: Int, step: Int, segment: Int) = new SampleRangeValid(start, last, step, segment)

//The following are deprecated due to the ambiguity between step and segment variables
//  final def SampleRange(start: Int, last: Int, step: Int)               = new SampleRange(start, last, step, -1)
//  final def SampleRange(start: Int, last: Int, segment: Int)            = new SampleRange(start, last, -1,   segment)
//  final def SampleRange(start: Int, last: Int)                          = new SampleRange(start,    last,     -1,       -1)
  /** Scala-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
    *
    * @param range Tuple containing start and end. segment=-1 is assumed.
    */
  final def SampleRange( range: (Int, Int) )                            = new SampleRange(start = range._1, last = range._2, step = -1, segment = -1)
  /** Scala-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
    *
    * @param range Tuple containing start and end.
    * @param segment Which segment to read from
    */
  final def SampleRange( range: (Int, Int), segment: Int)               = new SampleRange(start = range._1, last = range._2, step = -1, segment)
  /** Scala-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
    *
    * @param range Tuple containing start, end, and step. segment=-1 is assumed.
    */
  final def SampleRange( range: (Int, Int, Int) )                       = new SampleRange(start = range._1, last = range._2, step = range._3, segment = -1)
  /** Scala-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
    *
    * @param range Tuple containing start, end, and step
    * @param segment Which segment to read from
    */
  final def SampleRange( range: (Int, Int, Int), segment: Int )         = new SampleRange(start = range._1, last = range._2, step = range._3, segment)
  /** Java-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
    *
    * @param range Array containing start, end, and optionally, step
    * @param segment Which segment to read from
    */
  final def SampleRange( range: Array[Int], segment: Int ): SampleRangeSpecifier =
    nounou.elements.ranges.SampleRange.convertArrayToSampleRange(range, segment)
  /** Java-based signature alias for [[SampleRange(start:Int,last:Int,step:Int,segment:Int* SampleRange(start: Int, last: Int, step: Int, segment: Int)]]
    *
    * @param range Array containing start, end, and optionally, step. segment = -1 is assumed.
    */
  final def SampleRange( range: Array[Int] ): SampleRangeSpecifier = SampleRange( range, -1 )

  final def SampleRangeAll(step: Int, segment: Int) = new SampleRangeAll(step, segment)
  final def SampleRangeAll() = new SampleRangeAll(1, -1)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" RangeTs ">

  final def SampleRangeTs(startTs: Long, endTS: Long, stepTS: Long): SampleRangeTs =
    new SampleRangeTs(startTs, endTS, stepTS)
  final def FrameRangeTs(startTs: Long, endTS: Long): SampleRangeTs =
    new SampleRangeTs(startTs, endTS, -1L)

//  final def RangeTs(stamps: Array[Long], preTS: Long, postTS: Long): Array[ranges.RangeTs] =
//    stamps.map( (s: Long) => ranges.RangeTs(s-preTS, s+postTS) )

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
