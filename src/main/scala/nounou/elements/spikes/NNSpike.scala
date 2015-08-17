package nounou.elements.spikes

import nounou.elements.NNElement
import nounou.util.LoggingExt
import breeze.linalg.{DenseVector, DenseMatrix}
import nounou.NN._
import nounou.elements.data.NNData

object NNSpike extends LoggingExt {

//  def toArray(xSpike : XSpike): Array[Array[Int]] = xSpike.toArray
//  def toArray(xSpikes: Array[XSpike]): Array[Array[Array[Int]]] = xSpikes.map( _.toArray )
//  def readSpikeFrames(xData: NNData, channels: Array[Int], xFrames: Array[Int], length: Int, trigger: Int) = {
//    xFrames.map( readSpikeFrame(xData, channels, _, length, trigger))
//  }
//  def readSpikeFrame(xData: NNData, channels: Array[Int], frame: Int, segment: Int, length: Int): NNSpike = {
//    loggerRequire( frame > 0, s"frame must be >0, not ${frame}")
//    loggerRequire( length > 0, s"length must be >0, not ${length}")
//
//    val tempWF = channels.map( ch => xData.readTrace( ch, SampleRangeReal(frame, frame+length-1, step=1, segment) ))
//    new NNSpikeFrame( frame, tempWF, frame, segment)
//  }

}

/**An immutable class to encapsulate a single spike waveform in a neurophysiological recording,
  * to be accumulated into a [[nounou.elements.spikes.NNSpikes]] database.
  *
  * @param timestamp the timestamp corresponding to the beginning of the waveform window
  * @param waveform the waveform data, given as concatenated waveforms from each channel
  * @param channels number of channels
  * @param unitNo
  */
class NNSpike(val timestamp: BigInt, val waveform: Vector[Int], val channels: Int = 1, val unitNo: Long = 0L)
  extends NNElement {

  loggerRequire( waveform != null, "Waveform must contain a non-null vector of Int values.")
  loggerRequire( channels >= 1, s"Waveform must have at least one channel, $channels is invalid!")
  loggerRequire( waveform.length > 0, s"Waveform must have some samples, sample count ${waveform.length} is invalid!")
  loggerRequire( waveform.length % channels == 0, "The given waveform length is not equally divisible by the channel count!")
  val singleWaveformLength = waveform.length / channels

  override def toString = s"XSpike(time=${timestamp}, channels=${channels}, swflen=${singleWaveformLength}, unitNo=${unitNo}} )"

//  def toArray() = Array.tabulate(channels)(p => waveform( :: , p ).toArray )

  override def isCompatible(that: NNElement) = false

}






//class NNSpikeFrame(override val time : Long,
//                  override val waveform: Array[Array[Int]],
//                  unitNo: Int = 0,
//                  val segment: Int)
//  extends NNSpike(time, waveform, unitNo){
//
//    lazy val frame = time.toInt
//
//}
//
