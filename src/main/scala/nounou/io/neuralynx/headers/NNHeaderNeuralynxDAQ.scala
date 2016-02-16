package nounou.io.neuralynx.headers

/**
  * A header trait for [[nounou.io.neuralynx.headers.NNHeaderNeuralynx NNHeaderNeuralynx]]
  * objects which include the common sampling items in the header for Neuralynx
  * continuous (*.nsc) and spike (*.nse, *.nst, *.ntt) files
  *
  * Created by ktakagaki on 16/01/27.
  */
trait NNHeaderNeuralynxDAQ extends NNHeaderNeuralynx{


  /** [Header value NCS/NSE/NST/NTT: "AcqEntName"] Acquisition entity name */
  def getHeaderAcqEntName: String


  /** [Header value NCS/NSE/NST/NTT: "HardwareSubSystemName"] */
  def getHeaderHardwareSubSystemName: String
  /** [Header value NCS/NSE/NST/NTT: "HardwareSubSystemType"] */
  def getHeaderHardwareSubSystemType: String
  /** [Header value NCS/NSE/NST/NTT: "SamplingFrequency"] Sample rate, Hz */
  def getHeaderSamplingFrequency: Double
  /** [Header value NCS/NSE/NST/NTT: "ADMaxValue"] volts/short bit to convert internal file Int16 to values */
  def getHeaderADMaxValue: Double
  /** [Header value NCS/NSE/NST/NTT: "ADBitVolts"] volts/short bit to convert internal file Int16 to values */
  def getHeaderADBitVolts: Double

  /** [Header value NCS/NSE/NST/NTT: "ADChannel"] */
  def getHeaderADChannel: Int
  /** [Header value NCS/NSE/NST/NTT: "InputRange"] input range in +/- mV */
  def getHeaderInputRange: Int
  /** [Header value NCS/NSE/NST/NTT: "InputInverted"] */
  def getHeaderInputInverted: Boolean
  /** [Header value NCS/NSE/NST/NTT: "DspDelayCompensation"] */
  def getHeaderDspDelayCompensation: Boolean
  /** [Header value NCS/NSE/NST/NTT: "DspFilterDelay_µs"] filter delay in timestamps (microsec) */
  def getHeaderDspFilterDelay: Int

  override def getNeuralynxHeaderStringImpl() = {
    super[NNHeaderNeuralynx].getNeuralynxHeaderStringImpl() +
      s"-AcqEntName $getHeaderAcqEntName\n" +
      "\n" +
      s"-HardwareSubSystemName $getHeaderHardwareSubSystemName\n" +
      s"-HardwareSubSystemType $getHeaderHardwareSubSystemType\n" +
      s"-SamplingFrequency ${getHeaderSamplingFrequency.toInt}\n" +
      s"-ADMaxValue 32767\n" +
      s"-ADBitVolts ${"%6.3e".format(getHeaderADBitVolts)}\n" +
      s"-ADChannel $getHeaderADChannel\n" +
      s"-InputRange $getHeaderInputRange\n" +
      s"-InputInverted ${if(getHeaderInputInverted) "True" else "False"}\n" +
      s"-DspDelayCompensation " + {if(getHeaderDspDelayCompensation) "Enabled" else "Disabled"} + " \n" +
      s"-DspFilterDelay_µs $getHeaderDspFilterDelay\n"
    //  -NumADChannels 1
  }

}


abstract class NNHeaderNeuralynxDAQRead(override val originalHeaderText: String)
  extends NNHeaderNeuralynxRead(originalHeaderText)
  with NNHeaderNeuralynxDAQ
{

  override lazy val getHeaderAcqEntName = nlxHeaderValueS("AcqEntName", "NotInFile")

  override lazy val getHeaderHardwareSubSystemName = nlxHeaderValueS("HardwareSubSystemName", "NotInFile")
  override lazy val getHeaderHardwareSubSystemType = nlxHeaderValueS("HardwareSubSystemType", "NotInFile")
  override lazy val getHeaderSamplingFrequency = nlxHeaderValueD("SamplingFrequency", "1")
  override lazy val getHeaderADMaxValue = nlxHeaderValueD("ADMaxValue", "32767")
  override lazy val getHeaderADBitVolts = nlxHeaderValueD("ADBitVolts", "3.05185e-009")

  override lazy val getHeaderADChannel = nlxHeaderValueI("ADChannel", "-1")
  override lazy val getHeaderInputRange = nlxHeaderValueI("InputRange", "2500")
  override lazy val getHeaderInputInverted = nlxHeaderValueB("InputInverted", false)
  override lazy val getHeaderDspDelayCompensation = nlxHeaderValueB("DspDelayCompensation", false)
  override lazy val getHeaderDspFilterDelay = nlxHeaderValueI("DspFilterDelay_µs", "0")

  loggerRequire( getHeaderADBitVolts * getHeaderADMaxValue - getHeaderInputRange * 1e-6 < 0.00001,
        s"ADBitVolts($getHeaderADBitVolts) * ADMaxValue($getHeaderADMaxValue) must equal InputRange($getHeaderInputRange) * 1e-6!"
  )

}

abstract class NNHeaderNeuralynxDAQConcrete( override val getHeaderCheetahRev: String,
                                             override val getHeaderFileType: String,
                                             override val getHeaderRecordSize: Int,

                                             override val getHeaderAcqEntName: String,
                                             override val getHeaderHardwareSubSystemName: String,
                                             override val getHeaderHardwareSubSystemType: String,
                                             override val getHeaderSamplingFrequency: Double,
                                             override val getHeaderADMaxValue: Double,
                                             override val getHeaderADBitVolts: Double,

                                             override val getHeaderADChannel: Int,
                                             override val getHeaderInputRange: Int,
                                             override val getHeaderInputInverted: Boolean,
                                             override val getHeaderDspDelayCompensation: Boolean,
                                             override val getHeaderDspFilterDelay: Int
                                           )
  extends NNHeaderNeuralynxConcrete(
    getHeaderCheetahRev, getHeaderFileType, getHeaderRecordSize)
  with NNHeaderNeuralynxDAQ{


}
