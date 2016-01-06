package nounou.elements.data

import breeze.linalg.{DenseMatrix => DM, DenseVector => DV}
import nounou.elements.traits.{NNTiming, NNDataScale}
import nounou.ranges.NNRangeValid

/**NNData class with internal representation as data array.
 */
class NNDataPreloaded(val data: Array[DV[Int]], timingEntry: NNTiming, scaleEntry: NNDataScale)
  extends NNData  {

    setScale(scaleEntry)
    override val timing = timingEntry

    override def getChannelCount: Int = data.length

    override def readPointIntImpl(channel: Int, frame: Int, segment: Int) =
      data(channel)(timing.cumulativeFrame(frame, segment))

    override def readTraceIntDVImpl(channel: Int, rangeFrValid: NNRangeValid) = {
      data( channel )(
              rangeFrValid.toRangeInclusive( timing.segmentStartFrame( rangeFrValid.segment ))
      )

    }

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
