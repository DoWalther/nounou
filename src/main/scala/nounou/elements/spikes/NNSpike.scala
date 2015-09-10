package nounou.elements.spikes

import java.math.BigInteger

import nounou.elements.NNElement
import breeze.linalg.{DenseVector, DenseMatrix}

/**An immutable class to encapsulate a single spike waveform in a neurophysiological recording,
  * to be accumulated into a [[nounou.elements.spikes.NNSpikes]] database.
  *
  * @param timestamp the timestamp corresponding to the beginning of the waveform window
  * @param waveform the waveform data, given as concatenated waveforms from each channel
  * @param channels number of channels
  * @param unitNo the classified unit of this spike. 0 indicates an unclassified unit.
  */
class NNSpike(val timestamp: BigInt, val waveform: Vector[Int], val channels: Int = 1, val unitNo: Long = 0L)
  extends NNElement {

  loggerRequire( waveform != null, "Waveform must contain a non-null vector of Int values.")
  loggerRequire( channels >= 1, s"Waveform must have at least one channel, $channels is invalid!")
  loggerRequire( waveform.length > 0, s"Waveform must have some samples, sample count ${waveform.length} is invalid!")
  loggerRequire( waveform.length % channels == 0, "The given waveform length is not equally divisible by the channel count!")
  val singleWaveformLength = waveform.length / channels

  def this(timestamp: BigInt, waveform: Array[Int], channels: Int, unitNo: Long) =
        this(timestamp, waveform.toVector, channels, unitNo)
  def this(timestamp: BigInt, waveform: Array[Int], channels: Int) =
        this(timestamp, waveform.toVector, channels)
  def this(timestamp: BigInteger, waveform: Array[Int], channels: Int, unitNo: Long) =
        this(BigInt(timestamp), waveform.toVector, channels, unitNo)
  def this(timestamp: BigInteger, waveform: Array[Int], channels: Int) =
        this(BigInt(timestamp), waveform.toVector, channels)
//  def this(timestamp: BigInteger, waveform: Array[Int]) = this(timestamp, waveform, 1, 0L)

  def toStringFullImplParams() = s"ts=${timestamp}, ch=${channels}, swflen=${singleWaveformLength}, unitNo=${unitNo}, "
  def toStringFullImplTail() = ""

  // <editor-fold defaultstate="collapsed" desc=" Java accessors ">

  /**Java accessor for timestamp, returns [[java.math.BigInteger]], which is immutable.*/
  def getTimestamp(): BigInteger = timestamp.bigInteger
  /**Java accessor for waveform, returns an Array[Int] clone.*/
  def getWaveform(): Array[Int] = waveform.toArray[Int]
  /**Java accessor for channels, alias for [[channels()]].*/
  def getChannels(): Int = channels
  /**Java accessor for channels, alias for [[unitNo()]].*/
  def getUnitNo(): Long = unitNo

  // </editor-fold>

  /**'''__MUST OVERRIDE__''' Gives immutable clone of this object but with a new unitNo.
   */
  def reassignUnitNo(newUnitNo: Long) = new NNSpike(timestamp, waveform, channels, newUnitNo)

//  def toArray() = Array.tabulate(channels)(p => waveform( :: , p ).toArray )

  override def isCompatible(that: NNElement) = that match {
    case x: NNSpike => {
      getClass == x.getClass &&
      waveform.length == x.waveform.length &&
      channels == x.channels
    }

  }

}




//object NNSpike extends LoggingExt {
//
//  //  def toArray(xSpike : XSpike): Array[Array[Int]] = xSpike.toArray
//  //  def toArray(xSpikes: Array[XSpike]): Array[Array[Array[Int]]] = xSpikes.map( _.toArray )
//  //  def readSpikeFrames(xData: NNData, channels: Array[Int], xFrames: Array[Int], length: Int, trigger: Int) = {
//  //    xFrames.map( readSpikeFrame(xData, channels, _, length, trigger))
//  //  }
//  //  def readSpikeFrame(xData: NNData, channels: Array[Int], frame: Int, segment: Int, length: Int): NNSpike = {
//  //    loggerRequire( frame > 0, s"frame must be >0, not ${frame}")
//  //    loggerRequire( length > 0, s"length must be >0, not ${length}")
//  //
//  //    val tempWF = channels.map( ch => xData.readTrace( ch, SampleRangeReal(frame, frame+length-1, step=1, segment) ))
//  //    new NNSpikeFrame( frame, tempWF, frame, segment)
//  //  }
//
//}