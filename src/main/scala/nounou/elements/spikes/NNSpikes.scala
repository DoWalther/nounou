package nounou.elements.spikes

import nounou.elements.{NNDataScaleElement, NNDataTimingElement, NNElement}
import nounou.util.LoggingExt

import scala.collection.mutable.TreeSet

//import nounou.elements.layouts.NNDataLayoutTrode
//import scala.collection.immutable.TreeMap
//import nounou.elements.data.{NNData}
//import nounou.util.LoggingExt
//import breeze.linalg.DenseVector
//
object NNSpikes extends LoggingExt {

}

/** A mutable database of [[nounou.elements.spikes.NNSpike]] objects for display and processing.
  * Based on a [[scala.collection.mutable.TreeSet]], with enforcing of NNSpike compatiblity.
    *
    */
class NNSpikes()//val trodeLayout: NNDataLayoutTrode, val waveFormLength: Int)
  extends NNElement with NNDataTimingElement with NNDataScaleElement {

  override def toStringFullImplParams() = s"no.=${_database.size}, "
  override def toStringFullImplTail() = ""

  private val _database = new TreeSet[NNSpike]()( Ordering.by[NNSpike, BigInt]( (x: NNSpike) => x.timestamp) )
  private var prototypeSpike: NNSpike = null

  def add(elem: NNSpike): Boolean = {
    if( prototypeSpike == null ){
      //if this is the first spike added to the database
      prototypeSpike = elem
    } else if(!prototypeSpike.isCompatible(elem)) {
      //if more than one spike has already been loaded, and they are incompatible with new spike
      throw loggerError(s"Tried to add an incompatible spike: ${elem}")
    }

    _database.add(elem)
  }

  }

//  def readSpikeTs(trode: Int): Array[Long] = {
//    loggerRequire(trodeLayout.isValidTrode(trode), "trode={} is invalid", trode.toString, spikes.length.toString)
//    spikes(trode).map( p => p._1 ).toArray
//  }
//
//  def spikeCount(trode: Int) = {
//    loggerRequire(trodeLayout.isValidTrode(trode), "trode={} is invalid", trode.toString, spikes.length.toString)
//    spikes(trode).size
//  }
//  def spikeCounts() = spikes.map(p=> p.size)
//
//
////  def checkDataCompatibility(xData: XData): Unit = {
////
////    xTrodes match {
////      case XTrodesNull => {
////        logger.info("Will automatically initiate XTrodes layout with XTrodesIndividual object, assuming each channel is individual with no trode interdependencies.")
////        xTrodes = new XTrodesIndividual(xData.channelCount)
////      }
////      case xTr: XTrodes => {
////        if( xTr.channelCount < xData.channelCount ){
////          logger.warn("Previously loaded XTrode layout has less channels than the data given. Hopefully, this is intended...")
////        }
////        loggerRequire( xTr.channelCount == xData.channelCount, "Previously loaded XTrode layout has more channels than data given!")
////      }
////    }
////
////    spikes.length match {
////      case 0 => spikes = Array.tabulate(xTrodes.channelCount)( p => TreeMap[Long, XSpikeWaveform]())
////      case x: Int => {
////        loggerRequire(x == xTrodes.channelCount,
////          "Previously loaded spike data has different channel count ({} )from the XTrode layout ({}). Must be corrected!",
////          x.toString, xTrodes.channelCount.toString)
////      }
////    }
////  }
//
////  // <editor-fold defaultstate="collapsed" desc=" set/getXTrodes ">
////
////  def xTrodes_=( xTrodes: NNTrodes ): Unit = {
////    if( xtr == NNTrodesNull) {
////      if( xdat != NNDataNull ) {
////        loggerRequire( xTrodes.channelCount == channelCount,
////          "You tried to load an XTrode object with channel count {}, when an XData object with channel count {} is already loaded!",
////          xTrodes.channelCount.toString, xdat.channelCount.toString
////        )
////      } else {
////        channelCount = xTrodes.channelCount
////      }
////      xtr = xTrodes
////    }
////  }
////  def xTrodes(): NNTrodes = xtr
////  def setXTrodes( xTrodes: NNTrodes ): Unit = { xTrodes_=( xTrodes ) }
////  def getXTrodes(): NNTrodes = xTrodes()
////
////  // </editor-fold>
////  // <editor-fold defaultstate="collapsed" desc=" set/getXData ">
////
////  def xData_=( xData: NNData ): Unit = {
////    if( xdat == NNDataNull) {
////      if( xtr != NNTrodesNull ) {
////        loggerRequire( xData.channelCount == channelCount,
////          "You tried to load an XData object with channel count {}, when a XTrode object with channel count {} is already loaded!",
////          xData.channelCount.toString, xtr.channelCount.toString
////        )
////      } else {
////        channelCount = xData.channelCount
////      }
////      xdat = xData
////    }
////  }
////  def xData(): NNData = xdat
////  def setXData( xData: NNData ): Unit = { xData_=( xData ) }
////  def getXData(): NNData = xData()
////
////  // </editor-fold>
//
//
//  // <editor-fold desc="addSpike/addSpikes">
//
////  def addSpike(trode: Int, ts: Long, xSpikeWf: XSpikeWaveform) = {
////    loggerRequire(xSpikeWf.length == waveformLength, "tried to add a spike {} which does not have waveformLength {}!", xSpikeWf.toString, waveformLength.toString)
////    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
////    spikes(trode) = spikes(trode) + (ts -> xSpikeWf)
////  }
////
////  def addSpikes(trode: Int, ts: Array[Long], xData: XData, wfPre: Int, wfPost: Int) = {
////    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
////    loggerRequire(wfPre+wfPost+1 == waveformLength, "wfPre={} and wfPost={} are incompatible with waveformLength={}",
////      wfPre.toString, wfPost.toString, waveformLength.toString)
////    spikes(trode) = spikes(trode) ++ ts.map( t => {
////        t -> new XSpikeWaveform(
////          Array.tabulate(xTrodes.trodeSize(trode))(
////            trCh => xData.readTraceA(xTrodes.trodeChannels(trode)(trCh), RangeTsEvent(t, wfPre, wfPost, 1))
////          )
////        )
////      }
////    ).filter(p => p._2.length == waveformLength)
////  }
//
//  // </editor-fold>
//
//  // <editor-fold desc="readSpikes/readSpikeTimes">
//
////  def readSpikes(trode: Int): Array[Array[Array[Int]]] = {
////    loggerRequire(isValidTrode(trode), "trode={} is invalid. spikes.length={}", trode.toString, spikes.length.toString)
//////    val tempret = new ArrayBuffer[Array[Array[Int]]]()
//////    tempret.sizeHint( spikeCount(trode ))
////    spikes(trode).map( p => p._2.waveform ).toArray
////  }
//
//
//  // </editor-fold>
//
////  // <editor-fold desc="XConcatenatable">
////
////  override def :::(that: NNElement): NNSpikes = {
////    that match {
////      case x: NNSpikes => {
////        if( this.isCompatible(x) ) {
////          val temp = new NNSpikes( waveformLength, x.xTrodes, x.xData )
////          temp.spikes ++: this.spikes
////          temp.spikes ++: x.spikes
////          temp
////        }
////        else throw new IllegalArgumentException("cannot concatenate spikes with different waveform lengths")
////      }
////      case _ => {
////        require(false, "cannot concatenate different types!")
////        this
////      }
////    }
////  }
////
////  override def isCompatible(that: NNElement): Boolean =
////    that match {
////      case x: NNSpikes => {
////        this.waveformLength == x.waveformLength && this.xTrodes.isCompatible(x.xTrodes) && this.xData.channelCount == x.xData.channelCount
////      }
////      case _ => false
////    }
////
////  // </editor-fold>
//  override def isCompatible(that: NNElement): Boolean = {
//    that match {
//      case x: NNSpikes => trodeLayout.isCompatible(that) &&
//                            this.waveFormLength == x.waveFormLength &&
//                            super[NNDataTimingElement].isCompatible(x) &&
//                            super[NNDataScaleElement].isCompatible(x)
//      case _ => false
//    }
//
//  }
//}
////
////object NNSpikesNull$$ extends NNSpikes(0) {
////
////  override def toString() = "XSpikesNull"
////
////}
