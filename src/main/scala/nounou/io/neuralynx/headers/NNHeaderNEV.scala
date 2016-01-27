package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.FileNEV

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNEV(override val originalHeaderText: String)
  extends NNHeaderNeuralynxRead(originalHeaderText) {

  def this() = this("")

  def getHeaderRecordType = nlxHeaderValueS("FileType", "Event")
  def getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNEV.recordSize.toString)


  override def getNeuralynxHeaderStringImpl() = {
    "######## Neuralynx Data File Header\n" +
    s"## Output by Nounou v ${version}\n" +
    s"## Output time ${System.currentTimeMillis()}\n" +
    s" -CheetahRev $getHeaderCheetahRev\n" +
    s" -FileType $getHeaderRecordType\n" +
    s" -RecordSize $getHeaderRecordSize\n" +
    {if(originalHeaderPresent) commentLines(originalHeaderText) else ""}
  }

  }