package nounou.ranges

import java.math.BigInteger
import nounou.elements.traits.{NNTimingElement, NNTiming}
import nounou.util.LoggingExt

/**
  * This trait specifies a range of data samples to extract, for instance, when reading data traces.
  * It allows specification of ranges such as "all samples"
  * ([[nounou.ranges.NNRangeAll SampleRangeAll]]) and millisecond- or timestamp(Long)-
  * dependent sample ranges. These latter specifications can only be resolved to real data frame
  * ranges using sampling information given in the actual data
  * ([[nounou.elements.traits.NNTiming NNTiming]]).
  */
trait NNRangeSpecifier extends LoggingExt {

  /**
    * Returns the real segment number for the frame range, taking into account -1 for automatic determination.
    */
  def getInstantiatedSegment(nnTiming: NNTiming): Int
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.getInstantiatedSegment(nnTiming:* getInstantiatedSegment]]
    */
  final def getInstantiatedSegment(nnTimingElement: NNTimingElement): Int
    = getInstantiatedSegment(nnTimingElement.timing())

  /**
    * Returns the real step for the sample range in units of single data samples,
    * taking into account -1 for automatic determination.
    */
  def getInstantiatedStep(nnTiming: NNTiming): Int
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.getInstantiatedStep(nnTiming:* getInstantiatedStep]]
    */
  final def getInstantiatedStep(nnTimingElement: NNTimingElement): Int =
    getInstantiatedStep(nnTimingElement.timing())

  /**
    * Returns the concrete real sample range with start (can be negative, starting before the data),
    * end (can be beyond end of assumed data as specified in [[nounou.elements.traits.NNTiming NNTiming]]),
    * steps (must be positive int), and segment (present within assumed data).
    */
  def getInstantiatedRange(nnTiming: NNTiming): NNRangeInstantiated
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.getInstantiatedRange(nnTiming:* getSampleRangeInstantiated]]
    */
  final def getInstantiatedRange(nnTimingElement: NNTimingElement): NNRangeInstantiated =
    getInstantiatedRange(nnTimingElement.timing())

  /**
    * Returns the concrete valid sample range with start/end (within available data, cannot overhang),
    * steps (must be positive int), and segment (present within assumed data).
    * In contrast to
    * [[nounou.ranges.NNRangeSpecifier.getInstantiatedRange(nnTiming:nounou\.elements\.traits\.NNTiming* getSampleRangeReal(NNDataTiming)]],
    * the resulting sample range here cuts off overhangs.
    */
  def getValidRange(nnTiming: NNTiming): NNRangeValid
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.getValidRange(nnTiming:* getSampleRangeValid]]
    */
  final def getValidRange(nnDataTimingElement: NNTimingElement): NNRangeValid =
    getValidRange(nnDataTimingElement.timing())

  /**
    * Returns [[nounou.ranges.NNRangeSpecifier.getValidRange(nnTiming:* getSampleRangeValid]],
    * along with pre- and post- padding sample counts
    * for when the original sample range exceeds/overhangs the available data.
    */
  def getRangeValidPrePost(nnDataTiming: NNTiming): (Int, NNRangeValid, Int) =
    getInstantiatedRange(nnDataTiming).getRangeValidPrePost(nnDataTiming)
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.getRangeValidPrePost(nnDataTiming:* getSampleRangeValidPrePost]]
    */
  final def getRangeValidPrePost(nnTimingElement: NNTimingElement): (Int, NNRangeValid, Int) =
    getRangeValidPrePost(nnTimingElement.timing())

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" reading sample timepoints ">

  /**
    * Reads out a list of the timepoints specified by this NNRangeSpecifier in units of frames.
    * Default is to read all timepoints including overhang, but for override by NNRangeSpecifierValid,
    * only "valid" timepoints will be read.
    */
  final def readTimepoints(nnDataTiming: NNTiming): Array[Int] ={
    val tempInstantiated = getInstantiatedRange(nnDataTiming: NNTiming)
    (for(c <- tempInstantiated.start to tempInstantiated.last by tempInstantiated.step ) yield c).toArray
  }

  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.readTimepoints(nnDataTiming:* readSampleRangeTimes]]
    */
  final def readTimepoints(nnDataTimingElement: NNTimingElement): Array[Int] =
    readTimepoints(nnDataTimingElement.timing())

  /**
    * Reads out a list of the timepoints specified by this NNRangeSpecifier in units of ms.
    * Default is to read "real" timepoints including overhang, but for override by NNRangeSpecifierValid,
    * only "valid" timepoints will be read.
    */
  final def readTimepointsMs(nnDataTiming: NNTiming): Array[Double] ={
    readTimepoints(nnDataTiming).map( nnDataTiming.convertFrToMs(_))
  }
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.readTimepointsMs(nnDataTiming:* readSampleRangeTimesFr]]
    */
  final def readTimepointsMs(nnDataTimingElement: NNTimingElement): Array[Double] =
    readTimepointsMs(nnDataTimingElement.timing())

  /**
    * Reads out a list of the timepoints specified by this NNRangeSpecifier in units of timestamps.
    * Default is to read "real" timepoints including overhang, but for override by NNRangeSpecifierValid,
    * only "valid" timepoints will be read.
    */
  final def readTimepointsTs(nnDataTiming: NNTiming): Array[BigInteger] ={
    val realSegment = this.getInstantiatedSegment(nnDataTiming)
    readTimepoints(nnDataTiming).map( nnDataTiming.convertFrsgToTs(_, realSegment ).bigInteger )
  }
  /**
    * Alias for [[nounou.ranges.NNRangeSpecifier.readTimepointsTs(nnDataTiming:* readNNRangeTimepointsFr]]
    */
  final def readTimepointsTs(nnDataTimingElement: NNTimingElement): Array[BigInteger] =
    readTimepointsTs(nnDataTimingElement.timing())

  // </editor-fold>

}