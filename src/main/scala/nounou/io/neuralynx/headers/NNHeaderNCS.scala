package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNCS}

trait NNHeaderNCS extends NNHeaderNeuralynxDAQ {

  override def getNeuralynxHeaderStringImpl() = {
    super[NNHeaderNeuralynxDAQ].getNeuralynxHeaderStringImpl()
  }

}
/**
  * Encapsulates header information and serialization to text for Neuralynx NCS file headers.
  */
final class NNHeaderNCSRead(override val originalHeaderText: String)
  extends NNHeaderNeuralynxDAQRead(originalHeaderText)
  with NNHeaderNCS {

  // Although the following two items are read from all Neuralynx header objects,
  // they are instantiated in individual file-type specific headers in order to set defaults
  // and conduct require() testing
  override lazy val getHeaderFileType = nlxHeaderValueS("FileType", "CSC")
  override lazy val getHeaderNumADChannels: Int = nlxHeaderValueI("NumADChannels", "-1")
  loggerRequire(
    getHeaderNumADChannels == 1,
    s"Neuralynx NCS file with non-standard NumADChannels: $getHeaderNumADChannels"
  )
  override lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNCS.recordSize.toString)
  loggerRequire(getHeaderRecordSize == FileNCS.recordSize, s"NCS file with non-standard record size: $getHeaderRecordSize")

  loggerRequire(getHeaderFileType == "CSC", s"NCS file with non-standard record type: $getHeaderFileType")
  loggerRequire(getHeaderSamplingFrequency >= 1000d, s"NCS file with non-standard sampling frequency: ${getHeaderSamplingFrequency}")
  loggerRequire(getHeaderADMaxValue == 32767, s"NCS file with non-standard ADMaxValue: ${getHeaderADMaxValue}")

}