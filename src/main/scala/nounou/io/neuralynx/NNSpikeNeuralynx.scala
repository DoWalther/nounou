package nounou.io.neuralynx

import java.math.BigInteger

import nounou.elements.spikes.NNSpike
import nounou.io.neuralynx.headers.NNHeaderNeuralynxSpike
import nounou.util.LoggingExt

object NNSpikeNeuralynx extends LoggingExt {

  val NULL_SPIKE = null

  val emptyScNumber = 0L
  val emptyParams = Vector.tabulate(8)( (v: Int) => 0L )

  implicit def convertNNSpikeToNNSpikeNeuralynx(spike: NNSpike): NNSpikeNeuralynx =
    convertNNSpikeToNNSpikeNeuralynxImpl(spike, emptyScNumber, emptyParams)
  def convertNNSpikeToNNSpikeNeuralynx(spike: NNSpike, dwScNumber: Long, dnParams: Vector[Long]): NNSpikeNeuralynx =
    convertNNSpikeToNNSpikeNeuralynxImpl(spike, dwScNumber, dnParams)

  private def convertNNSpikeToNNSpikeNeuralynxImpl(
      spike: NNSpike, dwScNumber: Long, dnParams: Vector[Long]): NNSpikeNeuralynx = {

    loggerRequire(
      //ToDo 1: must convert to scaled!!!
      spike.waveform.forall( (v: Double) => (Short.MinValue.toInt <= v && v <= Short.MaxValue.toInt) ),
      "All values of waveform must be within bounds of Short to comply with the Neuralynx spike formats.")

    val waveformShort = spike.waveform//.map(_.toShort)

    new NNSpikeNeuralynx(spike.timestamp, dwScNumber, spike.unitNo, dnParams, waveformShort, spike.channels)
//    spike.channels match {
//      case 1 => new NNSpikeNSE(spike.timestamp, dwScNumber, spike.unitNo, dnParams, waveformShort)
//      case 2 => new NNSpikeNST(spike.timestamp, dwScNumber, spike.unitNo, dnParams, waveformShort)
//      case 4 => new NNSpikeNTT(spike.timestamp, dwScNumber, spike.unitNo, dnParams, waveformShort)
//      case _ => throw loggerError("Neuralynx can only support spikes with 1,2,or 4 channels")
//    }
  }

}


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
class NNSpikeNeuralynx(
                      val qwTimeStamp: BigInt,
                      val dwScNumber: Long,
                      val dwCellNumber: Long,
                      val dnParams: Vector[Long],
                      val snData: Vector[Double],
                      channels: Int
                      ) extends
                      NNSpike(/*timestamp = */qwTimeStamp,
                              //ToDo 1 !!! must convert this to real scaling!!!
                              /*waveform = */snData.map( _.toDouble ),
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

  /**Java accessor for qwTimeStamp, returns [java.math.BigInteger], which is immutable.*/
  def getQwTimeStamp(): BigInteger = qwTimeStamp.bigInteger
  /**Java accessor for dwScNumber.*/
  def getDwScNumber(): Long = dwScNumber
  /**Java accessor for dwCellNumber.*/
  def getDwCellNumber(): Long = dwCellNumber
  /**Java accessor for getDnParams, which returns a protective clone.*/
  def getDnParams(): Array[Long] = dnParams.toArray
  /**Java accessor for getDnParams, which returns a protective clone.*/
  def getSnData(scaling: NNScalingNeuralynx): Array[Short] = {
    scaling.convertAbsoluteToShort( waveform.toArray )
  }

  // </editor-fold>

    override def reassignUnitNo(newUnitNo: Long): NNSpikeNeuralynx =
      new NNSpikeNeuralynx(qwTimeStamp, dwScNumber, newUnitNo, dnParams, snData, channels)

}

//final class NNSpikeNSE(
//        qwTimeStamp: BigInt, dwScNumber: Long, dwCellNumber: Long, dnParams: Vector[Long], snData: Vector[Double] ) extends
//      NNSpikeNeuralynx(qwTimeStamp, dwScNumber, dwCellNumber, dnParams, snData, 1) {
//
//  override def reassignUnitNo(newUnitNo: Long): NNSpikeNSE =
//    new NNSpikeNSE(qwTimeStamp, dwScNumber, newUnitNo, dnParams, snData)
//
//
//}
//final class NNSpikeNST(
//        qwTimeStamp: BigInt, dwScNumber: Long, dwCellNumber: Long, dnParams: Vector[Long], snData: Vector[Double] ) extends
//      NNSpikeNeuralynx(qwTimeStamp, dwScNumber, dwCellNumber, dnParams, snData, 2) {
//
//  override def reassignUnitNo(newUnitNo: Long): NNSpikeNST =
//    new NNSpikeNST(qwTimeStamp, dwScNumber, newUnitNo, dnParams, snData)
//
//}
//final class NNSpikeNTT(
//        qwTimeStamp: BigInt, dwScNumber: Long, dwCellNumber: Long, dnParams: Vector[Long], snData: Vector[Double] ) extends
//      NNSpikeNeuralynx(qwTimeStamp, dwScNumber, dwCellNumber, dnParams, snData, 4){
//
//  override def reassignUnitNo(newUnitNo: Long): NNSpikeNTT =
//    new NNSpikeNTT(qwTimeStamp, dwScNumber, newUnitNo, dnParams, snData)
//
//}
//
