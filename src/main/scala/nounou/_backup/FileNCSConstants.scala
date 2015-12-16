//package nounou.io.neuralynx.fileObjects
//
///**
//  * Created by ktakagaki on 15/11/24.
//  */
//trait FileNCSConstants extends FileNeuralynx {
//
////  /**Number of bytes per record in NCS files*/
////  override final val recordBytes = 1044
////
////  /**Number of samples per record in NCS files*/
////  final val recordNCSSampleCount= 512
////  /**Size of non-data bytes at head of each record in NCS files*/
////  final val recordNonNCSSampleHead = recordBytes - recordNCSSampleCount * 2
////
////  final def recordIndexStartByte(record: Int, index: Int) = {
////    recordStartByte(record) + 20L + (index * 2)
////  }
////
////  def cumulativeFrameToRecordIndex(cumFrame: Int) = {
////    ( cumFrame / recordNCSSampleCount, cumFrame % recordNCSSampleCount)
////  }
//
//}
