package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNeuralynxSpikeInfo, FileNEVInfo}

/**
  * Encapsulates text header handling for Neuralynx spike files (*.nse, *.nst, *.ntt).
  *
  */
trait NNHeaderNeuralynxSpike extends NNHeaderNeuralynxDAQ {

  /** [Header value NSE/NST/NTT: "WaveformLength"] */
  def getHeaderWaveformLength: Int
  /** [Header value NSE/NST/NTT: "AlignmentPt"] */
  def getHeaderAlignmentPt: Int

  override def getNeuralynxHeaderStringImpl() = {
    super[NNHeaderNeuralynxDAQ].getNeuralynxHeaderStringImpl() +
     "\n" +
    s"-WaveformLength $getHeaderWaveformLength\n" +
    s"-AlignmentPt $getHeaderAlignmentPt\n"
  }

}

class NNHeaderNeuralynxSpikeRead(override val originalHeaderText: String)
  extends NNHeaderNeuralynxDAQRead(originalHeaderText)
  with NNHeaderNeuralynxSpike
 {

//  lazy val recordSize: Int = FileNeuralynxSpikeInfo.getRecordSize(trodeCount)

  override lazy val getHeaderFileType = nlxHeaderValueS("FileType", "Spike")
  loggerRequire(
    getHeaderFileType == "Spike",
    s"Spike file (*.nse/*.nst/*.ntt) with non-standard record type: $getHeaderFileType"
  )

  override lazy val getHeaderWaveformLength = nlxHeaderValueI("WaveformLength", "0")
  override lazy val getHeaderAlignmentPt = nlxHeaderValueI("AlignmentPt", "-1")

  override lazy val getHeaderNumADChannels: Int = nlxHeaderValueI("NumADChannels", "-1")
  loggerRequire(
    getHeaderNumADChannels == 1 || getHeaderNumADChannels == 2 || getHeaderNumADChannels == 4 ,
    s"Neuralynx spike file with non-standard NumADChannels(trode count): $getHeaderNumADChannels")
  override lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", "-1")
  loggerRequire(
    getHeaderRecordSize == FileNeuralynxSpikeInfo.getRecordSize(getHeaderNumADChannels),
    s"Neuralynx spikefile with non-standard record size: $getHeaderRecordSize")

}


///**
//  * Encapsulates text header for spike NSE file (Neuralynx).
//  *
//  */
//trait NNHeaderNSE extends NNHeaderNeuralynxSpike {
//
//  //  override def getHeaderFileType: String
//  //  override def getHeaderRecordSize: Int
//
//  //  override def getNeuralynxHeaderStringImpl() = {
//  //    super[NNHeaderNeuralynxSpike].getNeuralynxHeaderStringImpl()
//  //  }
//
//}

//class NNHeaderNSERead(override val originalHeaderText: String)
//  extends NNHeaderNeuralynxSpikeRead(originalHeaderText) {
//
//  override lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNeuralynxSpikeInfo.getRecordSize("nse").toString)
//  loggerRequire(
//    getHeaderRecordSize == FileNeuralynxSpikeInfo.getRecordSize("nse"),
//    s"NSE file with non-standard record size: $getHeaderRecordSize"
//  )
//
//}


class NNHeaderNeuralynxSpikeConcrete(
                           headerSamplingFrequency: Double,
                           headerWaveformLength: Int,
                           headerAlignmentPt: Int,
                           headerNumADChannels: Int,

                           headerCheetahRev: String = "-1",
                           headerAcqEntName: String = "NA",
                           headerHardwareSubSystemName: String = "NA",
                           headerHardwareSubSystemType: String = "NA",

                           headerADChannel: Int = -1,
                           headerInputRange: Int = 1000,
                           headerInputInverted: Boolean = false,
                           headerDspDelayCompensation: Boolean = true,
                           headerDspFilterDelay: Int = 0
                         )
  extends NNHeaderNeuralynxDAQConcrete(
    getHeaderCheetahRev = headerCheetahRev, getHeaderFileType = "Spike",
    getHeaderNumADChannels = headerNumADChannels,
    getHeaderRecordSize = FileNeuralynxSpikeInfo.getRecordSize(headerNumADChannels),
    getHeaderAcqEntName = headerAcqEntName, getHeaderHardwareSubSystemName = headerHardwareSubSystemName, getHeaderHardwareSubSystemType = headerHardwareSubSystemType,
    getHeaderSamplingFrequency = headerSamplingFrequency,
    getHeaderADMaxValue = 32767, getHeaderADBitVolts = headerInputRange/32767.toDouble*1E-6,
    getHeaderADChannel = headerADChannel, getHeaderInputRange = headerInputRange,
    getHeaderInputInverted = headerInputInverted, getHeaderDspDelayCompensation = headerDspDelayCompensation, getHeaderDspFilterDelay = headerDspFilterDelay
  )
  with NNHeaderNeuralynxSpike
    /*with NNHeaderNSE*/ {

  override val getHeaderWaveformLength: Int = headerWaveformLength
  override val getHeaderAlignmentPt: Int = headerAlignmentPt

  //ToDo 3: handle prior headers?
  override val originalHeaderText = ""

}









//class NNHeaderNSEConcrete( headerSamplingFrequency: Double,
//                           headerWaveformLength: Int,
//                           headerAlignmentPt: Int,
//
//                           headerCheetahRev: String = "-1",
//                           headerAcqEntName: String = "NA",
//                           headerHardwareSubSystemName: String = "NA",
//                           headerHardwareSubSystemType: String = "NA",
//
//                           headerADChannel: Int = -1,
//                           headerInputRange: Int = 1000,
//                           headerInputInverted: Boolean = false,
//                           headerDspDelayCompensation: Boolean = true,
//                           headerDspFilterDelay: Int = 0
//                         )
//  extends NNHeaderNeuralynxDAQConcrete(
//    getHeaderCheetahRev = headerCheetahRev, getHeaderFileType = "Spike", getHeaderRecordSize = FileNSE.recordSize,
//    getHeaderAcqEntName = headerAcqEntName, getHeaderHardwareSubSystemName = headerHardwareSubSystemName, getHeaderHardwareSubSystemType = headerHardwareSubSystemType,
//    getHeaderSamplingFrequency = headerSamplingFrequency,
//    getHeaderADMaxValue = 32767, getHeaderADBitVolts = headerInputRange/32767.toDouble*1E-6,
//    getHeaderADChannel = headerADChannel, getHeaderInputRange = headerInputRange,
//    getHeaderInputInverted = headerInputInverted, getHeaderDspDelayCompensation = headerDspDelayCompensation, getHeaderDspFilterDelay = headerDspFilterDelay
//  )
//    with NNHeaderNSE {
//
//  override val getHeaderWaveformLength: Int = headerWaveformLength
//  override val getHeaderAlignmentPt: Int = headerAlignmentPt
//
//  //ToDo 3: handle prior headers?
//  override val originalHeaderText = ""
//
//}
