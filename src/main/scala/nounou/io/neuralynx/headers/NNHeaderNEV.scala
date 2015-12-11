package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.FileNEV

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNEV(override val originalHeaderText: String)
  extends NNHeaderNeuralynx(originalHeaderText) {

  def this() = this("")

  final lazy val headerRecordType = nlxHeaderValueS("FileType", "Event")
  final lazy val headerRecordSize = nlxHeaderValueI("RecordSize", FileNEV.recordSize.toString)


  override def toNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
    s"## Output by Nounou v ${version}\n" +
    s"## Output time ${System.currentTimeMillis()}\n" +
    s" -CheetahRev $headerCheetahRev\n" +
    s" -FileType $headerRecordType\n" +
    s" -RecordSize $headerRecordSize\n" +
    {if(originalHeaderPresent) commentLines(originalHeaderText) else ""}
  }

  }