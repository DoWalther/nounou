package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNSE, FileNEV}

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNSE(originalHeaderText: String)
  extends NNHeaderNeuralynx(originalHeaderText) {

  final lazy val headerRecordType = nlxHeaderValueS("FileType", "Spike")
  final lazy val headerRecordSize = nlxHeaderValueI("RecordSize", FileNSE.recordSize.toString)
  require(headerRecordType == "Spike", s"NSE file with non-standard record type: $headerRecordType")

  override def toNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${System.currentTimeMillis()}\n" +
      s" -CheetahRev $headerCheetahRev\n" +
      s" -FileType $headerRecordType\n" +
      s" -RecordSize $headerRecordSize\n" + {
      if (originalHeaderPresent) commentLines(originalHeaderText) else ""
    }
  }
}