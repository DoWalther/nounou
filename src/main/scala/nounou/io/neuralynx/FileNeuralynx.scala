package nounou.io.neuralynx

import java.io.File

import breeze.io.{ByteConverterLittleEndian, RandomAccessFile}
import nounou.elements.NNElement
import nounou.io.FileLoader
import nounou.util.LoggingExt

/** This trait encapsulates the constants needed to read neuralynx files (NCS, NEV, NEX, NSE, NST, NTT).
  * Among other functions, it provides file handle handling and header reading.
  *
  * @author ktakagaki
  */
trait FileNeuralynxConstants {

  /**Number of bytes per record.*/
  val recordBytes: Int

  //ToDo 1: check the following
  final def recordStartByte(record: Int): Long = (headerBytes.toLong + recordBytes.toLong * record.toLong)

  /**The total number of bytes in the initial Neuralynx text header.*/
  final val headerBytes = 16384


}

/** This trait encapsulates the functions needed to read neuralynx files (NCS, NEV, NEX, NSE, NST, NTT).
  * Among other functions, it provides file handle handling and header reading.
 *
* @author ktakagaki
*/
trait FileNeuralynx extends LoggingExt with FileNeuralynxConstants{

  /** The Neuralynx file handle. This construct is very delicate, since
    * all classes MUST override this file handle early in the initialization
    * sequence, or else the downstream
    * lazy values relying on it (handle, fileName, originalHeaderText, etc.)
    * will fail.
    *
    * This is programmed this way so that the code for intializing the file handle val
    * can be encapsulated in this trait (trait initialization order is
    * complex, but we can assume that lazy values are handled at the end.
    */
  val file: File
  lazy val handle: RandomAccessFile = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)
  lazy val fileName = file.getCanonicalPath


  // <editor-fold defaultstate="collapsed" desc=" header related ">


  lazy val originalFileHeader = {
    handle.seek(0)
    val tempstr = new String(handle.readUInt8(headerBytes).map(_.toChar))
    //println(tempstr.toCharArray.toList)
    //println(tempstr.toCharArray.last.isWhitespace)
    //tempstr.take( tempstr.lastIndexWhere(!_.isWhitespace) + 1 ) //strip trailing whitespaces
    //println(tempstr.replaceAll("""(?m)[\s\x00]+$""","").toCharArray.toList)
    tempstr.replaceAll("""(?m)[\s\x00]+$""","")
  }

  /** Text to append when writing neuralynx header again to file.
    * For the most part, you should start new header lines with "## ", so that they are handled as
    * comments.
    */
  var headerAppendText = ""

  def headerAppended = originalFileHeader + "\n" + headerAppendText

  def fullHeader: String = {

    val tempHeadAp = headerAppended

    if (tempHeadAp.length > headerBytes){
      logger.warn("headerText with appended material is longer than headerBytes")
      tempHeadAp.take(headerBytes)
    } else {
      tempHeadAp.padTo(headerBytes, 0.toChar).toString()
    }

  }

  def nlxHeaderParserS(valueName: String, default: String): String = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(originalFileHeader) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }

  def nlxHeaderParserD(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toDouble

  def nlxHeaderParserI(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toInt

  // </editor-fold>

}