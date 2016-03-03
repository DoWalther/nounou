package nounou.ranges

import nounou.elements.data.traits.NNTiming


/**Encapsulates a range based on one trigger frame and segment, with an event range specified with frames,
  * before and after the trigger Ts.
  *
  * Programming note: this class closely mirrors NNRangeTsEvent, so repeat yourself for any changes
  *
 * @author ktakagaki
 */
class NNRangeEvent(val trigger: Int, val startOffset: Int, val lastOffset: Int, val step: Int, val segment: Int) extends NNRangeSpecifier {

  ///////////////////////////////////////////////////////////////////////////
  // Default constructor checks
  ///////////////////////////////////////////////////////////////////////////

  loggerRequire( startOffset <= lastOffset, s"startOffset($startOffset) should be <= to lastOffset($lastOffset)")
  if( startOffset > 0 ) logger.info(s"using startOffset of $startOffset, which is >0. This may be unintended")
//  loggerRequire( startOffset >=0, s"framesPre ($startOffset) should be a >=0 number of frames")
//  loggerRequire( lastOffset >=0, s"framesPost ($lastOffset) should be a >=0 number of frames")
  loggerRequire( step >=1, s"step ($step) should be a >=1 step specification")

  ///////////////////////////////////////////////////////////////////////////
  // toString related
  ///////////////////////////////////////////////////////////////////////////

  override def toString() = s"NNRangeEvent($trigger, startOffset=$startOffset, lastOffset=$lastOffset, step=$step, segment=$segment)"

  ///////////////////////////////////////////////////////////////////////////
  // NNRangeSpecifier methods
  ///////////////////////////////////////////////////////////////////////////

  override def getInstantiatedStep(nnTiming: NNTiming): Int = {
    frameSegmentBufferRefresh(nnTiming)
    instantiatedBuffer.getInstantiatedStep(nnTiming)
  }

  override def getInstantiatedSegment(nnTiming: NNTiming): Int = {
    frameSegmentBufferRefresh(nnTiming)
    instantiatedBuffer.getInstantiatedSegment(nnTiming)
  }

  override final def getInstantiatedRange(nnTiming: NNTiming): NNRangeInstantiated = {
    frameSegmentBufferRefresh(nnTiming)
    instantiatedBuffer
  }

  override final def getValidRange(nnTiming: NNTiming): NNRangeValid = {
    frameSegmentBufferRefresh(nnTiming)
    instantiatedBuffer.getValidRange(nnTiming)
  }

  ///////////////////////////////////////////////////////////////////////////
  // frameSegmentBuffer related
  ///////////////////////////////////////////////////////////////////////////

  protected var timingBuffer: NNTiming = null
  protected var instantiatedBuffer: NNRangeInstantiated = null

  protected def frameSegmentBufferRefresh(nnTiming: NNTiming): Unit = {
    if( timingBuffer != nnTiming || instantiatedBuffer == null) {
      timingBuffer = nnTiming
      instantiatedBuffer = (new NNRange(trigger+startOffset, trigger+lastOffset, step, segment)).getInstantiatedRange(nnTiming)
    }
  }


}