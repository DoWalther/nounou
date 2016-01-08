package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNSE, FileNEV}

/** Encapsulates text header for spike NSE file (Neuralynx).
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNSE(originalHeaderText: String) extends NNHeaderNeuralynx(originalHeaderText) {

  def getHeaderRecordType = nlxHeaderValueS("FileType", "Spike")
  def getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNSE.recordSize.toString)
  require(getHeaderRecordType == "Spike", s"NSE file with non-standard record type: $getHeaderRecordType")

  override def getNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${System.currentTimeMillis()}\n" +
      s" -CheetahRev $getHeaderCheetahRev\n" +
      s" -FileType $getHeaderRecordType\n" +
      s" -RecordSize $getHeaderRecordSize\n" + {
      if (originalHeaderPresent) commentLines(originalHeaderText) else ""
    }
  }
}