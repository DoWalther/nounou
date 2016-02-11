package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV, convert}
import breeze.signal._
import breeze.signal.support.FIRKernel1D
import nounou.elements.data.NNData
import nounou.NN._
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

/**
 * @author ktakagaki
 * //@date 2/1314.
 */
class NNFilterDecimate( parentVal: NNData, factorVarInput: Int )
    extends NNFilterDownsampleParent( parentVal ) {

  def this(parentVal: NNData) = this(parentVal, 10)

  var kernel: FIRKernel1D[Double] = null
  setFactor( factorVarInput )

  // <editor-fold defaultstate="collapsed" desc=" accessors ">

  override def setFactor( factor: Int ): Unit = {
    require( factor <= 32,
      logger.error( "Downsample rate {} must be <= 32." , factor.toString )
    )

    if( factor == factorVar){ //this.factorVar ){
      logger.trace( "factor is already {}, not changing. ", factor.toString )
    } else {
      factorVar = factor
      refreshTimingBuffer(factor)
      if(factor != 1){
        this.kernel = designFilterDecimation[ FIRKernel1D[Double] ](factor, multiplier = 1d)
        logger.info( "set kernel to {}", kernel )
      }
      changedData()
    }

  }

//  def setDecimateOff(): Unit = if(kernel == null){
//    logger.info( "filter is already off, not changing. ")
//  } else {
//    logger.info( "Turning filter kernel off." )
//    factorVar = 1
//    kernel = null
//  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double =
    if(factorVar == 1){
      parentVal.readPointImpl(channel, frame, segment)
    } else {
      //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = parentVal.readTraceDV(
        channel,
        NNRange(frame * factorVar - kernel.overhangPre, frame * factorVar + kernel.overhangPost, 1, segment)
      )
      val tempRet = convolve( tempData, kernel.kernel, overhang = OptOverhang.None )
      require( tempRet.length == 1, "something is wrong with the convolution!" )
      tempRet(0)
    }

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DV[Double] =
    if(factorVar == 1){
        parentVal.readTraceDVImpl(channel, range)
    } else {

      val instantiatedRange = new NNRangeInstantiated(
        range.start * factorVar - kernel.overhangPre, range.last * factorVar + kernel.overhangPost, 1, range.segment
      )
      //by calling _parent.readTrace instead of _parent.readTraceImpl,
      // we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = parentBuffer.readTraceDV(channel, instantiatedRange)

      val tempRes: DV[Double] =
        convolve( tempData, kernel.kernel,
          range = OptRange.RangeOpt( new Range.Inclusive(0, (range.last - range.start)*factorVar, range.step*factorVar) ),
          overhang = OptOverhang.None )

      tempRes / kernel.multiplier
    }

  // </editor-fold>

  }