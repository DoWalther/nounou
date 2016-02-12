package nounou.io.neuralynx.fileObjects

import java.io.File

import nounou.io.neuralynx.headers.{NNHeaderNEVRead, NNHeaderNeuralynx, NNHeaderNEV, NNHeaderNSE}

/**
  * Information and constants regarding Neuralynx NEV files in general.
  * This trait is defined (outside the companion class) to allow static external access (via Object) as well as class use.
  */
trait FileNEV {
  final val recordSize = 184
}


/**
  * Static adapter to use information in trait FileNEV.
  */
object FileNEV extends FileNEV

/**
  * File handler for reading Neuralynx NEV files, to be used by file adapter.
  */
class FileReadNEV(file: File) extends FileReadNeuralynx[NNHeaderNEV](file) with FileNEV {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNEV = null
  override def header(): NNHeaderNEV = {
    if (_header == null) _header = new NNHeaderNEVRead( originalHeaderText )
    _header
  }
}

class FileWriteNEV(file: File, headerNEV: NNHeaderNEV) extends FileWriteNeuralynx(file, headerNEV) with FileNEV {


//  override lazy val header: NNHeaderNEV = headerNEV

}
