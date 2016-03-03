package nounou.io.neuralynx

import java.io.File

import breeze.linalg.{DenseVector => DV, convert}
import nounou.elements.data.traits.NNTiming
import nounou.elements.traits.NNTiming
import nounou.io.neuralynx.fileObjects.{FileReadNCS, FileReadNeuralynx}
import nounou.io.neuralynx.headers.NNHeaderNCS
import nounou.ranges.NNRangeValid


/**
  * A specialized immutable [[nounou.elements.data.NNDataChannel]] for NCS files.
  * This class encapsulates a reference to the file handle,
  * and can load data from file dynamically on request.
  *
  */
class NNDataChannelFileReadNCS(override val file: File)  extends FileReadNCS( file ) {

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl() = super.toStringImpl() + s", file=${file.getCanonicalPath}"

  // </editor-fold>

  /**Number of samples per record in NCS files*/
  final val recordNCSSampleCount= 512
  /**Size of non-data bytes at head of each record in NCS files*/
  final val recordNonNCSSampleHead = recordSize - recordNCSSampleCount * 2

  final def recordIndexStartByte(record: Int, index: Int) = {
    recordStartByte(record) + 20L + (index * 2)
  }

  def cumulativeFrameToRecordIndex(cumFrame: Int) = {
    ( cumFrame / recordNCSSampleCount, cumFrame % recordNCSSampleCount)
  }


  /** Standard timestamp increment for contiguous records, depends on sample rate from header. */
  lazy val headerRecordTSIncrement = (1000000D * recordSize.toDouble / header.asInstanceOf[NNHeaderNCS].getHeaderSamplingFrequency).toLong

  //    override def isValid(): Boolean = {
  //      super.isValid() && (headerRecordType == "CSC")
  //    }

  // </editor-fold>

  //ToDo update these
  override val channelName = file.getCanonicalFile.toString
  override var channelNumber = -1

  def NNDataChannelNCS(fileName: String) = new NNDataChannelFileReadNCS( new File(fileName) )

  //final val xBits = 1024
  //final lazy val xBitsD = xBits.toDouble
  //final val absOffset = 0D

  //also specified in FileAdapterNSE
  final val absUnit: String = "µV"

  //val absGain = ???
  //private val t = FileAdapterNCS.instance

  @transient private var currentRecord = 0

  // <editor-fold defaultstate="collapsed" desc=" read record timestamp and check other variables ">

  @transient
  private var dwChannelNum: Long = -1
  private def readNCSRecordHeaderTS(record: Int): BigInt = {

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
    require(dwSampleFreq == header.getHeaderSamplingFrequency,
      s"Reported sampling frequency $dwSampleFreq for rec $record is different from header $header.headerSampleRate)"
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
  @transient
  private var thisRecTS = readNCSRecordHeaderTS(currentRecord)// - BigInt(header.getHeaderDspFilterDelay)
  @transient
  private var lastRecTS = thisRecTS
  @transient
  private var tempStartTimestamps = Vector[BigInt]( lastRecTS )
  @transient
  private var tempLengths = Vector[Int]()
  @transient
  private var tempSegmentStartCumulativeFrame = 0

  //Read loop
  currentRecord = 1 //already dealt with rec=0
  while(currentRecord < headerRecordCount){
    thisRecTS = readNCSRecordHeaderTS(currentRecord)// - BigInt(header.getHeaderDspFilterDelay)
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

  override val timing =
    new NNTiming( sampleRate = header.getHeaderSamplingFrequency,
                  _segmentLengths = tempLengths.toArray,
                  _segmentStartTss = tempStartTimestamps.toArray,
                  filterDelay = if(header.getHeaderDspDelayCompensation) 0 else header.getHeaderDspFilterDelay
    )

  override val scaling: NNScalingNeuralynx =

    new NNScalingNeuralynx( unit = this.absUnit,
                            absolutePerShort = 1.0E6 * header.getHeaderADBitVolts *
                                  {if(header.getHeaderInputInverted) -1d else 1d}
    )

  logger.info( "loaded {}", this )

  // <editor-fold defaultstate="collapsed" desc=" data implementations ">

  override def readPointImpl(frame: Int, segment: Int): Double = {
    val (record, index) = cumulativeFrameToRecordIndex( timing.cumulativeFrame(frame, segment) )
    handle.seek( recordIndexStartByte( record, index ) )
    scaling.convertShortToAbsolute( handle.readInt16 )
  }

  override def readTraceDVImpl(range: NNRangeValid): DV[Double] = {

    var (currentRecord: Int, currentIndex: Int) =
      cumulativeFrameToRecordIndex( timing.cumulativeFrame(range.start, range.segment) )

    val (endReadRecord: Int, endReadIndex: Int) =
      cumulativeFrameToRecordIndex( timing.cumulativeFrame(range.last, range.segment) )
      //range is inclusive of lastValid

    //ToDo1 program step
    //val step = range.step

    val tempRet = DV.zeros[Double](range.last-range.start+1)
    var currentTempRetPos = 0

    handle.seek( recordIndexStartByte(currentRecord, currentIndex) )

    if(currentRecord == endReadRecord){
      //if the whole requested trace fits in one record
      val writeLen = (endReadIndex - currentIndex) + 1
      val writeEnd = currentTempRetPos + writeLen

      //ToDo 3: improve breeze dv requirement documentation
      tempRet( 0 until writeEnd ) := DV( scaling.convertShortToAbsolute( handle.readInt16(writeLen) ) )
    } else {
      //if the requested trace spans multiple records

      //read data contained in first record
      var writeEnd = /*0 +*/ (512 - currentIndex)
      tempRet(0 until writeEnd ) := DV( scaling.convertShortToAbsolute( handle.readInt16(512 - currentIndex) ) )

      currentRecord += 1
      currentTempRetPos = writeEnd
      handle.jumpBytes(recordNonNCSSampleHead)

      //read data from subsequent records, excluding lastValid record
      while (currentRecord < endReadRecord) {
        writeEnd = currentTempRetPos + 512
        tempRet(currentTempRetPos until writeEnd ) :=
          DV( scaling.convertShortToAbsolute( handle.readInt16(512 /*- currentIndex*/) ) )
        currentRecord += 1
        currentTempRetPos = writeEnd
        handle.jumpBytes(recordNonNCSSampleHead)
      }

      //read data contained in lastValid record
      writeEnd = currentTempRetPos + endReadIndex + 1
      tempRet(currentTempRetPos until writeEnd ) :=
        DV( scaling.convertShortToAbsolute( handle.readInt16(endReadIndex + 1) ) )

    }

    //ToDo 3: eliminate this extra reading and downsampling (range.step != 1) after installing appropriate tests
    tempRet( 0 until tempRet.length by range.step )

  }

  // </editor-fold>


}
