package nounou.io.neuralynx

import java.io.File

import nounou.elements.NNElement
import nounou.elements.data.{NNDataChannel, NNData}
import nounou.elements.spikes.NNSpikes
import nounou.io.{FileSaver, FileLoader}
import nounou.util.LoggingExt

/**
 * Created by ktakagaki on 15/08/18.
 */
class FileAdapterNSE extends FileLoader /*with FileSaver*/ with LoggingExt {

  override val canLoadExtensions: Array[String] = Array("nse")
  override def load(file: File): Array[NNElement] = {
    val ret = new NNSpikesNeuralynx(file)
    loggerRequire(ret.checkValidFile(), s"File ${file.getName} is not a valid NSE file!")


    ???
  }

//  override val canSaveExtensions = Array("nse")
//  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
//    data.forall( _ match {
//      case x: NNData => true
//      case x: NNDataChannel => true
//      case _ => false
//    }
//    )

}
