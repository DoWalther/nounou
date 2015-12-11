package nounou.io.neuralynx.fileObjects

import java.io.File

import nounou.io.neuralynx.headers.{NNHeaderNeuralynx, NNHeaderNEV, NNHeaderNSE}

trait FileNEV {
  final val recordSize = 184
}
object FileNEV extends FileNEV

/**
 * Created by ktakagaki on 15/05/28.
 */
class FileReadNEV(file: File) extends FileReadNeuralynx(file) with FileNEV {

  override lazy val header: NNHeaderNEV = new NNHeaderNEV(originalHeaderText)
  require(header.headerRecordType == "Event", s"NEV file with non-standard record type: ${header.headerRecordType}")

}

class FileWriteNEV(file: File, headerNEV: NNHeaderNEV) extends FileWriteNeuralynx(file) with FileNEV {

  override lazy val header: NNHeaderNEV = headerNEV

}
