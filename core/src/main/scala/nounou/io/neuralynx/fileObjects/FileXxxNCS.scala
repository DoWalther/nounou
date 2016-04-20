package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.elements.data.NNDataChannel
import nounou.elements.traits.NNChannelNumbered
import nounou.io.neuralynx.headers.{NNHeaderNCSRead, NNHeaderNCS}

/**
  * Information and constants regarding Neuralynx NCS files in general.
  * This trait is defined (outside the companion class) to allow static external access (via Object) as well as class use.
  */
trait FileNCS {
  final val recordSize = 1044
}

/**
  * Static adapter to use information in trait FileNCS.
  */
object FileNCS extends FileNCS

/**
  * File handler for reading Neuralynx NCS files, to be used by file adapter.
  */
abstract class FileReadNCS(file: File)
  extends FileReadNeuralynx[NNHeaderNCS](file, FileNCS.recordSize)
  with NNDataChannel with NNChannelNumbered  {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNCS = null

  override def header(): NNHeaderNCS = {
    if (_header == null) _header = new NNHeaderNCSRead(originalHeaderText)
    _header
  }

//  require(header.getHeaderFileType == "CSC", s"NCS file with non-standard record type: ${header.getHeaderFileType}")

}
