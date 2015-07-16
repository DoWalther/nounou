package nounou.io.neuralynx

import java.io.File

import breeze.io.RandomAccessFile

trait FileNCSConstants extends FileNeuralynxConstants {

  /**Number of bytes per record in NCS files*/
  final val recordBytes = 1044
  /**Number of samples per record in NCS files*/
  final val recordSampleCount= 512
  /**Size of non-data bytes at head of each record in NCS files*/
  final val recordNonDataHead = recordBytes - recordSampleCount * 2

}

/**
 * Created by ktakagaki on 15/05/28.
 */
trait FileNCS extends FileNeuralynx with FileNCSConstants {


  //ToDo 1: check the following
  override def recordStartByte(record: Int) = (headerBytes.toLong + recordBytes.toLong * record.toLong)

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

  /**The number of records in the ncs file, based on the file length*/
  lazy val headerRecordCount = ((handle.length - headerBytes).toDouble/headerRecordSize.toDouble).toInt

  /**Standard timestamp increment for contiguous records*/
  lazy val headerRecordTSIncrement = (1000000D * headerRecordSize.toDouble/headerSampleRate).toLong

  // <editor-fold defaultstate="collapsed" desc=" recordIndex ">

  def recordIndexStartByte(record: Int, index: Int) = {
    recordStartByte(record) + 20L + (index * 2)
  }

  def cumulativeFrameToRecordIndex(cumFrame: Int) = {
    ( cumFrame / recordSampleCount, cumFrame % recordSampleCount)
  }

  // </editor-fold>



}
