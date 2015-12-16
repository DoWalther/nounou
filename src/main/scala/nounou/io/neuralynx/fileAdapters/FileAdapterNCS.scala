package nounou.io.neuralynx.fileAdapters

import java.io.File

import breeze.linalg.{DenseVector => DV}
import nounou.elements.NNElement
import nounou.elements.data.{NNData, NNDataChannel}
import nounou.io.neuralynx.NNDataChannelFileReadNCS
import nounou.io.{FileLoader, FileSaver}
import nounou.util.LoggingExt

class FileAdapterNCS  extends FileLoader with FileSaver with LoggingExt {

  override val canLoadExtensions = Array("ncs")
  override def load(file: File): Array[NNElement] = Array(new NNDataChannelFileReadNCS(file))

  override val canSaveExtensions = Array("ncs")
  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
    data.forall( _ match {
                            case x: NNData => true
                            case x: NNDataChannel => true
                            case _ => false
                          }
    )

  /** Actual saving of file.
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

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" record structure ">


  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" load ">



//    fileHandle = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)

    // <editor-fold desc="parse the Neuralynx header">


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
//    // <editor-fold defaultstate="collapsed" desc=" Loop through the file and process record start timestamps ">
//
//    // <editor-fold defaultstate="collapsed" desc=" First record dealt with separately ">
//
//    fileHandle.seek( headerBytes )
//
//    ///qwTimeStamp
//    var thisRecTS = readNCSRecordHeaderCheckAndReturnTS()//fHand.readUInt64Shifted()
//    var lastRecTS = thisRecTS
//    var tempStartTimestamps = Vector[Long]( lastRecTS )
//      var tempLengths = Vector[Int]() //tempLengths defined with -1 at header for looping convenience, will be dropped later
//      var tempSegmentStartFrame = 0
//
////      //snSamples
////      fHand.jumpBytes(recordSampleCount*2)
//
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc=" read loop ">
//
//    rec = 1 //already dealt with rec=0
//    //var lastRecJump = 1
//    //var lastTriedJump = 4096
//    while(rec < tempNoRecords){
//
//      fileHandle.seek( recordStartByte(rec) )
//      //qwTimeStamp
//      thisRecTS = readNCSRecordHeaderCheckAndReturnTS()
//      //ToDo 3: Implement cases where timestamps skip just a slight amount d/t DAQ problems
//
//      if(thisRecTS > lastRecTS + tempRecTSIncrement/*lastRecJump=1*/){
//        //new segment!
//
//        //Append timestamp for record rec as a new segment start
//        tempStartTimestamps = tempStartTimestamps :+ thisRecTS
//        //Append length of previous segment as segment length
//        tempLengths = tempLengths :+ (rec*512 - tempSegmentStartFrame)
//        //New segment's start frame
//        tempSegmentStartFrame = rec*512
//
//      } else { } //advanced correctly within segment
//
//      //reset marker for lastTS
//      lastRecTS = thisRecTS
//
//      rec += 1 //this will cause break in while if on lastValid record
//
//    }

// <editor-fold defaultstate="collapsed" desc=" backup old while with skipping">
//
//    while(rec < tempNoRecords){
//
//        fHand.seek( recordStartByte(rec) )
//        //qwTimeStamp
//        thisRecTS = readNCSRecordHeaderCheckAndReturnTS()
//
//        //ToDo 3: Implement cases where timestamps skip just a slight amount d/t DAQ problems
//        if(thisRecTS > lastRecTS + tempRecTSIncrement*lastRecJump){
//
//          //jumped over too many records!
//          if( lastRecJump != 1 ){
//            //Went over change in segment, rewind and try with step of 1
//            rec = rec - lastRecJump + 1
//            fHand.seek( recordStartByte(rec) )
//            lastRecJump = 1
//
//            //qwTimeStamp
//            thisRecTS = fHand.readUInt64Shifted
//
//            if(thisRecTS > lastRecTS + tempRecTSIncrement/*lastRecJump*/){
//              //We got the correct start of a segment, with lastRecJump of 1!!!
//
//              //Append timestamp for record rec as a new segment start
//              tempStartTimestamps = tempStartTimestamps :+ thisRecTS
//              //Append length of previous segment as segment length
//              tempLengths = tempLengths :+ (rec*512 - tempSegmentStartFrame)
//              //New segment's start frame
//              tempSegmentStartFrame = rec*512
//
//              //reset next jump attempt count
//              lastTriedJump = 4096
//
//            } else {
//              //We went ahead by lastRecJump = 1, but the record was just one frame ahead in the same jump
//              if( lastTriedJump > 1 ){
//                //Jump less at next loop
//                lastTriedJump = lastTriedJump / 2
//              }
//            }
//
//          } else {
//            //lastRecJump = 1, we've found the start of a new segment
//
//            //Append timestamp for record rec as a new segment start
//            tempStartTimestamps = tempStartTimestamps :+ thisRecTS
//            //Append length of previous segment as segment length
//            tempLengths = tempLengths :+ (rec*512 - tempSegmentStartFrame)
//            //New segment's start frame
//            tempSegmentStartFrame = rec*512
//
//            //reset next jump attempt count
//            lastTriedJump = 4096
//
//          }
//
//        } //else { } //advanced correctly within segment
//
//        //reset marker for lastTS
//        lastRecTS = thisRecTS
//
//        // <editor-fold defaultstate="collapsed" desc=" VARIOUS CHECKS, NOT NECESSARY ">
//        //dwChannelNumber
//        fHand.jumpBytes(4)
//        //dwSampleFreq
//        val dwSampleFreq = fHand.readUInt32
//        require(dwSampleFreq == sampleRate,
//          s"Reported sampling frequency for record $rec, $dwSampleFreq, " +
//            s"is different from file sampling frequency $sampleRate )" )
//        //dwNumValidSamples
//        val dwNumValidSamples = fHand.readUInt32
//        require(dwNumValidSamples == recordSampleCount,
//          s"Currently can only deal with records which are $recordSampleCount samples long.")
//        // </editor-fold>
//
//        // <editor-fold defaultstate="collapsed" desc=" loop 'rec' advancement ">
//        if( rec == tempNoRecords -1 ){
//          //was on lastValid record
//          rec += 1 //this will cause break in while
//        } else if (rec + lastTriedJump < tempNoRecords ) {
//          //try the jump in lastTriedJump
//          lastRecJump = lastTriedJump
//          rec += lastRecJump
//        } else {
//          //jump to the end of the file
//          lastRecJump = tempNoRecords-1-rec
//          lastTriedJump = lastRecJump
//          rec += lastRecJump
//        }
//        // </editor-fold>
//
//
//      }
// </editor-fold>

//      //Last record cleanup: Append length of previous segment as segment length
//      tempLengths = tempLengths :+ (tempNoRecords*512 - tempSegmentStartFrame)

// </editor-fold>

    //println("tempADBitVolts " + tempADBitVolts)

//    val nnDataChannelNCS = new NNDataChannelNCS(
//                  fileHandle = fileHandle,
//                  new NNDataTiming(sampleRate, tempLengths.toArray,
//                      tempStartTimestamps.toArray, BigInt(9223372036854775807L)+1),
//                  NNDataScale.apply(Short.MinValue.toInt*xBits, Short.MaxValue.toInt*xBits,
//                          absGain = 1.0E6 * tempADBitVolts / xBitsD,
//                          absOffset = 0d,
//                          absUnit = "microV"),
//                  channelName = tempAcqEntName)
//    //println("absGain " + xDataChannelNCS.scale.absGain)
//    logger.info( "loaded {}", nnDataChannelNCS )
//    Array[NNElement]( nnDataChannelNCS )

//  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" save ">


  // </editor-fold>

//  /** Factory method returning single instance. */
//  override def create(): FileLoader = FileAdapterNCS.instance
//}




