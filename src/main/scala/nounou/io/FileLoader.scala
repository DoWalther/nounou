package nounou.io

import java.io.File
import java.util.ServiceLoader

import nounou.elements.data.{NNDataChannelArray, NNDataChannel}
import nounou.util.LoggingExt
import scala.collection.JavaConverters._
import nounou.elements.NNElement
import scala.collection.mutable

/**This trait marks individual file adapter classes as being able to handle
  * the loading of certain file extensions.
  *
 * Created by ktakagaki on 15/03/24.
 */
trait FileLoader extends LoggingExt {

//  /**Factory method returning single instance.*/
//  def create(): FileLoader

  /** A list of lower-case extensions which can be loaded.*/
  val canLoadExtensions: Array[String]
  /**Whether the given file can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadFile(file: File): Boolean = canLoadFile( file.getName )
  /**Whether the given file can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadFile(fileName: String): Boolean = canLoadExtension( nounou.util.getFileExtension(fileName) )
  /**Whether the given extension can be loaded. For now, based simply on a match with the file extension.*/
  final def canLoadExtension(extension: String): Boolean = canLoadExtensions.contains( extension.toLowerCase )
  /**Actual loading of file.*/
  def load(file: File): Array[NNElement]
  /**Actual loading of file.*/
  final def load(fileName: String): Array[NNElement] = load( new File(fileName) )

}

/** This [[FileLoader]] instance serves as a placeholder in the loader list
  * for extensions which have already been
  * searched for in the META-INF and do not exist.
  */
final class FileLoaderNone extends FileLoader{

  override val canLoadExtensions: Array[String] = Array[String]()

  /** Actual loading of file. */
  override def load(file: File): Array[NNElement] = {
    throw loggerError(s"The file ${file} has no valid loader yet.")
  }

}


///** This singleton FileLoader object is the main point of use for file loading.
//  * It maintains a list of available loaders in the system (from Meta-Inf)
//  * and uses the first valid loader to realize the [[FileLoader.load]] functions.
//   */
//object FileLoader extends LoggingExt {
//
//}