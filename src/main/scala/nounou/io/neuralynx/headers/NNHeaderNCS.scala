package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNCS}

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNCS(originalHeaderText: String)
  extends NNHeaderNeuralynx(originalHeaderText) {

  final lazy val headerRecordType = nlxHeaderValueS("FileType", "CSC")
  final lazy val headerRecordSize = nlxHeaderValueI("RecordSize", FileNCS.recordSize.toString)

  lazy val headerAcqEntName = nlxHeaderValueS("AcqEntName", "NoName")
  /**Sample rate, Hz*/
  lazy val headerSampleRate = nlxHeaderValueD("SamplingFrequency", "1")
  lazy val headerADBitVolts = nlxHeaderValueD("ADBitVolts", "3.05185e-009")

  override def toNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${System.currentTimeMillis()}\n" +
      s" -CheetahRev $headerCheetahRev\n" +
      s" -FileType $headerRecordType\n" +
      s" -RecordSize $headerRecordSize\n" +
      s" -SamplingFrequency $headerSampleRate\n" +
      s" -ADBitVolts $headerADBitVolts\n" +
      //  -ADMaxValue 32767
      //  -NumADChannels 1
      {if(originalHeaderPresent) commentLines(originalHeaderText) else ""}
  }

}