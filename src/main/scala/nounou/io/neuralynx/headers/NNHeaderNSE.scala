package nounou.io.neuralynx.headers

import java.time.Instant
import nounou.io.neuralynx.fileObjects.{FileNSE, FileNEV}

/**
  * Encapsulates text header for spike NSE file (Neuralynx).
  *
  */
trait NNHeaderNSE extends NNHeaderNeuralynx {

  override def getHeaderRecordType: String

  override def getHeaderRecordSize: Int

  require(getHeaderRecordType == "Spike", s"NSE file with non-standard record type: $getHeaderRecordType")

  /**
    * [Header value NCS: "DspFilterDelay_µs"] filter delay in timestamps (microsec)
    */
  def getHeaderDspFilterDelay: Int


  override def getNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${Instant.now().toString()}\n" +
      s" -CheetahRev $getHeaderCheetahRev\n" +
      s" -FileType $getHeaderRecordType\n" +
      s" -RecordSize $getHeaderRecordSize\n" +
      s" -DspFilterDelay_µs $getHeaderDspFilterDelay\n"
  }
}

class NNHeaderNSERead(originalHeaderText: String) extends NNHeaderNeuralynxRead(originalHeaderText) with NNHeaderNSE {

  override lazy val getHeaderRecordType = nlxHeaderValueS("FileType", "Spike")

  override lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNSE.recordSize.toString)

  require(getHeaderRecordType == "Spike", s"NSE file with non-standard record type: $getHeaderRecordType")

  /**
    * [Header value NCS: "DspFilterDelay_µs"] filter delay in timestamps (microsec)
    */
  lazy val getHeaderDspFilterDelay = nlxHeaderValueI("DspFilterDelay_µs", "0")


  override def getNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${Instant.now().toString()}\n" +
      s" -CheetahRev $getHeaderCheetahRev\n" +
      s" -FileType $getHeaderRecordType\n" +
      s" -RecordSize $getHeaderRecordSize\n" +
      s" -DspFilterDelay_µs $getHeaderDspFilterDelay\n" + {
      if (originalHeaderPresent) commentLines(originalHeaderText) else ""
    }
  }

}
