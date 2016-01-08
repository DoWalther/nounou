package nounou.elements

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import nounou.elements.traits.layout.NNLayoutHexagonal
import nounou.util.LoggingExt

/** Base class for all data elements.
  * Mainly provides:
  *   + git tracking, versioning
  *   + toString printout basics
  *   + JSON serialization
  *   + logging capabilities
  */
trait NNElement extends LoggingExt {

//  /** '''[NNElement]''' __'''SHOULD OVERRIDE'''__
//    */
//  def header(): NNHeader = null
  //ToDo3: consider add: pt info, rec info, rec start time/date, etc

  /**'''[NNElement]''' getCanonicalName, buffered for serialization with GSON. */
  lazy val className = this.getClass.getCanonicalName
  //This temporary val is necessary to trigger initialization of `object NNGit`
  def nnGitObj = nounou.util.NNGit
  /**'''[NNElement]''' Git HEAD of the current revision, buffered for serialization with GSON.*/
  lazy val gitHead = nnGitObj.getGitHead
  /**'''[NNElement]''' Git HEAD shortened to first 10 characters.*/
  @transient
  lazy val gitHeadShort = gitHead.take(10)

  /**'''[NNElement]''' Reads global [[nounou]] version number, buffered for serialization with GSON.*/
  val version = nounou.version

  /**'''[NNElement]'''*/
  def toJsonString: String = nounou.gson.toJson( this )

  // <editor-fold defaultstate="collapsed" desc=" toString and related ">

  override final def toString(): String = getClass.getSimpleName + "(" + toStringImpl + ")"
  final def toString(simple: Boolean): String =
    if(simple) toString()
    else getClass.getName + "(" + toStringImpl + ")"

  /** The contents of the [[nounou.elements.NNElement.toString()*]] output to be given within parenthesis after the class name.
    */
  def toStringImpl(): String
  /** Lines to be output for [[nounou.elements.NNElement.toStringFull()*]], after [[nounou.elements.NNElement.toString()*]] output and line divider.
    */
  def toStringFullImpl(): String
  /**Usually multiline output string, starting with [[nounou.elements.NNElement.toString()*]] output and then
    * more detailed information, where available.
    */
  final def toStringFull(): String = {
    val tempTail =
      if( toStringFullImpl() == "" ){ "" }
      else {
        "\n================================================================================\n" +
        toStringFullImpl()
      }
    toString(false).dropRight(1) + s", $gitHeadShort)" + tempTail
  }

  // </editor-fold>

}

class NNElementDeserializeIntermediate {
  var className: String = ""
  var gitHead: String = ""
}


object NNElement {

  /** Loads this [[nounou.elements.NNElement]] object
    * from a JSON text file created via GSON, with correct class type obtained from
    * serialized className data.
    */
  def loadJson(file: File): NNElement = {
    val gsonString = Files.readAllLines( file.toPath, StandardCharsets.UTF_8 ).toArray.mkString("\n")
    val tempObj = nounou.gson.fromJson( gsonString, classOf[NNElementDeserializeIntermediate] )
    val targetClass = Class.forName(tempObj.className)
    val tempret = nounou.gson.fromJson( gsonString, targetClass ).asInstanceOf[NNElement]//targetClass.type]
    //the following casting is not elegant, but seems necessary to satisfy the scala compiler,
    //which doesn't seem to be able to infer the type of XXXXX.asInstanceOf[targetClass.type]
    tempret match {
      case x: NNLayoutHexagonal => x.asInstanceOf[NNLayoutHexagonal]
      case x: NNElement => x
    }
  }

}