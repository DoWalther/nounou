package nounou.io.neuralynx.fileObjects

import java.io.File

import nounou.io.neuralynx.headers.{NNHeaderNeuralynx, NNHeaderNEV, NNHeaderNSE}

trait FileNEV {
  final val recordSize = 184
}
object FileNEV extends FileNEV

/**File handler for reading Neuralynx NEV files, to be used by file adapter.
  * Created by ktakagaki on 15/05/28.
  */
class FileReadNEV(file: File) extends FileReadNeuralynx[NNHeaderNEV](file) with FileNEV {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNEV = null
  override def getHeader: NNHeaderNEV = {
    if (_header == null) _header = new NNHeaderNEV(originalHeaderText)
    _header
  }
}

class FileWriteNEV(file: File, headerNEV: NNHeaderNEV) extends FileWriteNeuralynx(file) with FileNEV {

//  override lazy val header: NNHeaderNEV = headerNEV

}
