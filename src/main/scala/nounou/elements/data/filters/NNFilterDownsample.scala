package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV, min}
import nounou.elements.data.NNData
import nounou.elements.traits.NNTiming
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

/**
 * @author ktakagaki
 * //@date 2/16/14.
 */
class NNFilterDownsample(private val parentVal: NNData, protected var initialFactor: Int )
  extends NNFilter( parentVal ) {

  protected var timingBuffer: NNTiming = null
  protected var factorVar = -1
  setFactor(initialFactor)

  def this(parentVal: NNData) = this(parentVal, 16)


  private val parentBuffer: NNFilterBuffer = new NNFilterBuffer(parentVal)
  override def changedDataImpl() = if(parentBuffer!= null) parentBuffer.changedData()
  override def changedDataImpl(ch: Int) = if(parentBuffer!= null) parentBuffer.changedData(ch)
  override def changedDataImpl(ch: Array[Int]) = if(parentBuffer!= null) parentBuffer.changedData(ch)
  //ToDo: make above full, and refactor out to prebufferfilter

  override def timing(): NNTiming = timingBuffer

  protected def refreshTimingBuffer(factor: Int) = {
    timingBuffer = new NNTiming(
      sampleRate = parentVal.timing.sampleRate / factor.toDouble,
      _segmentLengths = (for(seg <- 0 until parentVal.timing.segmentCount)
        yield ( (parentVal.timing.segmentLength(seg)-1)/factor) + 1 ).toArray,
      _segmentStartTss = parentVal.timing.segmentStartTss,
      filterDelay = parentVal.timing.filterDelay
    )
  }

  // <editor-fold defaultstate="collapsed" desc=" factor-related ">

  def getFactor(): Int = factorVar

  def setFactor( factor: Int ) = {
    loggerRequire( factor >= 1, "new factor {} cannot be less than 1!", factor.toString )
    if( factor == this.factorVar ){
      logger.trace( "factor is already {}, not changing. ", factor.toString )
    } else {
      this.factorVar = factor
      //if( factor == 1 ) parentBuffer.flushBuffer()
      refreshTimingBuffer(factor)
      logger.info( "changed factor to {}", factor.toString )
      changedData()
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double =
    parentBuffer.readPointImpl(channel, frame*factorVar, segment)

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DV[Double] =
    if(factorVar == 1){
      parentVal.readTraceDVImpl(channel, range)
    } else {
      val newRange = new NNRangeInstantiated(
              range.start*factorVar,
              range.last*factorVar,//min(range.last*factorVar, parentVal.timing.segmentLength(range.segment)-1),
              range.step*factorVar,
              range.segment
      )
//      println( newRange )
//      println( parentBuffer )
//      println( parentBuffer.timing().toStringFull() )
      parentBuffer.readTraceDV(channel, newRange)
    }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl(): String = s"factor=$factorVar"

  override def toStringFullImpl(): String = ""

  // </editor-fold>

}