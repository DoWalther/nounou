package nounou.elements.data

import nounou.elements.ranges.SampleRangeReal
import nounou.elements.spikes.NNSpike

/**
 * Created by ktakagaki on 15/09/03.
 */
trait NNDataSpikeReader {

  //self type infers that this trait assumes mixin to NNData
  this: NNData =>

  /**Reads a [[nounou.elements.spikes.NNSpike]] object from the data
   */
  def readSpike(channel: Int, frame: Int, segment: Int, spikeLength: Int): NNSpike = {
    loggerRequire( 0 < spikeLength && spikeLength < 100, s"spikeLength=$spikeLength should have been (0, 100]" )

    new NNSpike(
      /*timestamp =*/ timing().convertFrsgToTs(frame, segment),
      /*waveform =*/ readTraceInt(channel, new SampleRangeReal(frame, frame + spikeLength - 1, step = 1, segment)   ),
      /*channels =*/ 1
    )
  }
  /**Reads a [[nounou.elements.spikes.NNSpike]] object from the data
    */
  def readSpike(channels: Array[Int], frame: Int, segment: Int, spikeLength: Int): NNSpike = {
    loggerRequire( 0 < spikeLength && spikeLength < 100, s"spikeLength=$spikeLength should have been (0, 100]" )

    new NNSpike(
      /*timestamp =*/ timing().convertFrsgToTs(frame, segment),
      /*waveform =*/ readTraceInt(channels, new SampleRangeReal(frame, frame + spikeLength - 1, step = 1, segment)   ).flatten,
      /*channels =*/ channels.length
    )
  }

}
