package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNCS}

/**
  * Created by ktakagaki on 15/11/24.
  */
final class NNHeaderNCS(originalHeaderText: String) extends NNHeaderNeuralynx(originalHeaderText) {

  def getHeaderRecordType = nlxHeaderValueS("FileType", "CSC")
  def getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNCS.recordSize.toString)

  def getHeaderAcqEntName = nlxHeaderValueS("AcqEntName", "NoName")
  /**Sample rate, Hz*/
  def getHeaderSampleRate = nlxHeaderValueD("SamplingFrequency", "1")
  def getHeaderADBitVolts = nlxHeaderValueD("ADBitVolts", "3.05185e-009")

  override def getNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${System.currentTimeMillis()}\n" +
      s" -CheetahRev $getHeaderCheetahRev\n" +
      s" -FileType $getHeaderRecordType\n" +
      s" -RecordSize $getHeaderRecordSize\n" +
      s" -SamplingFrequency $getHeaderSampleRate\n" +
      s" -ADBitVolts $getHeaderADBitVolts\n" +
      //  -ADMaxValue 32767
      //  -NumADChannels 1
      {if(originalHeaderPresent) commentLines(originalHeaderText) else ""}
  }

}