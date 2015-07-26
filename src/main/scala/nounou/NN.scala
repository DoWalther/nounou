package nounou

//import breeze.linalg.DenseVector

import java.util.ServiceLoader

import com.google.gson.Gson
import scala.collection.JavaConverters._
import breeze.linalg.DenseVector
import nounou.elements.NNElement
import nounou.elements.data.{NNDataChannelArray, NNDataChannel, NNData}
import nounou.elements.data.filters.NNDataFilterMedianSubtract
import nounou.io.{FileLoaderNone, FileLoader}
import nounou.elements.ranges._
//import nounou.io.FileLoader._
import nounou.util.{LoggingExt, NNGit}

import scala.collection.mutable


/**A static class which encapsulates convenience functions for using nounou, with
 * an emphasis on use from Mathematica/MatLab/Java (avoidance of Java-unfriendly Scala constructs)
  *
 * @author ktakagaki
 * //@date 2/17/14.
 */
object NN extends LoggingExt {

  override final def toString(): String =
      "Welcome to nounou, a Scala/Java adapter for neurophysiological data.\n" +
      NNGit.infoPrintout

  // <editor-fold defaultstate="collapsed" desc=" file loading ">

  /** List of valid loaders available in the system (from META-INF)
    */
  private lazy val loaders = ServiceLoader.load(classOf[FileLoader]).iterator.asScala
  private val possibleLoaderBuffer = new mutable.HashMap[String, FileLoader]()

  /** This singleton FileLoader object is the main point of use for file loading.
    * It maintains a list of available loaders in the system (from Meta-Inf)
    * and uses the first valid loader to realize the [[FileLoader.load]] functions.
    */
  final def load(fileName: String): Array[NNElement] = {

    val fileExtension = nounou.util.getFileExtension(fileName)

    val loader = possibleLoaderBuffer.get(fileExtension) match {

      //If the loader for this extension has already been loaded
      //This includes the case where no real loader was found for a given extension, and FileLoaderNull was loaded as a marker
      case l: Some[FileLoader] => l.get

      //If the given extension has not been tested yet, it will be searched for within the available loaders
      case _ => {
        val possibleLoaders: Iterator[FileLoader] = loaders.filter( _.canLoadFile(fileName))
        val possibleLoader = if( possibleLoaders.hasNext ){
          val tempret = possibleLoaders.next
          if( possibleLoaders.hasNext ) {
            logger.info(s"Multiple possible loaders for file $fileName found. Will take first instance, ${tempret.getClass.getName}")
          }
          tempret
        } else {
          throw loggerError(s"Cannot find loader for file: $fileName")
        }
        possibleLoaderBuffer.+=( (fileExtension, possibleLoader) )
        possibleLoader
      }
    }
    loader.load(fileName)
  }

  final def load(fileNames: Array[String]): Array[NNElement] = {

    var tempElements = fileNames.flatMap( load(_) ).toVector

    //filters out NNDataChannel objects and joins them into one NNData if they are compatible
    val tempElementsNNDC = tempElements.filter(_.isInstanceOf[NNDataChannel])
    if( tempElementsNNDC.length > 1 ){
      if( tempElementsNNDC(0).isCompatible(tempElementsNNDC.tail) ) {
        tempElements = tempElements.filter(!_.isInstanceOf[NNDataChannel]).+:(
          new NNDataChannelArray(tempElementsNNDC.map(_.asInstanceOf[NNDataChannel]))
        )} else {
        loggerError("multiple files containing data channels were not compatible with each other!")
      }
    }

    tempElements.toArray

  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" options ">

  def OptNull() = nounou.OptNull

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" frame ranges ">

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

  final def SampleRangeTs(startTs: Long, endTS: Long, stepTS: Long): SampleRangeTS =
    new SampleRangeTS(startTs, endTS, stepTS)
  final def FrameRangeTs(startTs: Long, endTS: Long): SampleRangeTS =
    new SampleRangeTS(startTs, endTS, -1L)

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


//  // <editor-fold defaultstate="collapsed" desc=" RangeTsEvent ">
//
//  def RangeTsEvent(eventTs: Long, preFrames: Int, postFrames: Int) =
//    ranges.RangeTsEvent(eventTs, preFrames, postFrames)
//
//  def RangeTsEvent(eventTs: Array[Long], preFrames: Int, postFrames: Int) =
//    ranges.RangeTsEvent(eventTs, preFrames, postFrames)
//
//  // </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" RangeMs ">

////  final def RangeMs(startMs: Double, lastMs: Double, stepMs: Double, optSegment: OptSegment) =
////    ranges.RangeMs(startMs, lastMs, stepMs, optSegment)
//  final def RangeMs(startMs: Double, lastMs: Double, stepMs: Double) =
//    ranges.RangeMs(startMs, lastMs, stepMs)
////  final def RangeMs(startMs: Double, lastMs: Double, optSegment: OptSegment) =
////    ranges.RangeMs(startMs, lastMs, optSegment)
//  final def RangeMs(startMs: Double, lastMs: Double)=
//    ranges.RangeMs(startMs, lastMs)

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" RangeMsEvent ">

//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double, stepMs: Double, optSegment: OptSegment) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs, optSegment)
//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double, optSegment: OptSegment) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, optSegment)
//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double, stepMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs)
//  final def RangeMsEvent(eventMs: Double, preMs: Double, postMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs)
////  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double, optSegment: OptSegment) =
////    ranges.RangeMsEvent(eventMs, preMs, postMs, optSegment)
//  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs)
////  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double, stepMs: Double, optSegment: OptSegment) =
////    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs, optSegment)
//  final def RangeMsEvent(eventMs: Array[Double], preMs: Double, postMs: Double, stepMs: Double) =
//    ranges.RangeMsEvent(eventMs, preMs, postMs, stepMs)

// </editor-fold>
