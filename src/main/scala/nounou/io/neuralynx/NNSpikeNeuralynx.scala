package nounou.io.neuralynx

import java.math.BigInteger
import nounou.elements.spikes.NNSpike
import nounou.util.LoggingExt

/**Neuralynx specialization for [[nounou.elements.spikes.NNSpike]].
  *
  * The following are strictly true:
  *     + qwTimeStamp  (BigInt) == NNSpike.timestamp (BigInt)
  *     + dwCellNumber (UInt32) == NNSpike.unitNo    (Long)
  *     + snData (Array[Short]) == NNSpike.waveform  (Array[Int])
  *
  * The following are unique to this class:
  *     + dwScNumber
  *     + dnParams
  */
abstract class NNSpikeNeuralynx(
                      val qwTimeStamp: BigInt,
                      val dwScNumber: Long,
                      val dwCellNumber: Long,
                      val dnParams: Vector[Long],
                      val snData: Vector[Short],
                      channels: Int
                      ) extends
                      NNSpike(/*timestamp = */qwTimeStamp,
                              /*waveform = */snData.map(_.toInt).toVector,
                              /*channels = */channels,
                              /*unitNo = */dwCellNumber) {

  private val uInt64MaxValue = (BigInt(Long.MaxValue) + 1)*2 - 1
  private val uInt32MaxValue = (Int.MaxValue.toLong + 1)*2 - 1

  loggerRequire( 0 <= qwTimeStamp && qwTimeStamp < uInt64MaxValue,
    "Neuralynx qwTimeStamp (i.e. timestamp) must be within the ranges of UInt64")
  loggerRequire( 0 <= dwScNumber && dwScNumber < uInt32MaxValue,
    "Neuralynx dwScNumber (i.e. unitNo) must be within the ranges of UInt32")
  loggerRequire( 0 <= dwCellNumber && dwCellNumber < uInt32MaxValue,
    "Neuralynx dwCellNumber (i.e. unitNo) must be within the ranges of UInt32")
  loggerRequire( dnParams != null && dnParams.length == 8,
    "As of 3.2010, Neuralynx spike formats require a fixed 8 parameters.")

  // <editor-fold defaultstate="collapsed" desc=" Java accessors ">

  /**Java accessor for qwTimeStamp, returns [[java.math.BigInteger]], which is immutable.*/
  def getQwTimeStamp(): BigInteger = qwTimeStamp.bigInteger
  /**Java accessor for dwScNumber.*/
  def getDwScNumber(): Long = dwScNumber
  /**Java accessor for dwCellNumber.*/
  def getDwCellNumber(): Long = dwCellNumber
  /**Java accessor for getDnParams, which returns a protective clone.*/
  def getDnParams(): Array[Long] = dnParams.toArray
  /**Java accessor for getDnParams, which returns a protective clone.*/
  def getSnData(): Array[Short] = snData.toArray

  // </editor-fold>

}

object NNSpikeNeuralynx extends LoggingExt {
  def convertNNSpikeToNNSpikeNeuralynx(spike: NNSpike): NNSpikeNeuralynx ={

    loggerRequire(
      spike.waveform.forall( (v: Int) => (Short.MinValue.toInt <= v && v <= Short.MaxValue.toInt) ),
      "All values of waveform must be within bounds of Short to comply with the Neuralynx spike formats.")
    val waveformShort = spike.waveform.map(_.toShort)
    val emptyScNumber = 0L
    val emptyParams = Vector.tabulate(8)( (v: Int) => 0L )

    spike.channels match {
      case 1 => new NNSpikeNSE(spike.timestamp, emptyScNumber, spike.unitNo, emptyParams, waveformShort)
      case 2 => new NNSpikeNST(spike.timestamp, emptyScNumber, spike.unitNo, emptyParams, waveformShort)
      case 4 => new NNSpikeNTT(spike.timestamp, emptyScNumber, spike.unitNo, emptyParams, waveformShort)
      case _ => throw loggerError("Neuralynx can only support spikes with 1,2,or 4 channels")
    }
  }
}

final class NNSpikeNSE(
        qwTimeStamp: BigInt, dwScNumber: Long, dwCellNumber: Long, dnParams: Vector[Long], snData: Vector[Short] ) extends
      NNSpikeNeuralynx(qwTimeStamp, dwScNumber, dwCellNumber, dnParams, snData, 1)
final class NNSpikeNST(
        qwTimeStamp: BigInt, dwScNumber: Long, dwCellNumber: Long, dnParams: Vector[Long], snData: Vector[Short] ) extends
      NNSpikeNeuralynx(qwTimeStamp, dwScNumber, dwCellNumber, dnParams, snData, 2)
final class NNSpikeNTT(
        qwTimeStamp: BigInt, dwScNumber: Long, dwCellNumber: Long, dnParams: Vector[Long], snData: Vector[Short] ) extends
      NNSpikeNeuralynx(qwTimeStamp, dwScNumber, dwCellNumber, dnParams, snData, 4)

