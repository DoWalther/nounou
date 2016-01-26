package nounou.io

import java.util.ServiceLoader
//import scala.collection.JavaConverters._

import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/03/24.
 */
class FileLoaderTest extends FunSuite {

  test("reading META-INF/services") {

    val loader = ServiceLoader.load(classOf[FileLoader]).iterator

    assert(loader.hasNext, "FileLoaders must be accessible!")
    val loaderNCS=loader.next()
    assert(loaderNCS.getClass.getName == "nounou.io.neuralynx.fileAdapters.FileAdapterNCS")
    assert(loader.hasNext, "FileLoaders must be accessible!")
    val loaderNSE=loader.next()
    assert(loaderNSE.getClass.getName == "nounou.io.neuralynx.fileAdapters.FileAdapterNSE")
    assert(loader.hasNext, "FileLoaders must be accessible!")
    val loaderNEV=loader.next()
    assert(loaderNEV.getClass.getName == "nounou.io.neuralynx.fileAdapters.FileAdapterNEV")

    assert( loaderNCS.canLoad("xxxx.ncs"))
    assert( loaderNCS.canLoad("/nounou/Neuralynx/t130911/Tet4a.ncs"))

    assert( !loaderNCS.canLoad("xxxx.nse"))
    assert( loaderNSE.canLoad("Y.nse"))
    assert( loaderNEV.canLoad("z.nev"))

  }

}
