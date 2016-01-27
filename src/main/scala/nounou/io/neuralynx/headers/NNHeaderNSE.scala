package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.FileNSE

/**
  * Encapsulates text header for spike NSE file (Neuralynx).
  *
  */
trait NNHeaderNSE extends NNHeaderNeuralynxSpike {

  override def getHeaderFileType: String
  override def getHeaderRecordSize: Int

  override def getNeuralynxHeaderStringImpl() = {
    super[NNHeaderNeuralynxSpike].getNeuralynxHeaderStringImpl()
  }

}

class NNHeaderNSERead(override val originalHeaderText: String)
  extends NNHeaderNeuralynxSpikeRead(originalHeaderText)
  with NNHeaderNSE {

  override lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNSE.recordSize.toString)
  loggerRequire(getHeaderRecordSize == FileNSE.recordSize, s"NSE file with non-standard record size: $getHeaderRecordSize")

}

class NNHeaderNSEConcrete( headerSamplingFrequency: Double,
                           headerWaveformLength: Int,
                           headerAlignmentPt: Int,

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
    getHeaderCheetahRev = headerCheetahRev, getHeaderFileType = "NSE", getHeaderRecordSize = FileNSE.recordSize,
    getHeaderAcqEntName = headerAcqEntName, getHeaderHardwareSubSystemName = headerHardwareSubSystemName, getHeaderHardwareSubSystemType = headerHardwareSubSystemType,
    getHeaderSamplingFrequency = headerSamplingFrequency,
    getHeaderADMaxValue = 32767, getHeaderADBitVolts = headerInputRange/32767.toDouble*1E-6,
    getHeaderADChannel = headerADChannel, getHeaderInputRange = headerInputRange,
    getHeaderInputInverted = headerInputInverted, getHeaderDspDelayCompensation = headerDspDelayCompensation, getHeaderDspFilterDelay = headerDspFilterDelay
  )
  with NNHeaderNSE {

  override val getHeaderWaveformLength: Int = headerWaveformLength
  override val getHeaderAlignmentPt: Int = headerAlignmentPt

  //ToDo 3: handle prior headers?
  override val originalHeaderText = ""

}
