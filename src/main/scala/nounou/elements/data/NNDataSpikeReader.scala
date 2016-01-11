package nounou.elements.data

import nounou.ranges.NNRangeInstantiated
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
      /*waveform =*/ readTrace( channel, new NNRangeInstantiated(frame, frame + spikeLength - 1, step = 1, segment)   ),
      /*channels =*/ 1,
      0L
    )
  }
  /**Reads a [[nounou.elements.spikes.NNSpike]] object from the data
    */
  def readSpike(channels: Array[Int], frame: Int, segment: Int, spikeLength: Int): NNSpike = {
    loggerRequire( 0 < spikeLength && spikeLength < 100, s"spikeLength=$spikeLength should have been (0, 100]" )

    new NNSpike(
      /*timestamp =*/ timing().convertFrsgToTs(frame, segment),
      /*waveform =*/ readTrace( channels, new NNRangeInstantiated(frame, frame + spikeLength - 1, step = 1, segment)   ).flatten,
      /*channels =*/ channels.length,
      0L
    )
  }

}
