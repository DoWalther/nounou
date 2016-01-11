package nounou.ranges

import java.math.BigInteger

import nounou.elements.traits.NNTiming


/**Encapsulates a range based on one trigger Ts (timestamp in BigInt), with an event range specified with frames,
  * before and after the trigger Ts.
 * @author ktakagaki
 */
class NNRangeTsEvent(val triggerTs: BigInt, val startOffset: Int, val lastOffset: Int, val step: Int) extends NNRangeSpecifier {

  loggerRequire( startOffset <= lastOffset, s"startOffset($startOffset) should be <= to lastOffset($lastOffset)")
  if( startOffset > 0 ) logger.info(s"using startOffset of $startOffset, which is >0. This may be unintended")
//  loggerRequire( startOffset >=0, s"framesPre ($startOffset) should be a >=0 number of frames")
//  loggerRequire( lastOffset >=0, s"framesPost ($lastOffset) should be a >=0 number of frames")
  loggerRequire( step >=1, s"step ($step) should be a >=1 step specification")

  override def toString() = s"NNRangeTsEvent($triggerTs, startOffset=$startOffset, lastOffset=$lastOffset, step=$step)"


  // <editor-fold defaultstate="collapsed" desc=" NNRangeSpecifier methods ">

  //No need for check, since step is already instantiated
  override def getInstantiatedStep(nnTiming: NNTiming): Int = step

  override def getInstantiatedSegment(nnTiming: NNTiming): Int = {
    frameSegmentBufferRefresh(nnTiming)
    segmentBuffer
  }

  override final def getInstantiatedRange(nnTiming: NNTiming): NNRangeInstantiated = {
    frameSegmentBufferRefresh(nnTiming)
    new NNRangeInstantiated(triggerFrameBuffer + startOffset,
                            triggerFrameBuffer + lastOffset,
                            step,
                            segmentBuffer)
  }

  override final def getValidRange(nnTiming: NNTiming): NNRangeValid =
    getInstantiatedRange(nnTiming).getValidRange(nnTiming)
  
  // </editor-fold>

  private var timingBuffer: NNTiming = null
  private var segmentBuffer = -1
  private var triggerFrameBuffer = -1

  private def frameSegmentBufferRefresh(nnTiming: NNTiming): Unit = {
    if( timingBuffer != nnTiming || segmentBuffer == -1) {
      timingBuffer = nnTiming
      val temp = nnTiming.convertTsToFrsg(triggerTs)
      triggerFrameBuffer = temp._1
      segmentBuffer = temp._2
    }
  }


}