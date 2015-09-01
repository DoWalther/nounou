package nounou.io.neuralynx

import java.io.File

import breeze.io.{RandomAccessFile, ByteConverterLittleEndian}
import breeze.linalg.{DenseVector => DV, convert}
import nounou.elements.{NNDataScale, NNDataTiming}
import nounou.elements.data.{NNDataChannelNumbered, NNDataChannel}
import nounou.elements.ranges.SampleRangeValid


/** A specialized immutable [[nounou.elements.data.NNDataChannel]] for NCS files.
  * This class encapsulates the file handle, and can load data from file dynamically on request.
  */
class NNDataChannelNCS(override val file: File) extends NNDataChannel with FileNCS with NNDataChannelNumbered {

  override def toStringFullImplParams() = super.toStringFullImplParams() + s"file=${file.getCanonicalPath}, "

  //ToDo update this
  override val channelName = file.getCanonicalFile.toString
  override var channelNumber = -1

  def NNDataChannelNCS(fileName: String) = new NNDataChannelNCS( new File(fileName) )

  final val xBits = 1024
  final lazy val xBitsD = xBits.toDouble
  final val absOffset = 0D
  final val absUnit: String = "microV"
  //val absGain = ???
  //private val t = FileAdapterNCS.instance

  @transient private var currentRecord = 0

  // <editor-fold defaultstate="collapsed" desc=" read record timestamp and check other variables ">

  @transient
  private var dwChannelNum: Long = -1
  private def readNCSRecordHeaderTS(record: Int): BigInt = {
    //println( s"seek: ${recordStartByte(record)}")
    handle.seek( recordStartByte(record) )
    val qwTimestamp = handle.readUInt64()

    //dwChannelNumber... must advance by 4 bytes anyway
    val tempDwChannelNum = handle.readUInt32
    //for the first record, just read, no checks
    if(dwChannelNum == -1) { dwChannelNum = tempDwChannelNum }
    else {
      loggerRequire(dwChannelNum == tempDwChannelNum,
        s"Cannot read *.ncs files with multiple recording channels ($dwChannelNum, $tempDwChannelNum) yet!")
    }
    //fHand.jumpBytes(4)

    //dwSampleFreq
    val dwSampleFreq = handle.readUInt32.toDouble
    require(dwSampleFreq == headerSampleRate,
      s"Reported sampling frequency $dwSampleFreq for rec $record is different from header $headerSampleRate)"
    )

    //dwNumValidSamples
    val dwNumValidSamples = handle.readUInt32
    require(dwNumValidSamples == recordNCSSampleCount,
      s"Currently can only deal with records which are $recordNCSSampleCount samples long, $dwNumValidSamples is error in rec $currentRecord.")

    qwTimestamp
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" Loop through the file and process record start timestamps ">

  //First record dealt with separately
  currentRecord = 0
  @transient private var thisRecTS = readNCSRecordHeaderTS(currentRecord)
  @transient private var lastRecTS = thisRecTS
  @transient private var tempStartTimestamps = Vector[BigInt]( lastRecTS )
  @transient private var tempLengths = Vector[Int]()
  @transient private var tempSegmentStartCumulativeFrame = 0

  //Read loop
  currentRecord = 1 //already dealt with rec=0
  while(currentRecord < headerRecordCount){
    thisRecTS = readNCSRecordHeaderTS(currentRecord)
    //ToDo 3: Implement cases where timestamps skip just a slight amount d/t DAQ problems

    loggerRequire( thisRecTS > lastRecTS,
      s"timestamp in record $currentRecord has gone back in time $lastRecTS -> $thisRecTS ")

    if(thisRecTS > lastRecTS + headerRecordTSIncrement){
    //new segment!

      if( thisRecTS - lastRecTS > 86400000000L ){
        logger.warn(s"timestamp in record $currentRecord has jumped more than 24 hours: " +
          s"$lastRecTS -> $thisRecTS ")
      }

      //Append timestamp for record rec as a new segment start
      tempStartTimestamps = tempStartTimestamps :+ thisRecTS
      //Append length of previous segment as segment length
      tempLengths = tempLengths :+ (currentRecord*512 - tempSegmentStartCumulativeFrame)
      //New segment's start frame
      tempSegmentStartCumulativeFrame = currentRecord*512
    }//else  advanced correctly within segment

    //reset marker for lastTS
    lastRecTS = thisRecTS
    currentRecord += 1 //this will cause break in while if on lastValid record
  }
  //Last record cleanup: Append length of previous segment as segment length
  tempLengths = tempLengths :+ (headerRecordCount*512 - tempSegmentStartCumulativeFrame)

  // </editor-fold>

  setTiming( new NNDataTiming(headerSampleRate,
                              tempLengths.toArray,
                              tempStartTimestamps.toArray)
  )
  setScale( new NNDataScale(Short.MinValue.toInt*xBits, Short.MaxValue.toInt*xBits,
                            absGain = 1.0E6 * headerADBitVolts / xBitsD,
                            absOffset = this.absOffset,
                            absUnit = this.absUnit)
  )

  logger.info( "loaded {}", this )

  // <editor-fold defaultstate="collapsed" desc=" data implementations ">

  override def readPointImpl(frame: Int, segment: Int): Int = {
    val (record, index) = cumulativeFrameToRecordIndex( timing.cumulativeFrame(frame, segment) )
    handle.seek( recordIndexStartByte( record, index ) )
    handle.readInt16 * scale.xBits
  }

  override def readTraceDVImpl(range: SampleRangeValid): DV[Int] = {
    //println("XDataChannelNCS " + range.toString())
    var (currentRecord: Int, currentIndex: Int) =
      cumulativeFrameToRecordIndex( timing.cumulativeFrame(range.start, range.segment) )
    val (endReadRecord: Int, endReadIndex: Int) =
      cumulativeFrameToRecordIndex( timing.cumulativeFrame(range.last, range.segment) )
      //range is inclusive of lastValid

    //println( "curr " + (currentRecord, currentIndex).toString )
    //println( "end " + (endReadRecord, endReadIndex).toString )
    //ToDo1 program step
    //val step = range.step

    val tempRet = DV.zeros[Int](range.last-range.start+1)//range.length)//DV[Int]()
    var currentTempRetPos = 0

    handle.seek( recordIndexStartByte(currentRecord, currentIndex) )

    if(currentRecord == endReadRecord){
      //if the whole requested trace fits in one record
      val writeLen = (endReadIndex - currentIndex) + 1
      val writeEnd = currentTempRetPos + writeLen
      //      println( "writeLen " + writeLen.toString + " writeEnd " + writeEnd.toString )
      //ToDo 3: improve breeze dv requirement documentation
      tempRet(currentTempRetPos until writeEnd ) := convert( DV(handle.readInt16(writeLen)), Int)  * scale.xBits
      currentTempRetPos = writeEnd
    } else {
      //if the requested trace spans multiple records

      //read data contained in first record
      var writeEnd = currentTempRetPos + (512 - currentIndex)
      tempRet(currentTempRetPos until writeEnd ) := convert( DV(handle.readInt16(512 - currentIndex)), Int)  * scale.xBits
      currentRecord += 1
      currentTempRetPos = writeEnd
      handle.jumpBytes(recordNonNCSSampleHead)

      //read data from subsequent records, excluding lastValid record
      while (currentRecord < endReadRecord) {
        writeEnd = currentTempRetPos + 512
        tempRet(currentTempRetPos until writeEnd ) :=
          convert( DV(handle.readInt16(512 /*- currentIndex*/)), Int) * scale.xBits
        currentRecord += 1
        currentTempRetPos = writeEnd
        handle.jumpBytes(recordNonNCSSampleHead)
      }

      //read data contained in lastValid record
      writeEnd = currentTempRetPos + endReadIndex + 1
      tempRet(currentTempRetPos until writeEnd ) :=
        convert( DV(handle.readInt16(endReadIndex + 1)), Int) * scale.xBits

    }

    tempRet( 0 until tempRet.length by range.step )

  }

  // </editor-fold>


}
