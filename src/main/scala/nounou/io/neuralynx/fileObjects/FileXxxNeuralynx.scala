package nounou.io.neuralynx.fileObjects

import java.io.{IOException, File}

import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import nounou.io.neuralynx.headers.NNHeaderNeuralynx
import nounou.util.LoggingExt

import scala.reflect.ClassTag

object FileNeuralynx {

  /**The total number of bytes in the initial Neuralynx text header.*/
  val headerBytes = 16384

}

/** This trait encapsulates the functions needed to read neuralynx files (NCS, NEV, NEX, NSE, NST, NTT).
  * Among other functions, it provides file handle handling and header reading.
 *
* @author ktakagaki
*/
abstract class FileNeuralynx(val file: File) extends LoggingExt {
  def this(fileName: String) {   this(new File(fileName))  }

  /**Number of bytes per record, specified in each class, but should agree with
    * text information in header.headerRecordSize .*/
  val recordSize: Int
  require(header.headerRecordSize == recordSize, s"File with non-standard record size: ${header.headerRecordSize}")

  //ToDo 1: check the following
  final def recordStartByte(record: Int): Long = (headerBytes.toLong + recordSize.toLong * record.toLong)

  //  /** The Neuralynx file handle. This construct is somewhat delicate, since
  //    * all classes MUST override this file handle early in the initialization
  //    * sequence, or else the downstream
  //    * lazy values relying on it (handle, fileName, originalHeaderText, etc.)
  //    * will fail.
  //    *
  //    * This is programmed this way so that the code for intializing the file handle val
  //    * can be encapsulated in this trait (trait initialization order is
  //    * complex, but we can assume that lazy values are handled at the end.
  //    */
  //  val file: File
  val handle: RandomAccessFile// = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)
  final lazy val fileName = file.getCanonicalPath

  /**The total number of bytes in the initial Neuralynx text header.*/
  final val headerBytes = FileNeuralynx.headerBytes


  /**The number of records in the file, depends on the file length of the handle.*/
  lazy val headerRecordCount = ((handle.length - headerBytes).toDouble/recordSize.toDouble).toInt

  // <editor-fold defaultstate="collapsed" desc=" header related ">

  /**
    *
    */
  val header: NNHeaderNeuralynx

}

abstract class FileReadNeuralynx(override val file: File) extends FileNeuralynx(file) {

  //Beware that the following MUST be lazy, in order to read the correct file handle during initialization.
  //Non-lazy initialization will lead to null pointer error, since this would be processed before handle is created
  //in parent class.
  lazy val originalHeaderText: String = {
    val tempString = new String(handle.readUInt8(headerBytes).map(_.toChar))
    tempString.replaceAll( """(?m)[\s\x00]+$""", "")
  }

  override lazy val handle: RandomAccessFile = new RandomAccessFile(file, "w")(ByteConverterLittleEndian)

}

abstract class FileWriteNeuralynx(override val file: File) extends FileNeuralynx(file) {

  override lazy val handle: RandomAccessFile = new RandomAccessFile(file, "w")(ByteConverterLittleEndian)

}
