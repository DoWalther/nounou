package nounou.io.neuralynx.headers

import nounou.elements.headers.NNHeader
import nounou.io.neuralynx.fileObjects.{FileNeuralynx, FileNeuralynxInfo}
import org.apache.commons.io.filefilter.FalseFileFilter
import sun.font.TrueTypeFont

/**
  * Trait encapsulating Neuralynx header information (which is text-based).
  * Each NNElement derived from a Neuralynx file should have one of these headers available.
  *
  */
trait NNHeaderNeuralynx extends NNHeader {

  def originalHeaderText: String
  final def originalHeaderPresent: Boolean = (originalHeaderText == "")

  /**
    * The CheetahRev variable (usu obtained from the neuralynx file text header).
    * This is defined as a def (and not as a val) to avoid initialization order problems with inherited classes.
    */
  def getHeaderCheetahRev: String

  /**
    * The FileType variable obtained from the neuralynx file text header.
    * This is defined as a def (and not as a val) to avoid initialization order problems with inherited classes.
    */
  def getHeaderFileType: String

  /**
    * The RecordSize variable obtained from the neuralynx file text header.
    * This is defined as a def (and not as a val) to avoid initialization order problems with inherited classes.
    */
  def getHeaderRecordSize: Int



  /**
    * Returns the header string in neuralynx format, taking the value from getNeuralynxHeaderStringImpl
    * and cutting or padding it as necessary.
    */
  final def getNeuralynxHeaderString(): String = cutOrPad(
    getNeuralynxHeaderStringImpl +
    {if( originalHeaderPresent ) "\n\n" + commentLines( originalHeaderText ) else ""}
  )

  /**
    * Return a neuralynx-formatted header string appropriate for each file type.
    * If this string is too short, it will be padded, if too long, it will be truncated.
    */
  def getNeuralynxHeaderStringImpl(): String = {
    "######## Neuralynx Data File Header\n" +
      s"## Output by Nounou v ${version}\n" +
      s"## Output time ${System.currentTimeMillis()}\n" +
      "\n" +
      s"-CheetahRev $getHeaderCheetahRev\n" +
      "\n" +
      s"-FileType $getHeaderFileType\n" +
      s"-RecordSize $getHeaderRecordSize\n"

  }

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

  override def toStringImpl(): String = "FileType = " + getHeaderFileType + ", CheetahRev = " + getHeaderCheetahRev

}

/**
  * Immutable class encapsulating Neuralynx header information (which is text-based).
  * Each NNElement derived from a Neuralynx file should have one of these headers available.
  *
  */
abstract class NNHeaderNeuralynxRead(val originalHeaderText: String) extends NNHeaderNeuralynx {

  loggerRequire( originalHeaderText != null, "originalHeaderText cannot be null!")

  def this() { this("") }

//  @transient
//  final val originalHeaderPresent = !(originalHeaderText == "")

  // <editor-fold defaultstate="collapsed" desc=" common text header information accessors ">

  //ToDo 3: conduct testing on cheetah rev version?
  final def getHeaderCheetahRev = nlxHeaderValueS("CheetahRev", "-1")

  def getHeaderFileType: String

  def getHeaderRecordSize: Int

  // </editor-fold>



  override def toStringFullImpl(): String = ""

  override def toStringImpl(): String = "FileType = " + getHeaderFileType + ", CheetahRev = " + getHeaderCheetahRev

  // <editor-fold defaultstate="collapsed" desc=" parsers ">

  def nlxHeaderValueS(valueName: String, default: String): String = {
    //if(originalHeaderPresent){
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(originalHeaderText) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }
 // } else default

  def nlxHeaderValueD(valueName: String, default: String) = nlxHeaderValueS(valueName, default).toDouble

  def nlxHeaderValueI(valueName: String, default: String) = nlxHeaderValueS(valueName, default).toInt

  def nlxHeaderValueB(valueName: String, default: Boolean) =
    nlxHeaderValueS(valueName, default.toString).toLowerCase match {
      case x if x.substring(0,1) == "t" => true
      case x if x.substring(0,1) == "f" => false
      case x if x.substring(0,1) == "y" => true
      case x if x.substring(0,1) == "n" => false
      case x if x.substring(0,1) == "e" => true //Enabled
      case x if x.substring(0,1) == "d" => false //Disabled
      case x if x.substring(0,1) == "j" => true
      case "0" => false
      case _ => true
    }

  // </editor-fold>


}

abstract class NNHeaderNeuralynxConcrete(override val getHeaderCheetahRev: String,
                                         override val getHeaderFileType: String,
                                         override val getHeaderRecordSize: Int)
  extends NNHeaderNeuralynx
