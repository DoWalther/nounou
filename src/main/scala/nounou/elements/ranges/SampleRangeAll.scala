package nounou.elements.ranges

import nounou.elements.NNDataTiming


class SampleRangeAll(val step: Int, val segment: Int) extends SampleRangeSpecifier {

  override def toString() = s"RangeFrAll(step=$step, segment=$segment)"
  override final def getRealSegment(xDataTiming: NNDataTiming) = xDataTiming.getRealSegment(segment)

  override final def getRealStep(xFrames: NNDataTiming): Int = {
    if ( step == -1 ) 1 else step
  }
  override final def getSampleRangeReal(xFrames: NNDataTiming): SampleRangeReal = {
    new SampleRangeValid( 0, xFrames.segmentLength(getRealSegment(xFrames)), getRealStep(xFrames), segment)
  }
  override final def getSampleRangeValid(xFrames: NNDataTiming): SampleRangeValid = getSampleRangeValid(xFrames)
  override final def getSampleRangeValidPrePost(xFrames: NNDataTiming): (Int, SampleRangeValid, Int) =
    (0, getSampleRangeValid(xFrames), 0)

}

