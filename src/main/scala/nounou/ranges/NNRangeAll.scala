package nounou.ranges

import nounou.elements.traits.NNTiming


class NNRangeAll(val step: Int, val segment: Int) extends NNRangeSpecifier {

  override def toString() = s"RangeFrAll(step=$step, segment=$segment)"
  override final def getInstantiatedSegment(xDataTiming: NNTiming) = xDataTiming.getRealSegment(segment)

  override final def getInstantiatedStep(xFrames: NNTiming): Int = {
    if ( step == -1 ) 1 else step
  }
  override final def getInstantiatedRange(xFrames: NNTiming): NNRangeInstantiated = {
    new NNRangeValid( 0, xFrames.segmentLength(getInstantiatedSegment(xFrames)), getInstantiatedStep(xFrames), segment)
  }
  override final def getValidRange(xFrames: NNTiming): NNRangeValid = getValidRange(xFrames)
  override final def getRangeValidPrePost(xFrames: NNTiming): (Int, NNRangeValid, Int) =
    (0, getValidRange(xFrames), 0)


}

