package nounou.io.neuralynx.headers

import nounou.elements.headers.NNHeader

/** Immutable class encapsulating all Neuralynx header information (text-based).
  * Each NNElement derived from a Neuralynx file should have one of these headers available.
  *
  * Created by ktakagaki on 15/11/23.
  */
class NNHeaderNeuralynx(originalHeaderText: String, headerBytes: Int) extends NNHeader {


  /** Header cannot be merged for now.
    */
  override def isCompatible(that: nounou.elements.NNElement): Boolean = false

  /** The second and later rows of the toStringFull string.
    */
  override def toStringFullImplTail(): String = ???

  /** The contents of the toStringFull string, excluding the class head and trailing git Head.
    */
  override def toStringFullImplParams(): String = ???

  /** Text to append when writing neuralynx header again to file.
    * For the most part, you should start new header lines with "## ", so that they are handled as
    * comments.
    */
  var headerAppendText = ""

  def headerAppended = originalHeaderText + "\n" + headerAppendText

  def fullHeader: String = {

    val tempHeadAp = headerAppended

    if (tempHeadAp.length > headerBytes){
      logger.warn("headerText with appended material is longer than headerBytes")
      tempHeadAp.take(headerBytes)
    } else {
      tempHeadAp.padTo(headerBytes, 0.toChar).toString()
    }

  }


  // <editor-fold defaultstate="collapsed" desc=" parsers ">

  def nlxHeaderValueS(valueName: String, default: String): String = {
    val pattern = ("-" + valueName + """[ ]+(\S+)""").r
    pattern.findFirstIn(originalHeaderText) match {
      case Some(pattern(v)) => v
      case _ => default
    }
  }

  def nlxHeaderValueD(valueName: String, default: String) =
    nlxHeaderValueS(valueName, default).toDouble

  def nlxHeaderValueI(valueName: String, default: String) =
    nlxHeaderValueS(valueName, default).toInt

  // </editor-fold>

  def isValid(): Boolean = {
    (originalHeaderText.take(1) == "#") &&
      (headerCheetahRev != "-1")    //ToDo 3: more complicated testing on cheetah rev version?
  }

  lazy val headerRecordType = nlxHeaderValueS("FileType", "")
  lazy val headerCheetahRev = nlxHeaderValueS("CheetahRev", "-1")
  lazy val headerRecordSize = nlxHeaderValueI("RecordSize", "0")

}
