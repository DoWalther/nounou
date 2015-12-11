package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.io.neuralynx.headers.{NNHeaderNSE}

trait FileNSE {
  final val recordSize = 112
}
object FileNSE extends FileNSE

/**
 * Created by ktakagaki on 15/05/28.
 */
class FileReadNSE(file: File) extends FileReadNeuralynx(file) with FileNSE {

  override lazy val header: NNHeaderNSE = new NNHeaderNSE( originalHeaderText )

  require(header.headerRecordType == "Spike", s"NSE file with non-standard record type: ${header.headerRecordType}")

}

