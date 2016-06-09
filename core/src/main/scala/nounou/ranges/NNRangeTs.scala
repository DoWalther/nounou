package nounou.ranges

import java.math.BigInteger

import nounou.elements.traits.NNTiming


/**Encapsulates a Ts (timestamp in BigInt)-based frame range, with appropriate values.
 *
 * @author ktakagaki
 * //@date 3/19/14.
 */
class NNRangeTs(val startTs: BigInt, val lastTs: BigInt, val stepTs: BigInt) extends NNRangeSpecifier {

  def this(startTs: BigInt, lastTs: BigInt) = this(startTs, lastTs, BigInt(-1))

  loggerRequire( startTs <= lastTs, s"SampleRangeTs requires startTs <= lastTs. startTs=$startTs, lastTs=$lastTs")
  loggerRequire( stepTs >= 1 || stepTs == -1, s"step must be -1 (automatic) or positive. Invalid value: $stepTs")

  override def toString() = s"SampleRangeTs($startTs, $lastTs, $stepTs)"

  def this(startTs: BigInteger, lastTs: BigInteger, stepTs: BigInteger) {
    this( BigInt(startTs), BigInt(lastTs), BigInt(stepTs) )
  }

  // <editor-fold defaultstate="collapsed" desc=" RangeFrSpecifier methods ">
    
  override def getInstantiatedSegment(xDataTiming: NNTiming): Int = {
    realSegmentBufferRefresh(xDataTiming)
    realSegmentBuffer
  }

  private var realStepFramesBuffer = -1
  override def getInstantiatedStep(xDataTiming: NNTiming): Int = {
    if(realStepFramesBuffer == -1) {
      realStepFramesBuffer =
        if (stepTs == -1L) 1
        else {
          val stepReal = (stepTs.toDouble * xDataTiming.sampleRate / 1000000d).toInt
          loggerRequire(stepReal > 0, "This amounts to a negative time step! (stepTs=" + stepTs + " micro s => " + stepReal + " frames)")
          stepReal
        }
    }
    realStepFramesBuffer
  }
  override final def getInstantiatedRange(nnTiming: NNTiming): NNRangeValid = {
    //ToDo: This is not strictly correct? Should the instantiated range be shorter if overhang?
    getValidRange(nnTiming)
    //realSegmentBufferRefresh(nnTiming)
    //Range.inclusive( realStartFrameBuffer, realLastFrameBuffer, getRealStep(nnTiming))
    //Range.inclusive( 0, nnTiming.segmentLength(getRealSegment(nnTiming)), getRealStep(nnTiming))
  }

  override final def getValidRange(xDataTiming: NNTiming): NNRangeValid = {
    realSegmentBufferRefresh(xDataTiming)
    new NNRangeValid( realStartFrameBuffer, realLastFrameBuffer, getInstantiatedStep(xDataTiming), realSegmentBuffer )
//    realSegmentBufferRefresh(nnTiming)
//    val realSegment = new SampleRangeReal(realStartFrameBuffer, realLastFrameBuffer, getRealStep(nnTiming), realSegmentBuffer)
//    realSegment.getSampleRangeValid(nnTiming)
  }
  
  // </editor-fold>

  private var timingBuffer: NNTiming = null
  private var realSegmentBuffer = -1
  private var realStartFrameBuffer = -1
  private var realLastFrameBuffer = -1

  private def realSegmentBufferRefresh(nnTiming: NNTiming): Unit = {
    if( timingBuffer != nnTiming) {
      val fs1 = nnTiming.convertTsToFrsg(startTs)
      val fs2 = nnTiming.convertTsToFrsg(lastTs)
      loggerRequire(fs1._2 == fs2._2, "The two specified timestamps belong to different recording segments " +
        fs1._2.toString + " and " + fs2._2.toString)
      timingBuffer = nnTiming
      realSegmentBuffer = fs1._2

      val tempLen = nnTiming.segmentLength( realSegmentBuffer )
      if(fs1._1 < 0 || fs2._1 > tempLen )
        logger.warn("The TS specified frames [{}, {}] are out of range, this might be unintended.", fs1.toString(), fs2.toString())
      realStartFrameBuffer = fs1._1
      realLastFrameBuffer  = fs2._1
      //println("Real segment buff " + realSegmentBuffer.toString )
    }
  }


}