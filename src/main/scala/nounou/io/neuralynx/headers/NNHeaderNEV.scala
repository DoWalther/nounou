package nounou.io.neuralynx.headers

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNEV(originalHeaderText: String, headerBytes: Int)
  extends NNHeaderNeuralynx(originalHeaderText, headerBytes) {

    require(headerRecordType == "Event", s"NEV file with non-standard record type: $headerRecordType")

  }

object NNHeaderNEV{

  /**Factory constructor to pass into FileNeuralynx for generic header creation*/
  def factory(originalHeaderText: String, headerBytes: Int) = new NNHeaderNEV(originalHeaderText, headerBytes)

}
