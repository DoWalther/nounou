package nounou.io.neuralynx.headers

import nounou.elements.headers.NNHeader
import nounou.io.neuralynx.fileObjects.FileNeuralynx

/**
  * Trait encapsulating Neuralynx header information (which is text-based).
  * Each NNElement derived from a Neuralynx file should have one of these headers available.
  *
  */
trait NNHeaderNeuralynx extends NNHeader {

  /**
    * The CheetahRev variable (usu obtained from the neuralynx file text header).
    * This is defined as a def (and not as a val) to avoid initialization order problems with inherited classes.
    */
  def getHeaderCheetahRev: String

  /**
    * The FileType variable obtained from the neuralynx file text header.
    * This is defined as a def (and not as a val) to avoid initialization order problems with inherited classes.
    */
  def getHeaderRecordType: String

  /**
    * The RecordSize variable obtained from the neuralynx file text header.
    * This is defined as a def (and not as a val) to avoid initialization order problems with inherited classes.
    */
  def getHeaderRecordSize: Int

  /**
    * Returns the header string in neuralynx format, taking the value from toNeuralynxHeaderStringImpl
    * and cutting or padding it as necessary.
    */
  def getNeuralynxHeaderString(): String

  /**
    * Return a neuralynx-formatted header string appropriate for each file type.
    * If this string is too short, it will be padded, if too long, it will be truncated.
    */
  def getNeuralynxHeaderStringImpl(): String

  protected final def cutOrPad(string: String): String = {
    val temp = string.take(FileNeuralynx.headerBytes)
    if( temp.length == FileNeuralynx.headerBytes){
      logger.info(s"Neuralynx text header truncated from ${string.length} to ${FileNeuralynx.headerBytes} for writing.")
      temp
    } else {
      temp.padTo(FileNeuralynx.headerBytes, 0.toChar).toString
    }
  }

  protected final def commentLines(string: String): String = {
    string.split("\n").map("# "+ _).mkString("\n")
  }

  override def toStringFullImpl(): String = ???

  override def toStringImpl(): String = "FileType = " + getHeaderRecordType + ", CheetahRev = " + getHeaderCheetahRev

}

/**
  * Immutable class encapsulating Neuralynx header information (which is text-based).
  * Each NNElement derived from a Neuralynx file should have one of these headers available.
  *
  */
abstract class NNHeaderNeuralynxRead(val originalHeaderText: String) extends NNHeaderNeuralynx {

  loggerRequire( originalHeaderText != null, "originalHeaderText cannot be null!")

  def this() { this("") }

  @transient
  final val originalHeaderPresent = !(originalHeaderText == "")

  // <editor-fold defaultstate="collapsed" desc=" common text header information accessors ">

  //ToDo 3: conduct testing on cheetah rev version?
  final def getHeaderCheetahRev = nlxHeaderValueS("CheetahRev", "-1")

  def getHeaderRecordType: String

  def getHeaderRecordSize: Int

  // </editor-fold>

  final def getNeuralynxHeaderString(): String = cutOrPad( getNeuralynxHeaderStringImpl )

  def getNeuralynxHeaderStringImpl(): String

  override def toStringFullImpl(): String = ???

  override def toStringImpl(): String = "FileType = " + getHeaderRecordType + ", CheetahRev = " + getHeaderCheetahRev

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


}
