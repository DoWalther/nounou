package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.elements.data.NNDataChannel
import nounou.elements.data.traits.NNDataChannelNumbered
import nounou.io.neuralynx.headers.{NNHeaderNCS}

trait FileNCS {
  final val recordSize = 1044
}

/**Information regarding neuralynx continuous files.
  * This object is defined to allow static external access.
  */
object FileNCS extends FileNCS

/**File handler for reading Neuralynx NCS files, to be used by file adapter.
  * Created by ktakagaki on 15/05/28.
  */
abstract class FileReadNCS(file: File)
  extends FileReadNeuralynx[NNHeaderNCS](file) with FileNCS with NNDataChannel with NNDataChannelNumbered  {

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNCS = null
  override def getHeader: NNHeaderNCS = {
    if (_header == null) _header = new NNHeaderNCS(originalHeaderText)
    _header
  }

  require(getHeader.getHeaderSampleRate >= 1000d, s"NCS file with non-standard sampling frequency: ${getHeader.getHeaderSampleRate}")
  require(getHeader.getHeaderRecordType == "CSC", s"NCS file with non-standard record type: ${getHeader.getHeaderRecordType}")

}
