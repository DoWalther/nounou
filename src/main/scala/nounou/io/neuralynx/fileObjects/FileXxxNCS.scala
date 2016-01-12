package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.elements.data.NNDataChannel
import nounou.elements.data.traits.NNDataChannelNumbered
import nounou.io.neuralynx.headers.{NNHeaderNCS}

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
  extends FileReadNeuralynx[NNHeaderNCS](file) with FileNCS with NNDataChannel with NNDataChannelNumbered  {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNCS = null

  override def header(): NNHeaderNCS = {
    if (_header == null) _header = new NNHeaderNCS(originalHeaderText)
    _header
  }

  require(header.getHeaderSampleRate >= 1000d, s"NCS file with non-standard sampling frequency: ${header.getHeaderSampleRate}")
  require(header.getHeaderADMaxValue == 32767, s"NCS file with non-standard ADMaxValue: ${header.getHeaderADMaxValue}")
  require(header.getHeaderRecordType == "CSC", s"NCS file with non-standard record type: ${header.getHeaderRecordType}")

}
