package nounou.elements.spikes

import breeze.linalg.{DenseVector, max, min}
import breeze.numerics.abs
import java.math.BigInteger
import nounou.elements.NNElement
import nounou.elements.traits.NNElementCompatibilityCheck

/**
  * An immutable class to encapsulate a single spike waveform in a neurophysiological recording,
  * to be accumulated into a [[nounou.elements.spikes.NNSpikes]] database.
  *
  * @param timestamp the timestamp corresponding to the beginning of the waveform window: note that this is not the threshold crossing point
  * @param waveform the waveform data, given as concatenated waveforms from each channel
  * @param channels number of channels
  * @param unitNo the classified unit of this spike. 0 indicates an unclassified unit.
  */
class NNSpike(val timestamp: BigInt,
              val waveform: Vector[Double],
              val channels: Int,
              val unitNo: Long)  extends NNElement with NNElementCompatibilityCheck {

  // <editor-fold defaultstate="collapsed" desc=" argument checks ">

  loggerRequire( waveform != null, "Waveform must contain a non-null vector of Int values.")
  loggerRequire( channels >= 1, s"Waveform must have at least one channel, $channels is invalid!")
  loggerRequire( waveform.length > 0, s"Waveform must have some samples, sample count ${waveform.length} is invalid!")
  loggerRequire( waveform.length % channels == 0, "The given waveform length is not equally divisible by the channel count!")

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" transient waveform variables (max/min/length/etc.) ">

  @transient
  val singleWaveformLength = waveform.length / channels
  @transient
  lazy val waveformMax: Double = max( waveform )
  @transient
  lazy val waveformMin: Double = min( waveform )
  @transient
  lazy val waveformAbsMax: Double = max( waveform.map( abs(_) ) )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" alternate constructors ">

  //ToDo 4: This alternate constructor should be eliminated, all alternate constructors to nounou.NN
  def this(timestamp: BigInt, waveform: DenseVector[Double], channels: Int, unitNo: Long) =
    this(timestamp, waveform.toScalaVector(), channels, unitNo)

//  def this(timestamp: BigInteger, waveform: Array[Double], channels: Int, unitNo: Long) =
//    this(BigInt(timestamp), waveform.toVector, channels, unitNo)
//
//  def this(timestamp: BigInteger, waveform: Vector[Double], channels: Int, unitNo: Long) =
//    this(BigInt(timestamp), waveform, channels, unitNo)

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toString related ">


  def toStringImpl() = s"ts=${timestamp}, ch=${channels}, swflen=${singleWaveformLength}, unitNo=${unitNo}"

  def toStringFullImpl() = ""

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" //Java accessors ">

  /**Java accessor for timestamp, returns a [java.math.BigInteger], which is immutable.*/
  final def getTimestamp(): BigInteger = timestamp.bigInteger

  /**Java accessor for channels, alias for [[nounou.elements.spikes.NNSpike.channels]].*/
  final def getChannels(): Int = channels

  /**Java accessor for channels, alias for [[nounou.elements.spikes.NNSpike.unitNo]].*/
  final def getUnitNo(): Long = unitNo

  /**Java accessor for flattened waveform, returns an Array[Double] clone.*/
  final def readWaveformFlat(): Array[Double] = waveform.toArray

  /**Java accessor for waveforms.*/
  def readWaveform(): Array[Array[Double]] = {
    val tempReturn = new Array[Array[Double]](channels)
    for( ch <- 0 until channels ){
      tempReturn(ch) = waveform.slice( ch*singleWaveformLength, (ch+1)*singleWaveformLength ).toArray
    }
    tempReturn
  }

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