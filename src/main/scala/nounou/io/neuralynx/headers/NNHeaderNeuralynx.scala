package nounou.io.neuralynx.headers

import nounou.elements.headers.NNHeader
import nounou.io.neuralynx.fileObjects.FileNeuralynx

/** Immutable class encapsulating Neuralynx header information (whihc is text-based).
  * Each NNElement derived from a Neuralynx file should have one of these headers available.
  *
  * Created by ktakagaki on 15/11/23.
  */
abstract class NNHeaderNeuralynx(val originalHeaderText: String) extends NNHeader {

  def this() { this("") }

  @transient
  final lazy val originalHeaderPresent = !(originalHeaderText == "")
  // / && (headerCheetahRev != "-1")... this would be circular!!!

  // <editor-fold defaultstate="collapsed" desc=" common header information and validity check if text given ">

  final lazy val headerCheetahRev = nlxHeaderValueS("CheetahRev", "-1")
  val headerRecordType: String
  val headerRecordSize: Int
  //  final lazy val headerRecordType = nlxHeaderValueS("FileType", "")
  //  final lazy val headerRecordSize = nlxHeaderValueI("RecordSize", "0")

  //  def isValid(): Boolean = {
  //    (originalHeaderText.take(1) == "#") &&
  //      (headerCheetahRev != "-1")    //ToDo 3: more complicated testing on cheetah rev version?
  //  }

  // </editor-fold>

  final def toNeuralynxHeaderString(): String = cutOrPad( toNeuralynxHeaderStringImpl )
  def toNeuralynxHeaderStringImpl(): String

  final def cutOrPad(string: String): String = {
    val temp = string.take(FileNeuralynx.headerBytes)
    if( temp.length == FileNeuralynx.headerBytes){
      logger.info(s"Neuralynx text header truncated from ${string.length} to ${FileNeuralynx.headerBytes} for writing.")
      temp
    } else {
      temp.padTo(FileNeuralynx.headerBytes, 0.toChar).toString
    }
  }

  final def commentLines(string: String): String = {
    string.split("\n").map("# "+ _).mkString("\n")
  }

  /** The second and later rows of the toStringFull string.
    */
  override def toStringFullImpl(): String = ???

  /** The contents of the toStringFull string, excluding the class head and trailing git Head.
    */
  override def toStringImpl(): String = "FileType = " + headerRecordType + ", CheetahRev = " + headerCheetahRev



//  /** Text to append when writing neuralynx header again to file.
//    * For the most part, you should start new header lines with "## ", so that they are handled as
//    * comments.
//    */
//  var headerAppendText = ""
//
//  def headerAppended = originalHeaderText + "\n" + headerAppendText
//
//  def fullHeader: String = {
//
//    val tempHeadAp = headerAppended
//
//    if (tempHeadAp.length > FileNeuralynx.headerBytes){
//      logger.warn("headerText with appended material is longer than headerBytes")
//      tempHeadAp.take(FileNeuralynx.headerBytes)
//    } else {
//      tempHeadAp.padTo(FileNeuralynx.headerBytes, 0.toChar).toString()
//    }
//
//  }


  // <editor-fold defaultstate="collapsed" desc=" parsers ">

  def nlxHeaderValueS(valueName: String, default: String): String =
  if(originalHeaderPresent){
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(originalHeaderText) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  } else default

  def nlxHeaderValueD(valueName: String, default: String) = nlxHeaderValueS(valueName, default).toDouble

  def nlxHeaderValueI(valueName: String, default: String) = nlxHeaderValueS(valueName, default).toInt

  // </editor-fold>


  /** Header cannot be merged for now.
    */
  override def isCompatible(that: nounou.elements.NNElement): Boolean = false

}
