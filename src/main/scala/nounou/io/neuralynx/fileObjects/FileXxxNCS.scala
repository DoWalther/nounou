package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.elements.data.{NNDataChannel, NNDataChannelNumbered}
import nounou.io.neuralynx.headers.{NNHeaderNCS}

trait FileNCS {
  final val recordSize = 1044
}
object FileNCS extends FileNCS

/**
  * Created by ktakagaki on 15/05/28.
  */
abstract class FileReadNCS(file: File)
  extends FileReadNeuralynx(file) with FileNCS with NNDataChannel with NNDataChannelNumbered  {

  override val header: NNHeaderNCS = new NNHeaderNCS( originalHeaderText )

  require(header.headerSampleRate >= 1000d, s"NCS file with non-standard sampling frequency: ${header.headerSampleRate}")
  require(header.headerRecordType == "CSC", s"NCS file with non-standard record type: ${header.headerRecordType}")

}
