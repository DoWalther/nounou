package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV}
import nounou.elements.data.NNData
import nounou.elements.data.traits.NNTiming
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

/**
 * @author ktakagaki
 * //@date 2/16/14.
 */
abstract class NNFilterDownsampleParent( parentVal: NNData ) extends NNFilter( parentVal ) {

  protected var timingBuffer: NNTiming = null
  protected var factorVar = -1
  //def this(parentVal: NNData) = this(parentVal, 16)

  final protected val parentBuffer: NNFilterBuffer = new NNFilterBuffer(parentVal)
  final override def changedDataImpl() = if(parentBuffer!= null) parentBuffer.changedData()
  final override def changedDataImpl(ch: Int) = if(parentBuffer!= null) parentBuffer.changedData(ch)
  final override def changedDataImpl(ch: Array[Int]) = if(parentBuffer!= null) parentBuffer.changedData(ch)
  //ToDo: make above full, and refactor out to prebufferfilter

  final override def timing(): NNTiming = timingBuffer

  final protected def refreshTimingBuffer(factor: Int) = {
    timingBuffer =
      new NNTiming(
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

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl(): String = s"factor=$factorVar"

  override def toStringFullImpl(): String = ""

  // </editor-fold>

}