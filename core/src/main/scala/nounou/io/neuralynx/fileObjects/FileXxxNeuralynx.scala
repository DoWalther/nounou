package nounou.io.neuralynx.fileObjects

import java.io.{IOException, File}

import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import nounou.io.neuralynx.headers.NNHeaderNeuralynx
import nounou.util.LoggingExt

import scala.reflect.ClassTag

/**
  * Information about Neuralynx files in general.
  * This trait is defined (outside the companion object) to allow
  * inheritance to individual Neuralynx file type information objects.
  */
trait FileNeuralynxInfo {
  /** The total number of bytes in the initial Neuralynx text header, always fixed. */
  val headerBytes = FileNeuralynx.headerBytes
  /** The total number of bytes per Neuralynx record page, differs by individual file type. */
  val recordSize: Int
}

object FileNeuralynx {
  val headerBytes = 16384
}

/**
  * This abstract class encapsulates common functions needed to read Neuralynx files (NCS, NEV, NEX, NSE, NST, NTT).
  * It mainly provides file handle management and header text reading infrastructure.
  *
  * @param recordSize Number of bytes per record, should agree with
  *                   text information in NNHeaderNeuralynx.headerRecordSize.
  * @author ktakagaki
  *
  */
abstract class FileNeuralynx(val file: File, val recordSize: Int) extends LoggingExt with FileNeuralynxInfo{

  //def this(fileName: String) {   this(new File(fileName))  }

  //ToDo 1: check the following
  /**The byte within a file where a specific record should start.
    * Depends upon FileNeuralynx.headerBytes and recordSize, which is specified in each child class.
    */
  final def recordStartByte(record: Int): Long = (headerBytes.toLong + recordSize.toLong * record.toLong)

  /** The Neuralynx file handle, either a reading handle or a writing handle.
    * This val construct is somewhat delicate, since
    * all non-abstract classes MUST define/override this file handle early in the constructor initialization
    * sequence, or else downstream
    * lazy values relying on it (originalHeaderText, etc.)
    * will fail.
    *
    * This is programmed this way so that the code for intializing the file handle val
    * can be encapsulated in this trait (trait initialization order is
    * complex, but we can assume that lazy values are handled at the end.
    */
  protected val handle: RandomAccessFile

  final lazy val fileName = file.getCanonicalPath

}

/**
  * Abstract class to read Neuralynx files.
  *
  * Already in the default constructor, children of this class will create a
  * read file handle and read the header text
  * (child class implementations can use this to access header values.
  *
  */
abstract class FileReadNeuralynx[T <: NNHeaderNeuralynx](
                 file: File,
                 recordSize: Int
               ) extends FileNeuralynx(file, recordSize) with LoggingExt {

  val handle: RandomAccessFile = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

  /**
    * Get encapsulating class for file-type specific neuralynx header.
    * This is defined as a function (and not a val) to avoid initialization null pointer issues.
    *
    */
  def header(): T

  /**
    * Java-style alias for [[nounou.io.neuralynx.fileObjects.FileReadNeuralynx.header() header()]]
    *
    */
  final def getHeader(): T = header()

  /**
    * Protected header buffer to use in manual lazy initialization.
    */
  protected var _header: T

  //Beware that the following MUST be initialized BEFORE children rely on it, but AFTER the handle object.
  protected val originalHeaderText: String = {
    val tempString = new String(handle.readUInt8(headerBytes).map(_.toChar))
    tempString.replaceAll( """(?m)[\s\x00]+$""", "")
  }

  /**
    * [Header value Neuralynx] The number of records in the file, depends on the file length of the handle.
    */
  lazy val headerRecordCount = ((handle.length - headerBytes).toDouble/recordSize.toDouble).toInt
  loggerRequire(header.getHeaderRecordSize == recordSize, s"File with non-standard record size: ${header.getHeaderRecordSize}")

}

/**
  * Abstract class to write Neuralynx files.
  *
  * Already in the default constructor, children of this class will create a
  * read/write file handle and write header information to the file
  * (from child class implementations in getNeuralynxHeaderString().
  *
  */
class FileWriteNeuralynx(
                 file: File,
                 header: NNHeaderNeuralynx,
                 recordSize: Int
               ) extends FileNeuralynx(file, recordSize) {

  val handle: RandomAccessFile = new RandomAccessFile(file, "rw")(ByteConverterLittleEndian)
  handle.seek(0)
  //handle.writeChars( header.getNeuralynxHeaderString() )
  handle.writeUInt8( header.getNeuralynxHeaderString().toCharArray.map(_.toShort) )

}
