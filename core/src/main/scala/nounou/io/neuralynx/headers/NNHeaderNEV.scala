package nounou.io.neuralynx.headers

import nounou.io.neuralynx.fileObjects.{FileNEVInfo}

/**
  * Encapsulates text header for spike NSE file (Neuralynx).
  *
  */
trait NNHeaderNEV extends NNHeaderNeuralynx {

  override def getHeaderFileType: String
  override def getHeaderRecordSize: Int

//  NEV file header is simple with no extra information
//
//  override def getNeuralynxHeaderStringImpl() = {
//    super[NNHeaderNeuralynx].getNeuralynxHeaderStringImpl()
//  }

}

/**
  * Created by ktakagaki on 15/11/24.
  */
class NNHeaderNEVRead(override val originalHeaderText: String)
  extends NNHeaderNeuralynxRead(originalHeaderText)
  with NNHeaderNEV {

  override lazy val getHeaderFileType = nlxHeaderValueS("FileType", "Event")
  override lazy val getHeaderRecordSize = nlxHeaderValueI("RecordSize", FileNEVInfo.recordSize.toString)

  loggerRequire(getHeaderFileType == "Event", s"NEV file with non-standard record type: $getHeaderFileType")
  loggerRequire(getHeaderRecordSize == FileNEVInfo.recordSize, s"NEV file with non-standard record size: $getHeaderRecordSize")

  }