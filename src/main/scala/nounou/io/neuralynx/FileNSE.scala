package nounou.io.neuralynx

import java.io.File

import breeze.io.RandomAccessFile

  /**
 * Created by ktakagaki on 15/05/28.
 */
trait FileNSEConstants extends FileNeuralynxConstants {

    /**Number of bytes per record in NSE files*/
    override final val recordBytes = 112

  }

trait FileNSE extends FileNeuralynx with FileNSEConstants {

  // <editor-fold defaultstate="collapsed" desc=" header reading ">

  require(headerRecordType == "Spike", s"NSE file with non-standard record type: $headerRecordType")
  require(headerRecordSize == recordBytes, s"NSE file with non-standard record size: $headerRecordSize")
  require(headerSampleRate >= 1000d, s"NCS file with non-standard sampling frequency: $headerSampleRate")

  // </editor-fold>

  override def checkValidFile(): Boolean = {
    super.checkValidFile() && (headerRecordType == "Spike")
  }


}
