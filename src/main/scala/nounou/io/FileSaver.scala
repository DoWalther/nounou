package nounou.io

import java.io.File
import java.util.ServiceLoader

import nounou.NN._
import nounou.elements.NNElement
import nounou.util.LoggingExt
import scala.collection.JavaConverters._
import scala.collection.immutable

/** This singleton FileSaver object is the main point of use for file saving
  * (use via [[nounou.NN.save(fileName:String,data:Ar*]]).
  * It maintains a list of available savers in the system (from /resources/META-INF/services/nounou.io.FileSaver),
  * and uses the first saver which satisfies the specified file extension and data objects.
  *
  * Alternatively, specific FileSaver objects can be used, such as:
  * [[nounou.io.neuralynx.fileAdapters.FileAdapterNCS.save(*]]. In this case, you can specify
  * non-standard file extensions (will get warning; not recommended).
  *
  */
object FileSaver {

  /** List of valid loaders available in the system (from /resources/META-INF/services/nounou.io.FileSaver)
    */
  private lazy val savers = ServiceLoader.load(classOf[FileSaver])//.iterator.asScala

  private var possibleSaverBuffer = new immutable.HashMap[String, List[FileSaver]]()

  final def save(fileName: String, data: NNElement): Unit = save(fileName, Array(data))

  //ToDo3: Make code parallel to FileLoader, esp. vis-a-vis when duplicate savers found
  final def save(fileName: String, data: Array[NNElement]): Unit = {

    val fileExtension = nounou.util.getFileExtension(fileName)

    val extensionSaverIterator = (possibleSaverBuffer.get(fileExtension) match {

      //If the saver for this extension has already been loaded
      //This includes the case where no real saver was found for a given extension, and FileSaverNull was loaded as a marker
      case l: Some[List[FileSaver]] => l.get

      //If the given extension has not been tested yet, it will be searched for within the available loaders
      case _ => {
        val possibleSavers: List[FileSaver] = savers.iterator.asScala.filter( _.canSave(fileExtension)).toList

        if( possibleSavers.length == 0 ) {
          throw loggerError(s"Cannot find saver for file name: $fileName")
        }
        possibleSaverBuffer = possibleSaverBuffer.+( (fileExtension, possibleSavers) )
        possibleSavers
      }
    }).toIterator

    var realSaver: FileSaver = null

    while( extensionSaverIterator.hasNext && realSaver == null ) {
      val candidateSaver = extensionSaverIterator.next()
      if( candidateSaver.canSaveObjectArray(data) ) realSaver = candidateSaver
    }
    if(realSaver == null) {
      throw loggerError(s"Cannot find saver for name $fileName and type "+
        s"Array(${data.map(_.className).mkString(", ")})")
    }
    realSaver.save(fileName, data)

  }

}

/**This trait marks individual file adapter classes as being able to handle
  * the saving of certain object types. FileSaver classes should be
  * registered to the JVM with entries in
  * "/resources/META-INF/services/nounou.io.FileSaver"
  *
  * It is implemented as a trait (and not an abstract class),
  * since some FileLoader classes will also be FileSaver classes, and
  * double inheritance of classes is not possible in Scala.
  */
trait FileSaver extends LoggingExt {

  /**'''__MUST OVERRIDE__''' A list of __lower-case__ extensions which can be saved.*/
  val canSaveExtensions: Array[String]
  /** Standard file extension name (in lower case) with which to save, when no valid extension given.
    * Specified as "lazy" so that inheriting classes can first define [[canSaveExtensions]]*/
  final lazy val standardExtension: String = canSaveExtensions(0)

  /**Whether the given file can be saved. For now, based simply on a match with the file extension.*/
  final def canSave(file: File): Boolean = canSave( file.getName )
  /**Whether the given file (or extension, if plain extension given)
    * can be saved. For now, based simply on a match with [[canSaveExtensions]].*/
  final def canSave(fileName: String): Boolean = canSaveExtensions.contains( nounou.util.getFileExtension(fileName).toLowerCase )

  /** Whether a certain array of objects can be saved, implement with match.*/
  def canSaveObjectArray(obj: Array[NNElement]): Boolean


  /**'''__MUST OVERRIDE__''' Actual saving of file. Assume that canSaveObjectArray(data)==true.
    * @param fileName if the filename does not end with the correct extension, the standard extension will be appended.
    *                 If the filename exists, it will be given a postscript after the filename.
    */
  def saveImpl(fileName: String, data: Array[NNElement]): Unit

  /**Checks if canSaveObjectArray(data)==true, and if so, calls saveImpl().
    */
  final def save(fileName: String, data: Array[NNElement]): Unit = {
    if(canSaveObjectArray(data)) saveImpl(fileName, data)
    else throw loggerError("data input {} cannot be saved with this FileSaver object!", data.toString )
  }
  final def save(fileName: String, data: NNElement): Unit = save(fileName, Array(data))

  override def toString() = getClass.getName + "( canSaveExtensions=" + canSaveExtensions.toList +")"


}

/** This [[FileLoader]] instance serves as a placeholder in the loader list
  * for extensions which have already been
  * searched for in the META-INF and do not exist.
  */
final class FileSaverNull(override val canSaveExtensions: Array[String]) extends FileSaver{

  def this(canSaveExtension: String) {
    this( Array[String](canSaveExtension) )
  }

  override def saveImpl(fileName: String, data: Array[NNElement]): Unit = {
    throw loggerError(s"The file ${fileName} has no valid FileSaver yet.")
  }

  override def canSaveObjectArray(obj: Array[NNElement]): Boolean = false
}