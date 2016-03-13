package nounou.io.neuralynx.fileAdapters

import java.io.File
import nounou.elements.NNElement
import nounou.elements.data.{NNData, NNDataChannel}
import nounou.io.neuralynx.NNChannelFileReadNCS
import nounou.io.{FileLoader, FileSaver}
import nounou.util.LoggingExt


class FileAdapterNCS  extends FileLoader with FileSaver with LoggingExt {

  override val canLoadExtensions = Array("ncs")
  override def loadImpl(file: File): Array[NNElement] = Array(new NNChannelFileReadNCS(file))

  override val canSaveExtensions = Array("ncs")
  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
    data.forall( _ match {
                            case x: NNData => true
                            case x: NNDataChannel => true
                            case _ => false
                          }
    )

  /** Actual saving of file.
    *
    * @param fileName if the filename does not end with the correct extension, it will be appended. If it exists, it will be given a postscript.
    */
  override def saveImpl(fileName: String, data: Array[NNElement]): Unit = {
    ??? //ToDo: if NNData, dissect into NNDataChannels and separate filenames
  }

  def saveImpl(fileName: String, data: NNDataChannel): Unit = {
    ???
//    val headerText =
//      "## One channel of continuous data output to a Neuralynx NCS file\n" +
//        data.toStringFull().split("\n").map("## " + _ ).mkString("\n")
//    val handle = new RandomAccessFile( new File(fileName), "w")(ByteConverterLittleEndian)
//    handle.writeUInt8( headerText.toArray.map(_.toShort) )
//
//    val channelNumber = data match {
//      case d: NNDataChannelNumbered => d.channelNumber
//      case _ => 0
//    }
//
//    for( seg <- 0 until data.timing.segmentCount) {
//
//      val segLength = data.timing.segmentLength(seg)
//      val pages = segLength / recordNCSSampleCount
//      if (pages * recordNCSSampleCount != segLength) {
//        logger.warn(s"Segment $seg has a sample count which is not a multiple of the page length $recordNCSSampleCount. " +
//          s"The final ${segLength - pages * recordNCSSampleCount} samples will be truncated for writing.")
//      }
//
//      for (page <- 0 until pages) {
//        handle.writeUInt64( ULong.fromBigInt( data.timing.segmentStartTss(seg) ) )
//        handle.writeUInt32(channelNumber)
//        handle.writeUInt32(data.timing.sampleRate.toInt)
//        handle.writeUInt32(recordNCSSampleCount)
//        handle.writeInt16(data.readTrace(
//          new SampleRange(page * recordNCSSampleCount, page * recordNCSSampleCount + 511, 1, seg))
//          .map((v: Int) => (v.toDouble / data.scale.xBitsD).toShort)
//        )
//      }
//    }
//    handle.close()
  }


}

object FileAdapterNCS {

  val instance = new FileAdapterNCS

  def load( file: String ): Array[NNElement] = instance.load(file)
  def save( fileName: String, data: Array[NNElement]): Unit = instance.save(fileName,data)

}


//    nlxHeaderLoad()

//    var rec = 0
//    var dwChannelNum0: Long = 0
//    def readNCSRecordHeaderCheckAndReturnTS() = {
//      val returnTS = fileHandle.readUInt64Shifted()
//
//      //dwChannelNumber... must advance by 4 bytes anyway
//      if(rec == 0) {
//        //for the first record, just read, no checks
//        dwChannelNum0 = fileHandle.readUInt32
//      } else {
//        val dwChannelNum = fileHandle.readUInt32
//        loggerRequire(dwChannelNum0 == dwChannelNum,
//          s"Cannot read *.ncs files with multiple recording channels ($dwChannelNum, $dwChannelNum0) yet!")
//      }
//      //fHand.jumpBytes(4)
//
//      //dwSampleFreq
//      val dwSampleFreq = fileHandle.readUInt32.toDouble
//      require(dwSampleFreq == sampleRate,
//        s"Reported sampling frequency $dwSampleFreq for rec $rec is different from header $sampleRate)"
//      )
//
//      //dwNumValidSamples
//      val dwNumValidSamples = fileHandle.readUInt32
//      require(dwNumValidSamples == recordSampleCount,
//        s"Currently can only deal with records which are $recordSampleCount samples long, $dwNumValidSamples is error in rec $rec.")
//
//      returnTS
//    }
//