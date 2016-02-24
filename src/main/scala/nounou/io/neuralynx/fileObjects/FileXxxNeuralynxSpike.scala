package nounou.io.neuralynx.fileObjects

import java.io.File
import nounou.io.neuralynx.headers._
import nounou.util.{LoggingExt, getFileExtension}

object FileNeuralynxSpikeInfo extends LoggingExt {
  //final val recordSize = 112

  final def getTrodeCount(file: File): Int = getTrodeCount(file.getName)
  final def getTrodeCount(fileName: String): Int = getFileExtension(fileName) match {
    case "nse" => 1
    case "nst" => 2
    case "ntt" => 4
    case ext: String => throw loggerError(s"The extension $ext is not a valid neuralynx spike extension!")
  }


  final def getRecordSize(file: File): Int = getRecordSize(file.getName)
  final def getRecordSize(fileName: String): Int = getRecordSize( getTrodeCount(fileName) )
  final def getRecordSize(trodeCount: Int): Int = trodeCount match {
    case 1 => 112
    case 2 => 176
    case 3 => 304
    case n: Int => throw loggerError(s"The trodeCount ${n} is not a valid neuralynx trode count!")
  }

}
/**
  * Information and constants regarding Neuralynx spike files (*.nse, *.nst, *.ntt).
  */
class FileNeuralynxSpikeInfo(file: File) extends FileNeuralynxInfo {
  //final val recordSize = 112
  override final lazy val recordSize = FileNeuralynxSpikeInfo.getRecordSize(file)

}

/**
  * File handler for reading Neuralynx spike files (NSE, NST, NTT), to be used by file adapter.
  */
class FileReadNeuralynxSpike(file: File)
  extends FileReadNeuralynx[NNHeaderNeuralynxSpike](file, FileNeuralynxSpikeInfo.getRecordSize(file)){

  //The following lazy initialization done by hand to avoid var/val initialization order issues
  override final var _header: NNHeaderNeuralynxSpike = null
  override def header(): NNHeaderNeuralynxSpike = {
    if (_header == null) {
      _header = new NNHeaderNeuralynxSpikeRead(originalHeaderText)
    }
    _header
  }

  require(header.getHeaderFileType == "Spike", s"Neuralynx spike file with non-standard record type: ${header.getHeaderFileType}")

}

///**
//  * File handler for reading Neuralynx NSE files, to be used by file adapter.
//  */
//class FileReadNSE(file: File) extends FileReadNeuralynx[NNHeaderNSE](file) with FileNSE {
//
//  //The following lazy initialization done by hand to avoid var/val initialization order issues
//  override final var _header: NNHeaderNSE = null
//  override def header(): NNHeaderNSE = {
//    if (_header == null) _header = new NNHeaderNSERead(originalHeaderText)
//    _header
//  }
//
//  require(header.getHeaderFileType == "Spike", s"NSE file with non-standard record type: ${header.getHeaderFileType}")
//
//}

//class FileWriteNSE(file: File, headerNSE: NNHeaderNSE) extends FileWriteNeuralynx(file, headerNSE) with FileNSE {
//
//  //  override lazy val header: NNHeaderNEV = headerNEV
//
//}

