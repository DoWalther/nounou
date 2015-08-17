package nounou.io

import java.io.File
import java.util.ServiceLoader

import nounou.NN._
import nounou.elements.data.{NNDataChannelArray, NNDataChannel}
import nounou.util.LoggingExt
import scala.collection.JavaConverters._
import nounou.elements.NNElement
import scala.collection.mutable

/** This singleton FileLoader object is the main point of use for file saving
  * (use via [[nounou.NN.load()]]).
  * It maintains a list of available loaders in the system (from /resources/META-INF/services/nounou.io.FileLoader),
  * and uses the first loader which can load the specified file extension.
  *
  * Alternatively, specific FileLoader objects can be used, such as:
  * [[nounou.io.neuralynx.FileAdapterNCS.load()]]. In this case, you can try to read from files with
  * non-standard file extensions (will get warining; not recommended).
  *
  */
object FileLoader {

  /** List of valid loaders available in the system (from /resources/META-INF/services/nounou.io.FileLoader)
    */
  private lazy val loaders = ServiceLoader.load(classOf[FileLoader]).iterator.asScala
  private val possibleLoaderBuffer = new mutable.HashMap[String, FileLoader]()

  final def load(fileName: String): Array[NNElement] = {

    val fileExtension = nounou.util.getFileExtension(fileName)

    val loader = possibleLoaderBuffer.get(fileExtension) match {

      //If the loader for this extension has already been loaded
      //This includes the case where no real loader was found for a given extension, and FileLoaderNull was loaded as a marker
      case l: Some[FileLoader] => l.get

      //If the given extension has not been tested yet, it will be searched for within the available loaders
      case _ => {
        val possibleLoaders: Iterator[FileLoader] = loaders.filter( _.canLoadExtension(fileExtension))
        val possibleLoader = if( possibleLoaders.hasNext ){
          val tempret = possibleLoaders.next
          if( possibleLoaders.hasNext ) {
            logger.info(s"Multiple possible loaders for file $fileName found. Will take first instance, ${tempret.getClass.getName}")
          }
          tempret
        } else {
          throw loggerError(s"Cannot find loader for file: $fileName")
        }
        possibleLoaderBuffer.+=( (fileExtension, possibleLoader) )
        possibleLoader
      }
    }
    loader.load(fileName)
  }

  final def load(fileNames: Array[String]): Array[NNElement] = {

    var tempElements = fileNames.flatMap( load(_) ).toVector

    //filters out NNDataChannel objects and joins them into one NNData if they are compatible
    val tempElementsNNDC = tempElements.filter(_.isInstanceOf[NNDataChannel])
    if( tempElementsNNDC.length > 1 ){
      if( tempElementsNNDC(0).isCompatible(tempElementsNNDC.tail) ) {
        tempElements = tempElements.filter(!_.isInstanceOf[NNDataChannel]).+:(
          new NNDataChannelArray(tempElementsNNDC.map(_.asInstanceOf[NNDataChannel]))
        )} else {

        //ToDo 3: allow multiple unfused NNDataChannel instances
        loggerError("multiple files containing data channels were not compatible with each other!")
      }
    }

    tempElements.toArray

  }

}

/** This trait marks individual file adapter classes as being able to handle
  * the loading of certain file extensions. FileLoader classes should be
  * registered to the JVM with entries in
  * "/resources/META-INF/services/nounou.io.FileLoader".
  *
  * It is implemented as a trait (and not an abstract class),
  * since some FileLoader classes will also be FileSaver classes, and
  * double inheritance of classes is not possible in Scala.
  *
  * The interface is subject to slight changes in the near future due to
  * handling of files which output multiple [[NNElement]] objects.
  * Some of these objects need to be integrated into a bigger object,
  * this will be written in to the trait.
 */
trait FileLoader extends LoggingExt {

  /**'''__MUST OVERRIDE__''' A list of __lower-case__ extensions which can be loaded.*/
  val canLoadExtensions: Array[String]
  /**Whether the given file can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadFile(file: File): Boolean = canLoadFile( file.getName )
  /**Whether the given file can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadFile(fileName: String): Boolean = canLoadExtension( nounou.util.getFileExtension(fileName) )
  /**Whether the given extension can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadExtension(extension: String): Boolean = canLoadExtensions.contains( extension.toLowerCase )

  /**'''__MUST OVERRIDE__''' Actual loading of file.*/
  def load(file: File): Array[NNElement]
  /**Actual loading of file.*/
  final def load(fileName: String): Array[NNElement] = load( new File(fileName) )

}

/** This [[FileLoader]] instance serves as a placeholder in the loader list
  * for extensions which have already been
  * searched for in the META-INF and do not exist.
  */
final class FileLoaderNull extends FileLoader{

  override val canLoadExtensions: Array[String] = Array[String]()

  /** Actual loading of file. */
  override def load(file: File): Array[NNElement] = {
    throw loggerError(s"The file ${file} has no valid loader yet.")
  }

}