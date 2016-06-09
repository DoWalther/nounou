package nounou.ranges

import nounou.elements.traits.NNTiming


class NNRangeAll(val step: Int, val segment: Int) extends NNRangeSpecifier {

  override def toString() = s"NNRangeAll(step=$step, segment=$segment)"
  override final def getInstantiatedSegment(nnTiming: NNTiming) = nnTiming.getRealSegment(segment)

  override final def getInstantiatedStep(nnTiming: NNTiming): Int = {
    if ( step == -1 ) 1 else step
  }

  override final def getInstantiatedRange(nnTiming: NNTiming): NNRangeInstantiated = getValidRange(nnTiming)

  override final def getValidRange(nnTiming: NNTiming): NNRangeValid = {
    new NNRangeValid(
      0,
      nnTiming.segmentLength(getInstantiatedSegment(nnTiming))-1,
      getInstantiatedStep(nnTiming),
      getInstantiatedSegment(nnTiming))
  }

  override final def getRangeValidPrePost(nnTiming: NNTiming): (Int, NNRangeValid, Int) =
    (0, getValidRange(nnTiming), 0)


}

