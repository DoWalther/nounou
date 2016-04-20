package nounou.io.neuralynx.fileAdapters

import java.io.{IOException, File}
import nounou.elements.NNElement
import nounou.elements.traits.NNTiming
import nounou.elements.spikes.{NNSpike, NNSpikesParent, NNSpikes}
import nounou.io.neuralynx.fileObjects.{FileNeuralynxSpikeInfo, FileWriteNeuralynx, FileReadNeuralynxSpike}
import nounou.io.neuralynx.headers.NNHeaderNeuralynxSpikeConcrete
import nounou.io.neuralynx.{NNSpikeNeuralynx, NNSpikesNeuralynx, NNScalingNeuralynx}
import nounou.io.{FileSaver, FileLoader}
import nounou.util.LoggingExt
import spire.math.ULong

/**
  * Adapter for saving and loading of Neuralynx spike files (NSE, NST, NTT).
  *
  * Created by ktakagaki on 16/02/20.
  */
class FileAdapterNeuralynxSpikes extends FileLoader with FileSaver with LoggingExt {

  override val canLoadExtensions: Array[String] = Array("nse", "nst", "ntt")
  override val canSaveExtensions: Array[String] = Array("nse", "nst", "ntt")
  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
    if(data.length == 1){
      data(0) match {
        case x: NNSpikesNeuralynx => true
        case x: NNSpikes => true
        case _ => false
      }
    } else {
      logger.error("Call with an array of one NNSpikes object. Merge if you would like to write two or more NNEvent objects to the same file.")
      false
    }


  /**Empty 8-element array to write to spike features as placeholder.*/
  protected final val empty8: Array[Long] = Array.tabulate[Long](8)( (i: Int) => 0 )

  override def loadImpl(file: File): Array[NNElement] = {

    val fileAdapter = new FileReadNeuralynxSpike(file)
    val fHand = fileAdapter.handle
    fHand.seek( fileAdapter.headerBytes )
    val tempChannelCount =fileAdapter.header.getHeaderNumADChannels

    //unit also specified in NNDataChannelFileReadNCS
    val tempScaling = new NNScalingNeuralynx( unit = "µV",
      absolutePerShort = 1.0E6 * fileAdapter.header().getHeaderADBitVolts
    )
    val tempTiming = new NNTiming( sampleRate = fileAdapter.header.getHeaderSamplingFrequency,
      _segmentLengths = Array[Int](),
      _segmentStartTss = Array[BigInt](),
      filterDelay = 0)

    val tempReturn = new NNSpikesNeuralynx(
      alignmentPoint = fileAdapter.header.getHeaderAlignmentPt,
      scaling = tempScaling,
      timing = tempTiming )

    var tempQwTimestamp: BigInt = 0 //Temporary number


    var break = try {
      tempQwTimestamp = fHand.readUInt64().toBigInt
      false
    } catch {
      case ioe: IOException => true
      case _: Throwable => true
    }

    while(!break){

      //Cheetah timestamp for this record. This value is in microseconds.
      val readQwTimeStamp: BigInt = tempQwTimestamp
      //The spike acquisition entity number for this record. This is NOT the A/D channel number.
      val readDwScNumber: Long = fHand.readUInt32()
      //The classified cell number for this record. If no cells have been classified, this number will be zero.
      val readDwCellNumber: Long = fHand.readUInt32()
      //The classified cell number for this record. If no cells have been classified, this number will be zero.
      val readDnParams: Array[Long] = fHand.readUInt32(8)
      //The classified cell number for this record. If no cells have been classified, this number will be zero.
      val readSnData: Array[Short] = fHand.readInt16(32*tempChannelCount)
      val snDataTransposed = {
        if(tempChannelCount==1) readSnData
        else{
          (for(channel <- 0 until tempChannelCount; waveform <- 0 until 32)
            yield readSnData(waveform*tempChannelCount +  channel)).toArray
        }
      }

      tempReturn.add(
        new NNSpikeNeuralynx(
            readQwTimeStamp, //qwTimeStamp
            readDwScNumber, //dwScNumber
            readDwCellNumber, //dwCellNumber
            readDnParams.toVector, //dnParams
            tempScaling.convertShortToAbsolute(snDataTransposed).toVector, //snData
            fileAdapter.header.getHeaderNumADChannels //channels
           )
      )

      break = try {
        tempQwTimestamp = fHand.readUInt64().toBigInt
        false
      } catch {
        case ioe: IOException => true
        case _: Throwable => true
      }

    }

    // </editor-fold>

    logger.info( "FileAdapterNeuralynx: loaded {} ", tempReturn)
    Array[NNElement](tempReturn)

  }


  override def saveImpl(fileName: String, data: Array[NNElement]): Unit = {

    //from  canSaveObjectArray(data) == true, one can assume that data has one element, which is an NNSpikes object.
    val dataElem: NNSpikesNeuralynx = data(0) match  {
      case x: NNSpikesNeuralynx => x
      case x: NNSpikes => NNSpikesNeuralynx.convertNNSpikesToNNSpikesNeuralynx(x)
    }

    val trodeCount = dataElem.channels()
    // tempTimestampOffset: BigInt = - (dataElem.timing.factorTsPerFr * dataElem.alignmentPoint).toInt
    //logger.info(s"writing spikes with timestamp offset $tempTimestampOffset to coorect for alignmentPoint=${dataElem.alignmentPoint}")

    loggerRequire(
      trodeCount == 1 || trodeCount == 2 || trodeCount == 4 ,
      s"Cannot write spikes with ${trodeCount} (!= 1/2/4) channels to ${nounou.util.getFileExtension(fileName)} file."
    )
    loggerRequire(
      trodeCount == FileNeuralynxSpikeInfo.getTrodeCount(fileName),
      s"Cannot write spikes with ${trodeCount} to ${nounou.util.getFileExtension(fileName)} file."
    )

    //Create a header de novo to write to file
    val header =
    /*      dataElem.oldHeader match { case x: NNHeaderNSE => x case null => */
    {
      val absMax = dataElem.readSpikeAbsoluteMaximumValue()
      var inputRange = ( absMax / 500d).ceil.toInt * 500
      if(inputRange > 5000) {
        inputRange = 5000
        logger.info(s"largest spike abs max $absMax is bigger than 5000---input range will be set to 5000")
      } else {
        logger.debug(s"spike input range set to $inputRange based on abs max of $absMax")
      }

      new NNHeaderNeuralynxSpikeConcrete(
        headerSamplingFrequency = dataElem.timing.sampleRate,
        headerWaveformLength = dataElem.singleWaveformLength(),
        headerAlignmentPt = dataElem.alignmentPoint,
        headerNumADChannels = trodeCount,

        headerInputRange = inputRange,
        headerDspDelayCompensation = true, //if(dataElem.timing.filterDelay == 0) false else true,
        headerDspFilterDelay = dataElem.timing.filterDelay


      )
    }/*
      case _ => throw loggerError("NNSpikeNeuralynx object does not have a valid NNHeaderNSE object!")
    }*/

    //Create a scale for converting double values to Int16
    val saveScale = new NNScalingNeuralynx(
      dataElem.scaling.unit,
      header.getHeaderADBitVolts*1e6 *{ if(header.getHeaderInputInverted) -1d else 1d  }
    )

    //Note: all header information must be initialized before the FileWrite is constructed,
    //since header is written within default constructor of file adapter
    val fileAdapter = new FileWriteNeuralynx(new File(fileName), header, FileNeuralynxSpikeInfo.getRecordSize(fileName))

    val fHand = fileAdapter.handle
    fHand.seek(fileAdapter.headerBytes)

    for( spike <- dataElem.iterator()){
      //qwTimeStamp
      fHand.writeUInt64( ULong.fromBigInt( spike.timestamp /*+ tempTimestampOffset*/ ) )
      //dwScNumber
      fHand.writeUInt32(0)
      //dwCellNumber
      fHand.writeUInt32( spike.unitNo )
      //dnParams
      fHand.writeUInt32(empty8)
      //snData
      loggerRequire(
        spike.waveform.length == trodeCount * 32,
        s"Waveform must be ${trodeCount * 32} elements long, not ${spike.waveform.length}"
      )
      fHand.writeInt16( saveScale.convertAbsoluteToShort( spike.readWaveformFlat(false) ) )
    }

    //ToDo 4: The following is workaround for file being too long, seeks past EOF at some point in header code????
    fHand.setLength( fHand.getFilePointer )
    fHand.close()
  }

}
