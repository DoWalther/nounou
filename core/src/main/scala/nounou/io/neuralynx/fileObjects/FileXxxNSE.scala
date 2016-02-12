package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.io.neuralynx.headers.{NNHeaderNEV, NNHeaderNSERead, NNHeaderNSE}

/**
  * Information and constants regarding Neuralynx NCS files in general.
  * This trait is defined (outside the companion class) to allow static external access (via Object) as well as class use.
  */
trait FileNSE {
  final val recordSize = 112
}

/**
  * Static adapter to use information in trait FileNSE.
  */
object FileNSE extends FileNSE

/**
  * File handler for reading Neuralynx NSE files, to be used by file adapter.
  */
class FileReadNSE(file: File) extends FileReadNeuralynx[NNHeaderNSE](file) with FileNSE {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNSE = null
  override def header(): NNHeaderNSE = {
    if (_header == null) _header = new NNHeaderNSERead(originalHeaderText)
    _header
  }

  require(header.getHeaderFileType == "Spike", s"NSE file with non-standard record type: ${header.getHeaderFileType}")

}

class FileWriteNSE(file: File, headerNSE: NNHeaderNSE) extends FileWriteNeuralynx(file, headerNSE) with FileNSE {

  //  override lazy val header: NNHeaderNEV = headerNEV

}

