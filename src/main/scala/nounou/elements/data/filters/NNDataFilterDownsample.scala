package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV, min}
import nounou.elements.data.NNData
import nounou.elements.traits.NNTiming
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

/**
 * @author ktakagaki
 * //@date 2/16/14.
 */
class NNDataFilterDownsample( private val parentVal: NNData, protected var initialFactor: Int )
  extends NNDataFilter( parentVal ) {

  protected var timingBuffer: NNTiming = null//parentVal.timing()
  protected var factorVar = -1
  setFactor(initialFactor)

  def this(parentVal: NNData) = this(parentVal, 16)

  // <editor-fold defaultstate="collapsed" desc=" factor-related ">


  override def timing(): NNTiming = timingBuffer

  protected def refreshTimingBuffer(factor: Int) = {
    timingBuffer = new NNTiming(
      parentVal.timing.sampleRate / factor.toDouble,
      (for(seg <- 0 until parentVal.timing.segmentCount)
        yield ( (parentVal.timing.segmentLength(seg) - 1).toDouble/factor).round.toInt + 1 ).toArray,
      parentVal.timing.segmentStartTss
    )
  }

  def getFactor(): Int = factorVar

  def setFactor( factor: Int ) = {
    loggerRequire( factor >= 1, "new factor {} cannot be less than 1!", factor.toString )
    if( factor == this.factorVar ){
      logger.trace( "factor is already {}, not changing. ", factor.toString )
    } else {
      this.factorVar = factor
      refreshTimingBuffer(factor)
      logger.info( "changed factor to {}", factor.toString )
      changedData()
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double =
    parentVal.readPointImpl(channel, frame*factorVar, segment)

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DV[Double] =
    if(factorVar == 1){
      parentVal.readTraceDVImpl(channel, range)
    } else {
      parentVal.readTraceDV(channel,
                new NNRangeInstantiated(
                          range.start*factorVar,
                          min(range.last*factorVar, parentVal.timing.segmentLength(range.segment)-1),
                          range.step*factorVar,
                          range.segment)
        )
    }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl(): String = s"factor=$factorVar"

  override def toStringFullImpl(): String = ""

  // </editor-fold>

}