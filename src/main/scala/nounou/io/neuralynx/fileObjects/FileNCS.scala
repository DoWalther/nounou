package nounou.io.neuralynx.fileObjects

import java.io.File

/**This class is completely deprecated.
  *All functions of this class have been transferred to [[nounou.io.neuralynx.NNDataChannelFileNCS]],
  *as this data object class also encapsulates a single NCS file.
  *
  * Created by ktakagaki on 15/05/28.
  */
@deprecated
class FileNCS(file: File) /*extends FileNeuralynx(file)*/  {
//
//  /**Number of bytes per record in NCS files*/
//  override final val recordBytes = 1044
//
//  /**Number of samples per record in NCS files*/
//  final val recordNCSSampleCount= 512
//  /**Size of non-data bytes at head of each record in NCS files*/
//  final val recordNonNCSSampleHead = recordBytes - recordNCSSampleCount * 2
//
//  final def recordIndexStartByte(record: Int, index: Int) = {
//    recordStartByte(record) + 20L + (index * 2)
//  }
//
//  def cumulativeFrameToRecordIndex(cumFrame: Int) = {
//    ( cumFrame / recordNCSSampleCount, cumFrame % recordNCSSampleCount)
//  }
//
//  // <editor-fold defaultstate="collapsed" desc=" header reading ">
//
//  require(headerRecordSize == recordBytes, s"NCS file with non-standard record size: $headerRecordSize")
//  require(headerSampleRate >= 1000d, s"NCS file with non-standard sampling frequency: $headerSampleRate")
//
//  // </editor-fold>
//
//  /** Standard timestamp increment for contiguous records, depends on sample rate from header. */
//  lazy val headerRecordTSIncrement = (1000000D * recordBytes.toDouble / headerSampleRate).toLong
//
////    override def isValid(): Boolean = {
////      super.isValid() && (headerRecordType == "CSC")
////    }

}
