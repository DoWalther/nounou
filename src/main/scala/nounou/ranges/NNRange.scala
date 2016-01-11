package nounou.ranges

import nounou._
import nounou.elements.traits.NNTiming
import nounou.util.LoggingExt

object NNRange {

  def convertArrayToSampleRange(array: Array[Int], segment: Int): NNRangeSpecifier = {

    loggerRequire(array != null, "Input array cannot be null!")
    array.length match {
      case 0 => new NNRangeAll(1, segment)
      //case 1 => RangeFrAll(array(0))
      case 2 => new NNRange(array(0), array(1), 1,        segment)
      case 3 => new NNRange(array(0), array(1), array(2), segment)
      case _ => throw loggerError(s"input array cannot be ${array.length}, it must be 0, 2 or 3")
    }

  }

}

/** NNRange is the central class for specifying frame-based
  * data sample timepoints (trait [[nounou.ranges.NNRangeSpecifier NNRangeSpecifier]]).
  * Various constructor methods for this and other [[nounou.ranges.NNRangeSpecifier NNRangeSpecifier]]s
  * are located within the convenience class [[nounou.NN]].
  *
  * @see [[nounou.ranges.NNRangeInstantiated NNRangeInstantiated]] marked to be a real range (not default, etc.)
  * @see [[nounou.ranges.NNRangeValid NNRangeValid]] marked to be a valid range (not outside of defined data)
  * @see [[nounou.ranges.NNRangeAll NNRangeAll]] marker for total sample range
  * @see [[nounou.ranges.NNRangeTs NNRangeTs]] range defined in timestamp units
  *
  * @author ktakagaki
  * //@date 2/9/14.
  */
class NNRange(val start: Int, val last: Int, val step: Int, val segment: Int)
  extends NNRangeSpecifier with LoggingExt {

  override def toString() = s"FrameRange($start, $last, step=$step, segment=$segment)"

  loggerRequire( start <= last, s"FrameRange requires start <= last. start=$start, last=$last")
  loggerRequire( step >= 1 || step == -1, s"step must be -1 (automatic) or positive. Invalid value: $step")
  loggerRequire( segment >= -1, s"segment must be -1 (automatic first segment) or positive. Invalid value: $segment")

  // <editor-fold defaultstate="collapsed" desc=" range info accessors ">

  override final def getInstantiatedRange(timing: NNTiming): NNRangeInstantiated = {
    val realSegment = getInstantiatedSegment(timing)
    if(0 <= start){
      val segmentLength = timing.segmentLength(realSegment)
      if( last < segmentLength){
        new NNRangeValid( start, last, getInstantiatedStep(timing), realSegment)
      }
      else new NNRangeInstantiated( start, last, getInstantiatedStep(timing), realSegment)
    }
    else new NNRangeInstantiated( start, last, getInstantiatedStep(timing), realSegment)
  }

  override final def getValidRange(timing: NNTiming): NNRangeValid = {
    new NNRangeValid( firstValid(timing), lastValid(timing), getInstantiatedStep(timing), getInstantiatedSegment(timing) )
  }

  override final def getRangeValidPrePost(timing: NNTiming): (Int, NNRangeValid, Int) = {
    val totalLength =  timing.segmentLength( getInstantiatedSegment(timing) )
    val preL = preLength( totalLength )
    val postL = postLength( totalLength )
    (preL, getValidRange(timing), postL)
  }

  /** Read -1 as the default value for the timing.
    */
  override final def getInstantiatedStep(timing: NNTiming): Int =  if ( step == -1 ) 1 else step

  override final def getInstantiatedSegment(timing: NNTiming) = timing.getRealSegment( segment )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" utility functions ">

  // <editor-fold defaultstate="collapsed" desc=" protected utility functions: intervalContains/intervalMod ">

  protected[ranges] def intervalContains(start: Int, end: Int, step: Int): Int = {
    if (start > end) 0
    else if (start == end) 1
    else (end - start)/step + 1
  }

  /** How many units to:
    * - add to the end (when counting from start) or
    * - to subtract from the start (when counting backwards from end)
    * to get to the next step value
    */
  protected[ranges] def intervalMod(start: Int, end: Int, step: Int): Int = {
    if (start > end) Int.MinValue
    else if (start == end) step
    else intervalContains(start, end, step) * step - (end - start)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" first/lastValid ">

  /** Inclusive first valid frame, taking into account step and overhang
    */
  def firstValid(xDataTiming: NNTiming): Int = firstValid(xDataTiming.segmentLength(getInstantiatedSegment(xDataTiming)))
  private var fvBuffTL = -1
  private var fvBuff = - 156111
  /** Inclusive first valid frame, taking into account step and overhang
    */
  def firstValid(totalLength: Int) = {
    if( fvBuffTL == totalLength ) fvBuff
    else {
      fvBuff = firstValidImpl(totalLength)
      fvBuffTL = totalLength
      fvBuff
    }
  }
  private def firstValidImpl(totalLength: Int) = {
    if(start >= totalLength ) Int.MaxValue //no valid values
    else if( start >= 0 ) start
    else if( step == 1 ) 0
    else {
      val temp = intervalMod(start, -1, step) - 1
      if( temp < totalLength ) temp
      else Int.MaxValue //no valid values
    }
  }
  /** Inclusive last valid frame, taking into account step and overhang
    */
  def lastValid(xDataTiming: NNTiming): Int = lastValid(xDataTiming.segmentLength(getInstantiatedSegment(xDataTiming)))
  /** Valid lastValid frame, taking into account step and overhang
    */
  private var lvBuffTL = -1
  private var lvBuff = - 156112
  /** Inclusive last valid frame, taking into account step and overhang
    */
  def lastValid(totalLength: Int) = {
    if( lvBuffTL == totalLength ) lvBuff
    else {
      lvBuff = lastValidImpl(totalLength)
      lvBuffTL = totalLength
      lvBuff
    }
  }
  def lastValidImpl(totalLength: Int) = {
    if(last < 0 ) Int.MinValue //no valid values
    else if( 0 == last ) {
      val temp = firstValid(totalLength)
      if( temp == 0 ) 0
      else Int.MinValue //no valid values
    }
    else if( /*0 < end*/ last < totalLength ) {
      val fv = firstValid(totalLength)
      if (fv == last) fv
      else {
        val temp = intervalContains(fv, last, step)
        if (temp > 0) fv + (temp - 1) * step
        else Int.MinValue //no valid values
      }
    }
    else { //totalLength <= end
      if( step == 1 ) totalLength - 1
      else {
        val fv = firstValid(totalLength)
        val temp = intervalContains(fv, totalLength - 1, step)
        if (temp > 0) fv + (temp - 1) * step
        else Int.MinValue //no valid values
      }
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" length ">

  /** range length, can include zero padding if start<0 or totalLength<=end
    */
  def length(totalLength: Int): Int = intervalContains(start, last, step)

  /**range length, can include zero padding if start<0 or totalLength<=end
    */
  def length(xDataTiming: NNTiming): Int = xDataTiming.segmentLength(getInstantiatedSegment(xDataTiming))

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" preLength/postLength ">

  /** How many points to pad at the beginning given the valid data range.
    */
  def preLength(totalLength: Int): Int = {
    if( start >= 0 ) 0    //all post padding or no padding
    else if (last < 0) { //all pre padding
      (last - start)/step + 1
    } else{
      intervalContains(start, -1, step)
    }
  }

  /** How many points to pad at the end given the valid data range.
    */
  def postLength(totalLength: Int): Int = {
    if( start >= totalLength )  (last - start)/step + 1   //all post padding
    else if (last < totalLength) 0   //all pre padding or no padding
    else{
      intervalContains(lastValid(totalLength), last, step) - 1
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" isFullyValid ">

  /** Whether the frame range is completely contained within available data.
    * @param xFrames data object to which to apply the frames
    */
  def isFullyValid(xFrames: NNTiming): Boolean = isFullyValid( xFrames.segmentLength(getInstantiatedSegment(xFrames)) )

  /** Whether the frame range is completely contained within available data.
    * @param totalLength full length of this segment in frames, used to realize with RangeFr.all()
    */
  def isFullyValid(totalLength: Int): Boolean = {
      firstValid(totalLength) <= start && last <= lastValid(totalLength) //< totalLength
  }

  // </editor-fold>

  // </editor-fold>

}

/**Extends FrameRange but with the enforced assumption that all step and segment are instantiated.
  * start and last need not be within the given data range (need not be "valid").
 */
class NNRangeInstantiated(val start: Int, val last: Int, val step: Int, val segment: Int) extends NNRangeSpecifier {

  override def toString() = s"NNRangeInstantiated($start, $last, step=$step, segment=$segment)"
  loggerRequire(start <= last, s"start($start) should be < last($last)")
  loggerRequire(1 <= step, s"step($step) should be >= 1")
  loggerRequire(0 <= segment, s"segment($segment) should be >= 0")

  def length() = (last-start)/step + 1

  // <editor-fold defaultstate="collapsed" desc=" NNRangeSpecifier methods ">

  override final def getInstantiatedSegment(xDataTiming: NNTiming): Int = segment
  override final def getInstantiatedStep(xDataTiming: NNTiming): Int = step
  override final def getInstantiatedRange(xDataTiming: NNTiming): NNRangeInstantiated = this

  override def getValidRange(xDataTiming: NNTiming): NNRangeValid =
    (new NNRange(start, last, step, segment)).getValidRange(xDataTiming)
  override def getRangeValidPrePost(xDataTiming: NNTiming): (Int, NNRangeValid, Int) =
    (new NNRange(start, last, step, segment)).getRangeValidPrePost(xDataTiming)

  // </editor-fold>

}

/**Extends FrameRange but with the implicit assumption that start and last are within given data range,
  * and that step and segment are instantiated.
  */
class NNRangeValid(override val start: Int, override val last: Int, override val step: Int, override val segment: Int)
  extends NNRangeInstantiated(start, last, step, segment) {

  loggerRequire(0 <= start, s"Start $start must be >= 0")

  override def toString() = s"SampleRangeValid($start, $last, step=$step, segment=$segment)"

  final def toRangeInclusive() = new Range.Inclusive(start, last, step)
  final def toRangeInclusive(increment: Int) = new Range.Inclusive(start + increment, last + increment, step)

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier ">

  override def getValidRange(xDataTiming: NNTiming): NNRangeValid = this
  override def getRangeValidPrePost(xDataTiming: NNTiming): (Int, NNRangeValid, Int) = (0, this, 0)

  // </editor-fold>

  //The following override is not necessary, since valid sample ranges give themselves back with getSampleRangeReal
//  override def readSampleRangeTimesFr(nnDataTiming: NNDataTiming): Array[Int] ={
//    val tempValid = getSampleRangeValid(nnDataTiming: NNDataTiming)
//    (for(c <- tempValid.start to tempValid.last by tempValid.step ) yield c).toArray
//  }

}
