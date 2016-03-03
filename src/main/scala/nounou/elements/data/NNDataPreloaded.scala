package nounou.elements.data

import breeze.linalg.{DenseMatrix => DM, DenseVector => DV}
import nounou.elements.data.traits.{NNTiming, NNScaling}
import nounou.elements.traits.NNTiming
import nounou.ranges.NNRangeValid

/**NNData class with internal representation as data array.
 */
class NNDataPreloaded(val data: Array[DV[Double]], timingEntry: NNTiming, scaleEntry: NNScaling)
  extends NNData  {

  loggerRequire(data != null, "input data must be non-null")

  setScale(scaleEntry)

  override val timing = timingEntry

  override def getChannelCount: Int = data.length

  override def readPointImpl(channel: Int, frame: Int, segment: Int) =
    data(channel)( timing.cumulativeFrame(frame, segment) )

  override def readTraceDVImpl(channel: Int, rangeFrValid: NNRangeValid) = {
    data( channel )(
      rangeFrValid.toRangeInclusive( timing.segmentStartFrame( rangeFrValid.segment ))
    )

  }

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl(): String = s"channels=$getChannelCount length=${data(0).size}"

  override def toStringFullImpl(): String = ""

  // </editor-fold>

}

//class NNDataPreloadedSingleSegment( data: Array[DV[Int]], scale: NNDataScale, timing: NNDataTiming,
////                    xBits: Int,
////                    absGain: Double,
////                    absOffset: Double,
////                    absUnit: String,
////                    scaleMax: Int,
////                    scaleMin: Int,
////                    //channelNames: Vector[String], // = Vector.tabulate[String](data.length)(i => "no channel name")
//                    segmentStartTs: Long,
//                    sampleRate: Double/*,
//                    layout: NNLayout = NNLayoutNull$$*/
//                    )
//  extends NNDataPreloaded( Array(data),
//                          xBits,
//                          absGain, absOffset, absUnit,
//                          scaleMax, scaleMin, /*channelNames,*/ Array[Long](segmentStartTs),
//                          Array[Int](data.rows),
//                          sampleRate/*, layout*/){
//  override lazy val segmentCount = 1
//
//}
