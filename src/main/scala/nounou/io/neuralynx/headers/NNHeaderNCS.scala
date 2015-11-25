package nounou.io.neuralynx.headers

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNCS(originalHeaderText: String, headerBytes: Int)
  extends NNHeaderNeuralynx(originalHeaderText, headerBytes) {

  override def isValid() = {
    super.isValid() && (headerRecordType == "CSC")
  }

  lazy val headerAcqEntName = nlxHeaderValueS("AcqEntName", "NoName")
  //lazy val headerRecordType = nlxHeaderValueS("FileType", "")
  //lazy val headerRecordSize = nlxHeaderValueI("RecordSize", "0")
  /**Sample rate, Hz*/
  lazy val headerSampleRate = nlxHeaderValueD("SamplingFrequency", "1")
  lazy val headerADBitVolts = nlxHeaderValueD("ADBitVolts", "1")

}

object NNHeaderNCS{

  /**Factory constructor to pass into FileNeuralynx for generic header creation*/
  def factory(originalHeaderText: String, headerBytes: Int) = new NNHeaderNCS(originalHeaderText, headerBytes)

}
