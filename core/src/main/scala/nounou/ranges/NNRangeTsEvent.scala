package nounou.ranges

import nounou.elements.traits.NNTiming


/** Encapsulates a range based on one trigger Ts (timestamp in BigInt), with an event range specified with frames,
  * before and after the trigger Ts.
  *
  * Programming note: this class closely mirrors NNRangeEvent, so repeat yourself for any changes
  *
 * @author ktakagaki
 */
class NNRangeTsEvent(val triggerTs: BigInt, val startOffset: Int, val lastOffset: Int, val step: Int) extends NNRangeSpecifier {

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

  override def toString() = s"NNRangeTsEvent(triggerTs=$triggerTs, startOffset=$startOffset, lastOffset=$lastOffset, step=$step)"

  ///////////////////////////////////////////////////////////////////////////
  // NNRangeSpecifier methods
  ///////////////////////////////////////////////////////////////////////////

  //No need for check, since step is already instantiated
  override def getInstantiatedStep(nnTiming: NNTiming): Int = step

  override def getInstantiatedSegment(nnTiming: NNTiming): Int = {
    nnTiming.convertTsToFrsg(triggerTs)._2
  }

  override final def getInstantiatedRange(nnTiming: NNTiming): NNRangeInstantiated = {
    //frameSegmentBufferRefresh(nnTiming)
    val triggerFrsg = nnTiming.convertTsToFrsg(triggerTs)
    new NNRangeInstantiated( triggerFrsg._1 + startOffset,
                             triggerFrsg._1 + lastOffset,
                             getInstantiatedStep( nnTiming ),
                             triggerFrsg._2 )
}

  override final def getValidRange(nnTiming: NNTiming): NNRangeValid =
    getInstantiatedRange(nnTiming).getValidRange(nnTiming)

  ///////////////////////////////////////////////////////////////////////////
  // frameSegmentBuffer related
  ///////////////////////////////////////////////////////////////////////////

//  protected var timingBuffer: NNTiming = null
//  protected var segmentBuffer = -1
//  protected var triggerFrameBuffer = -1
//
//  protected def frameSegmentBufferRefresh(nnTiming: NNTiming): Unit = {
//    if( timingBuffer != nnTiming || segmentBuffer == -1) {
//      timingBuffer = nnTiming
//      val temp = nnTiming.convertTsToFrsg(triggerTs)
//      triggerFrameBuffer = temp._1
//      segmentBuffer = temp._2
//    }
//  }


}