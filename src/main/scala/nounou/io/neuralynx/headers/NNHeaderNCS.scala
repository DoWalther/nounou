package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNCS}

/**
  * Encapsulates header information and serialization to text for Neuralynx NCS file headers.
  */
final class NNHeaderNCS(originalHeaderText: String) extends NNHeaderNeuralynx(originalHeaderText) {

  /**
    * [Header value Neuralynx: "FileType"] Record type.
    * This is instantiated in the individual file-type specific header
    * classes to provide defaults.
    */
  lazy val getHeaderRecordType = nlxHeaderValueS("FileType", "CSC")

  /**
    * [Header value NCS: "RecordSize"] Record size for single pages in data file
    */
  lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNCS.recordSize.toString)

  /**
    * [Header value NCS: "AcqEntName"] Acquisition entity name
    */
  lazy val getHeaderAcqEntName = nlxHeaderValueS("AcqEntName", "NoName")

  /**
    * [Header value NCS: "SamplingFrequency"] Sample rate, Hz
    */
  lazy val getHeaderSampleRate = nlxHeaderValueD("SamplingFrequency", "1")

  /**
    * [Header value NCS: "ADBitVolts"] volts/short bit to convert internal file Int16 to values
    */
  lazy val getHeaderADMaxValue = nlxHeaderValueD("ADMaxValue", "32767")

  /**
    * [Header value NCS: "ADBitVolts"] volts/short bit to convert internal file Int16 to values
    */
  lazy val getHeaderADBitVolts = nlxHeaderValueD("ADBitVolts", "3.05185e-009")

  /**
    * [Header value NCS: "InputRange"] input range in +/- mV
    */
  lazy val getHeaderInputRange = nlxHeaderValueD("InputRange", "2500")


  override def getNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${System.currentTimeMillis()}\n" +
      s" -CheetahRev $getHeaderCheetahRev\n" +
      s" -FileType $getHeaderRecordType\n" +
      s" -RecordSize $getHeaderRecordSize\n" +
      s" -SamplingFrequency $getHeaderSampleRate\n" +
      s" -ADBitVolts $getHeaderADBitVolts\n" +
      s" -ADMaxValue 32767\n" +
      //  -NumADChannels 1
      {if(originalHeaderPresent) commentLines(originalHeaderText) else ""}
  }

}