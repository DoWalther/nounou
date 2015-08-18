package nounou.io.neuralynx

import java.io.File

import breeze.io.RandomAccessFile

trait FileNCSConstants extends FileNeuralynxConstants {

  /**Number of bytes per record in NCS files*/
  override final val recordBytes = 1044

  /**Number of samples per record in NCS files*/
  final val recordNCSSampleCount= 512
  /**Size of non-data bytes at head of each record in NCS files*/
  final val recordNonNCSSampleHead = recordBytes - recordNCSSampleCount * 2

  final def recordIndexStartByte(record: Int, index: Int) = {
    recordStartByte(record) + 20L + (index * 2)
  }

  def cumulativeFrameToRecordIndex(cumFrame: Int) = {
    ( cumFrame / recordNCSSampleCount, cumFrame % recordNCSSampleCount)
  }

}

  /**
 * Created by ktakagaki on 15/05/28.
 */
trait FileNCS extends FileNeuralynx with FileNCSConstants {

  // <editor-fold defaultstate="collapsed" desc=" header reading ">


  lazy val headerAcqEntName = nlxHeaderParserS("AcqEntName", "NoName")
  lazy val headerRecordSize = nlxHeaderParserI("RecordSize", "0")
  require(headerRecordSize == recordBytes,
    s"NCS file with non-standard record size: $headerRecordSize")
  /**Sample rate, Hz*/
  lazy val headerSampleRate = nlxHeaderParserD("SamplingFrequency", "1")
  require(headerSampleRate >= 1000d, //tempSampleFreqD == sampleRate,
    s"NCS file with non-standard sampling frequency: $headerSampleRate")
  lazy val headerADBitVolts = nlxHeaderParserD("ADBitVolts", "1")

  // </editor-fold>

  /**The number of records in the ncs file, depends on the file length of the handle.*/
  lazy val headerRecordCount = ((handle.length - headerBytes).toDouble/recordBytes.toDouble).toInt

  /**Standard timestamp increment for contiguous records, depends on sample rate from header.*/
  lazy val headerRecordTSIncrement = (1000000D * recordBytes.toDouble/headerSampleRate).toLong

}
