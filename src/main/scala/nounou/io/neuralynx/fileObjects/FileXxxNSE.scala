package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.io.neuralynx.headers.{NNHeaderNSE}

trait FileNSE {
  final val recordSize = 112
}
object FileNSE extends FileNSE

/**File handler for reading Neuralynx NSE files, to be used by file adapter.
  * Created by ktakagaki on 15/05/28.
  */
class FileReadNSE(file: File) extends FileReadNeuralynx[NNHeaderNSE](file) with FileNSE {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNSE = null
  override def getHeader: NNHeaderNSE = {
    if (_header == null) _header = new NNHeaderNSE(originalHeaderText)
    _header
  }

  require(getHeader.getHeaderRecordType == "Spike", s"NSE file with non-standard record type: ${getHeader.getHeaderRecordType}")

}

