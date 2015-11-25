package nounou.io.neuralynx.headers

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNSE(originalHeaderText: String, headerBytes: Int)
  extends NNHeaderNeuralynx(originalHeaderText, headerBytes) {

  require(headerRecordType == "Spike", s"NSE file with non-standard record type: $headerRecordType")

}

object NNHeaderNSE{

  /**Factory constructor to pass into FileNeuralynx for generic header creation*/
  def factory(originalHeaderText: String, headerBytes: Int) = new NNHeaderNSE(originalHeaderText, headerBytes)

}
