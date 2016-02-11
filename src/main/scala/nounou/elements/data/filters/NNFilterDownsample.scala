package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV, min}
import nounou.elements.data.NNData
import nounou.elements.traits.NNTiming
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

/**
 * @author ktakagaki
 * //@date 2/16/14.
 */
class NNFilterDownsample( parentVal: NNData, protected var initialFactor: Int )
  extends NNFilterDownsampleParent( parentVal ) {

  setFactor(initialFactor)

  // <editor-fold defaultstate="collapsed" desc=" factor-related ">

  //def getFactor(): Int = factorVar

  override def setFactor( factor: Int ) = {
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