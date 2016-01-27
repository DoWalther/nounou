package nounou.io.neuralynx.fileAdapters

import java.io.{IOException, File}

import nounou.elements.NNElement
import nounou.elements.events.NNEvents
import nounou.elements.spikes.NNSpikes
import nounou.elements.traits.NNTiming
import nounou.io.{FileSaver, FileLoader}
import nounou.io.neuralynx.fileObjects.{FileWriteNSE, FileNSE, FileWriteNEV, FileReadNSE}
import nounou.io.neuralynx._
import nounou.io.neuralynx.headers.{NNHeaderNSEConcrete, NNHeaderNSE, NNHeaderNEV}
import nounou.util.LoggingExt
import spire.math.ULong

import scala.collection.mutable

/**
  * Encapsulates header information and serialization to text for Neuralynx NEV file headers.
 */
class FileAdapterNSE extends FileLoader with FileSaver with LoggingExt {

  override val canLoadExtensions: Array[String] = Array("nse")
  override val canSaveExtensions: Array[String] = Array("nse")
  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
    if(data.length == 1){
      data(0) match {
        case x: NNSpikesNeuralynx => true
        case x: NNSpikes => true
        case _ => false
      }
    } else {
      logger.error("Call with an array of one NNSpike. Merge if you would like to write two or more NNEvent objects to the same file.")
      false
    }

  // <editor-fold defaultstate="collapsed" desc=" loading code ">

  override def load(file: File): Array[NNElement] = {

    val fileAdapter = new FileReadNSE(file)
    val fHand = fileAdapter.handle
    fHand.seek( fileAdapter.headerBytes )

    //unit also specified in NNDataChannelFileReadNCS
    val tempScaling = new NNScalingNeuralynx( unit = "µV",
                                              absolutePerShort = 1.0E6 * fileAdapter.header().getHeaderADBitVolts
    )
    val tempTiming = new NNTiming( sampleRate = fileAdapter.header.getHeaderSamplingFrequency,
                                   _segmentLengths = Array[Int](),
                                   _segmentStartTss = Array[BigInt](),
                                   filterDelay = 0)

    val tempReturn = new NNSpikesNeuralynx( alignmentPoint = fileAdapter.header.getHeaderAlignmentPt,
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
      val qwTimeStamp: BigInt = tempQwTimestamp
      //The spike acquisition entity number for this record. This is NOT the A/D channel number.
      val dwScNumber: Long = fHand.readUInt32()
      //The classified cell number for this record. If no cells have been classified, this number will be zero.
      val dwCellNumber: Long = fHand.readUInt32()
      //The classified cell number for this record. If no cells have been classified, this number will be zero.
      val dnParams: Array[Long] = fHand.readUInt32(8)
      //The classified cell number for this record. If no cells have been classified, this number will be zero.
      val snData: Array[Short] = fHand.readInt16(32)

      tempReturn.add(
        new NNSpikeNSE( qwTimeStamp, dwScNumber, dwCellNumber,
                        dnParams.toVector,
                        tempScaling.convertShortToAbsolute(snData).toVector) )

      break = try {
        tempQwTimestamp = fHand.readUInt64().toBigInt
        false
      } catch {
        case ioe: IOException => true
        case _: Throwable => true
      }

    }

    // </editor-fold>

    logger.info( "FileAdapterNSE: loaded {} ", tempReturn)
    Array[NNElement](tempReturn)

  }


  override def saveImpl(fileName: String, data: Array[NNElement]): Unit = {
    //from  canSaveObjectArray(data) == true, one can assume that data has one element, which is an NNEvent object.
    val dataElem: NNSpikesNeuralynx = data(0).asInstanceOf[NNSpikesNeuralynx]
    loggerRequire( dataElem.channels == 1, s"Cannot write spikes with ${dataElem.channels} (!= 1) channels to NSE file.")

    val header =
      dataElem.oldHeader match {
      case x: NNHeaderNSE => x
      case null => new NNHeaderNSEConcrete(
        headerSamplingFrequency = dataElem.timing.sampleRate,
        headerInputRange = 1000, //ToDO 1: make real!
        headerWaveformLength = dataElem.singleWaveformLength(),
        headerAlignmentPt = dataElem.alignmentPoint
      )
      case _ => throw loggerError("NNSpikeNeuralynx object does not have a valid NNHeaderNSE object!")
    }

    val saveScale = new NNScalingNeuralynx(dataElem.scaling.unit, header.getHeaderADBitVolts*1e6)
    val fileAdapter = new FileWriteNSE(new File(fileName), header)
    val fHand = fileAdapter.handle
    fHand.seek(fileAdapter.headerBytes)

    val empty8: Array[Long] = Array.tabulate[Long](8)( (i: Int) => 0 )
    for( spike <- dataElem.iterator()) {
      //qwTimeStamp
      fHand.writeUInt64( ULong.fromBigInt( spike.timestamp ) )
      //dwScNumber
      fHand.writeUInt32(0)
      //dwCellNumber
      fHand.writeUInt32( spike.getUnitNo() )
      //dnParams
      fHand.writeUInt32(empty8)
      //snData
      fHand.writeInt16( saveScale.convertAbsoluteToShort( spike.getWaveform() ) )
    }

    fHand.close()
  }

}
