package nounou.io.neuralynx

import java.io.File

import breeze.io.RandomAccessFile
import nounou.elements.NNElement
import nounou.io.FileLoader
import nounou.util.LoggingExt


/**
 *
* @author ktakagaki
* //@date 12/16/13
*/
trait FileNeuralynx {

  val file: File
  lazy val handle: RandomAccessFile = new RandomAccessFile(file)
  lazy val fileName = file.getCanonicalPath

  // <editor-fold defaultstate="collapsed" desc=" header related ">

  /**The total number of bytes in the initial Neuralynx text header.*/
  final val headerBytes = 16384

  @transient
  lazy val headerText = {
    handle.seek(0)
    new String(handle.readUInt8(headerBytes).map(_.toChar))
  }

  def nlxHeaderParserS(valueName: String, default: String): String = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(headerText) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }
  def nlxHeaderParserD(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toDouble
  def nlxHeaderParserI(valueName: String, default: String) = nlxHeaderParserS(valueName: String, default: String).toInt

  // </editor-fold>


  /**Size of each record, in bytes*/
  val recordBytes: Int

  /**Method to calculate start of each record within the file, in bytes*/
  def recordStartByte(record: Int): Int


}

//object FileAdapterNeuralynx {
//
//  val instance =
//
//}
