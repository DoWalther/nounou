package nounou.io.neuralynx.fileAdapters

import java.io.{File, IOException}
import nounou.elements.events.{NNEvent, NNEvents}
import nounou.elements.NNElement
import nounou.io.{FileLoader, FileSaver}
import nounou.io.neuralynx.NNEventNEV
import nounou.io.neuralynx.fileObjects.{FileNEVInfo, FileWriteNeuralynx, FileReadNEV}
import nounou.io.neuralynx.headers.NNHeaderNEV
import nounou.util.LoggingExt

import scala.collection.mutable

/**
  * Adapter for saving and loading of Neuralynx NEV files.
  */
class FileAdapterNEV extends FileLoader with FileSaver with LoggingExt {

  override val canLoadExtensions = Array("nev")

  override val canSaveExtensions = Array("nev")

  override def canSaveObjectArray(data: Array[NNElement]): Boolean =
    if(data.length == 1){
      data(0) match {
        case x: NNEvents => true
        case _ => false
      }
    } else {
      logger.error("Call with an array of one NNEvent. Merge if you would like to write two or more NNEvent objects to the same file.")
      false
    }

  override def loadImpl(file: File): Array[NNElement] = {

    val fileAdapter = new FileReadNEV(file)  //val fHand = new RandomAccessFile(file, "r")(ByteConverterLittleEndian)
    val fHand = fileAdapter.handle
    fHand.seek( fileAdapter.headerBytes )

    val eventMap = mutable.HashMap[BigInt, NNEvent]()

    // <editor-fold defaultstate="collapsed" desc=" read loop ">

    val xEventsReturn = new NNEvents()
    xEventsReturn.header = fileAdapter.header
    /**Temporary buffer of event trigger info (to encorporate into a duration event
      * if the next call on the same port is value zero). portValue -> (qwTimeStamp, ttlInt, eventString, id)
      */
    val eventStarts = new mutable.HashMap[Int, (BigInt, Int, String, Short)]()

    //ToDo 4: Consider looping until record count reached, instead of EOF, etc. (update in-loop repeat too)
    //Loop through file until cannot read any longer
    var break = try {
      //try reading first nstx (Reserved)
      fHand.readInt8()
      false
    } catch {
      case ioe: IOException => true
      case _: Throwable => true
    }

    while(!break){
      //skip nstx(reserved), npkt_id(ID for originating system), npkt_data_size(==2)
      //all of these values seem to be fixed at zero, at least for cheetah 5.5.1
      fHand.skipBytes(6-1)

      //Cheetah timestamp for this record. This value is in microseconds.
      val qwTimeStamp: BigInt = fHand.readUInt64().toBigInt
      val nevent_id = fHand.readInt16()   //fHand.skipBytes(2)
      //Decimal TTL value read from the TTL input port
      val nttl = fHand.readInt16()
      //skip ncrc, ndummy1, ndummy2, dnExtra(8x Int32)
      //all of these values seem to be fixed at zero, at least for cheetah 5.5.1
      fHand.skipBytes(38)//readInt16(3) //readInt32(8)
      //EventString
      val eventString = new String(fHand.readUInt8(128).filterNot(_ == 0).map(_.toChar))
      val portValue = NNEventNEV.toNEVPortValue(eventString)

      //Whether to close a previously started duration event, or to just mark a new event
      if(eventStarts.contains(portValue)){
        //if the port was already triggered before
        val prevTSCode = eventStarts(portValue)
        if(nttl == 0) {
          //if we have zero now, the previous trigger was a start, and this trigger is an end
          //log the previous trigger with the duration
          xEventsReturn.addEvent(portValue ->
            new NNEventNEV( timestamp = prevTSCode._1,
                            duration = qwTimeStamp - prevTSCode._1,
                            code = prevTSCode._2,
                            comment = prevTSCode._3,
                            id = prevTSCode._4)
          )
          //and delete the prior trigger entry
          eventStarts.-=(portValue)
        } else {
          //if we have a non-zero value again, the previous trigger was a 0 duration event
          //log the previous trigger as a 0 duration event
          xEventsReturn.addEvent(portValue ->
            new NNEventNEV( timestamp = prevTSCode._1,
                            duration = 0L,
                            code = prevTSCode._2,
                            comment = prevTSCode._3,
                            id = prevTSCode._4)
          )
          //and log(overwrite) a new start event
          eventStarts.+=( portValue -> (qwTimeStamp, nttl.toInt, eventString, nevent_id) )
        }
      } else {
        //if the port hasn't been triggered before...
        //if current value is zero, just write it
        if(nttl == 0) xEventsReturn.addEvent(portValue -> new NNEventNEV(qwTimeStamp, 0, nttl, eventString, nevent_id))
        //if current value is nonzero, wait until next
        else eventStarts.+=( portValue -> (qwTimeStamp, nttl.toInt, eventString, nevent_id) )
      }

      break = try {
        fHand.readInt8()
        false
      } catch {
        case ioe: IOException => true
        case _: Throwable => true
      }

    }

    //process remaining trigger events as zero duration events
    eventStarts.foreach(  (f: ((Int, (BigInt, Int, String, Short)))) => {
      xEventsReturn.addEvent(f._1 -> new NNEventNEV(f._2._1, 0L, f._2._2, f._2._3, f._2._4))
    })

    // </editor-fold>

    logger.info( "FileAdapterNEV: loaded {} ", xEventsReturn )
    Array[NNElement](xEventsReturn)

  }

  override def saveImpl(fileName: String, data: Array[NNElement]): Unit = {
    //from  canSaveObjectArray(data) == true, one can assume that data has one element, which is an NNEvent object.
    val dataElem = data(0).asInstanceOf[NNEvents]

    val header = dataElem.header match {
      case x: NNHeaderNEV => x
      case _ => ??? //new NNHeaderNEV()
    }

    val fileAdapter = new FileWriteNeuralynx(new File(fileName), header, FileNEVInfo.recordSize)

    val fHand = fileAdapter.handle

    fHand.seek(0)
    fHand.writeUInt16(header.getNeuralynxHeaderString().toCharArray())

    fHand.seek(fileAdapter.headerBytes)

    for (event <- dataElem.readPortEventArray()) {
      //skip nstx(reserved), npkt_id(ID for originating system), npkt_data_size(==2)
      //all of these values seem to be fixed at zero, at least for cheetah 5.5.1
      fHand.skipBytes(6 - 1)
      //cheetah timestamp in microseconds, shifted from unsigned long range to regular Long range
      val qwTimeStamp: BigInt = fHand.readUInt64().toBigInt
      val nevent_id = fHand.readInt16() //fHand.skipBytes(2)
      //Decimal TTL value read from the TTL input port
      val nttl = fHand.readInt16()
      //skip ncrc, ndummy1, ndummy2, dnExtra(8x Int32)
      //all of these values seem to be fixed at zero, at least for cheetah 5.5.1
      fHand.skipBytes(38)  //readInt16(3) //readInt32(8)
      //EventString
      val eventString = new String(fHand.readUInt8(128).filterNot(_ == 0).map(_.toChar))
      val portValue = NNEventNEV.toNEVPortValue(eventString)

    }
    ???
  }

}
