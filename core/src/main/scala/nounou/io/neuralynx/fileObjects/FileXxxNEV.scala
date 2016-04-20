package nounou.io.neuralynx.fileObjects

import java.io.File

import nounou.io.neuralynx.headers.{NNHeaderNEVRead, NNHeaderNeuralynx, NNHeaderNEV}

/**
  * Static object with NEV file information.
  */
object FileNEVInfo extends FileNeuralynxInfo {
  final val recordSize = 184
}

/**
  * File handler for reading Neuralynx NEV files, to be used by file adapter.
  */
class FileReadNEV(file: File) extends FileReadNeuralynx[NNHeaderNEV](file, FileNEVInfo.recordSize){

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNEV = null
  override def header(): NNHeaderNEV = {
    if (_header == null) _header = new NNHeaderNEVRead( originalHeaderText )
    _header
  }
}

//class FileWriteNEV(file: File, headerNEV: NNHeaderNEV) extends FileWriteNeuralynx(file, headerNEV, FileNEVInfo.recordSize)
