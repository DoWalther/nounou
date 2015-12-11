package nounou.elements.ranges

import java.math.BigInteger

import nounou.elements._timing.{NNDataTiming, NNDataTimingElement}
import nounou.util.LoggingExt

/** This trait specifies a range of data samples to extract, for instance, when reading data traces.
  * It allows specification of ranges such as "all samples"
  * ([[nounou.elements.ranges.SampleRangeAll SampleRangeAll]]) and millisecond- or timestamp(Long)-
  * dependent sample ranges. These latter specifications can only be resolved to real data frame
  * ranges using sampling information given in the actual data
  * ([[nounou.elements._timing.NNDataTiming]]).
  */
trait SampleRangeSpecifier extends LoggingExt {

  /** Returns the real segment number for the frame range, taking into account -1 for automatic determination.
    */
  def getRealSegment(xDataTiming: NNDataTiming): Int
  final def getRealSegment(xDataTimingElement: NNDataTimingElement): Int
    = getRealSegment(xDataTimingElement.timing())

  /** Returns the real step for the sample range in units of single data samples,
    * taking into account -1 for automatic determination.
    */
  def getRealStep(nnDataTiming: NNDataTiming): Int
  final def getRealStep(nnDataTimingElement: NNDataTimingElement): Int =
    getRealStep(nnDataTimingElement.timing())

  /** Returns the concrete real sample range with start (can be negative, starting before the data),
    * end (can be beyond end of assumed data as specified in [[nounou.elements._timing.NNDataTiming NNDataTiming]]),
    * steps (must be positive int), and segment (present within assumed data).
    */
  def getSampleRangeReal(nnDataTiming: NNDataTiming): SampleRangeReal
  final def getSampleRangeReal(nnDataTimingElement: NNDataTimingElement): SampleRangeReal =
    getSampleRangeReal(nnDataTimingElement.timing())

  /** Returns the concrete valid sample range with start/end (within available data, cannot overhang),
    * steps (must be positive int), and segment (present within assumed data).
    * In contrast to
    * [[nounou.elements.ranges.SampleRangeSpecifier.getSampleRangeReal(nnDataTiming:nounou\.elements\._timing\.NNDataTiming* getSampleRangeReal(NNDataTiming)]],
    * the resulting sample range here cuts off overhangs.
    */
  def getSampleRangeValid(nnDataTiming: NNDataTiming): SampleRangeValid
  final def getSampleRangeValid(nnDataTimingElement: NNDataTimingElement): SampleRangeValid =
    getSampleRangeValid(nnDataTimingElement.timing())

  /** Returns [[nounou.elements.ranges.SampleRangeSpecifier.getSampleRangeValid(nnDataTiming:* getSampleRangeValid]],
    * along with pre- and post- padding sample counts
    * for when the original sample range exceeds/overhangs the available data.
    */
  def getSampleRangeValidPrePost(nnDataTiming: NNDataTiming): (Int, SampleRangeValid, Int) =
    getSampleRangeValid(nnDataTiming).getSampleRangeValidPrePost(nnDataTiming)
  final def getSampleRangeValidPrePost(xDataTimingElement: NNDataTimingElement): (Int, SampleRangeValid, Int) =
    getSampleRangeValidPrePost(xDataTimingElement.timing())

  // </editor-fold>

  def getSampleRangeTimesFr(nnDataTiming: NNDataTiming): Array[Int] ={
    val tempReal = getSampleRangeReal(nnDataTiming: NNDataTiming)
    (for(c <- tempReal.start to tempReal.last by tempReal.step ) yield c).toArray
  }
  final def getSampleRangeTimesFr(nnDataTimingElement: NNDataTimingElement): Array[Int] =
    getSampleRangeTimesFr(nnDataTimingElement.timing())

  def getSampleRangeTimesMs(nnDataTiming: NNDataTiming): Array[Double] ={
    val tempReal = getSampleRangeReal(nnDataTiming: NNDataTiming)
    val tempRet = getSampleRangeTimesFr(nnDataTiming)
    tempRet.map( nnDataTiming.convertFrToMs(_))
  }
  final def getSampleRangeTimesMs(nnDataTimingElement: NNDataTimingElement): Array[Double] =
    getSampleRangeTimesMs(nnDataTimingElement.timing())

  def getSampleRangeTimesTs(nnDataTiming: NNDataTiming): Array[BigInteger] ={
    val tempReal = getSampleRangeReal(nnDataTiming: NNDataTiming)
    val tempRet = getSampleRangeTimesFr(nnDataTiming)
    tempRet.map( nnDataTiming.convertFrsgToTs(_, tempReal.segment).bigInteger )
  }
  final def getSampleRangeTimesTs(nnDataTimingElement: NNDataTimingElement): Array[BigInteger] =
    getSampleRangeTimesTs(nnDataTimingElement.timing())

}