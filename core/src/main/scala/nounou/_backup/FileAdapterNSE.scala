//package nounou.io.neuralynx.fileAdapters
//
//import java.io.{IOException, File}
//
//import nounou.elements.NNElement
//import nounou.elements.spikes.NNSpikes
//import nounou.io.neuralynx.headers.{NNHeaderNeuralynxSpikeConcrete}
//
///**
//  * Adapter for saving and loading of Neuralynx NSE files.
// */
//class FileAdapterNSE extends FileAdapterNeuralynxSpike {
//
////  override val canLoadExtensions: Array[String] = Array("nse")
////  override val canSaveExtensions: Array[String] = Array("nse")
////  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
////    if(data.length == 1){
////      data(0) match {
////        //case x: NNSpikesNeuralynx => true
////        case x: NNSpikes => true
////        case _ => false
////      }
////    } else {
////      logger.error("Call with an array of one NNSpike. Merge if you would like to write two or more NNEvent objects to the same file.")
////      false
////    }
//
////  override final val fileTypeName = "NSE"
////  override final val waveformLength = 32
////  override final val trodeCount: Int = 1
//
//  // <editor-fold defaultstate="collapsed" desc=" loading code ">
//
//
//  // </editor-fold>
//
//
//}
