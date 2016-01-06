package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV, convert}
import breeze.signal._
import breeze.signal.support.FIRKernel1D
import nounou.NN._
import nounou.elements.data.NNData
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

/**
 * @author ktakagaki
 * //@date 2/1314.
 */
class NNDataFilterDecimate( parentVar: NNData )
    extends NNDataFilterDownsample( parentVar ) {

    var kernel: FIRKernel1D[Long] = null

    override def setFactor( factor: Int ): Unit = {
      require( factor <= 16,
        logger.error( "Downsample rate {} must be <= 16." , factor.toString )
      )

      if( factor == this.factor ){
        logger.trace( "factor is already {}, not changing. ", factor.toString )
      } else if(factor == 1) setDecimateOff()
      else {
        kernel = designFilterDecimation[ FIRKernel1D[Long] ](factor, multiplier = 1024L)
        this.factorVar = factor
        changedData()
        logger.info( "set kernel to {}", kernel )
      }
    }

    def setDecimateOff(): Unit = if(kernel == null){
      logger.info( "filter is already off, not changing. ")
    } else {
      logger.info( "Turning filter kernel off." )
      factorVar = 1
      kernel = null
    }




    override def readPointIntImpl(channel: Int, frame: Int, segment: Int): Int =
      if(kernel == null){
        parentVar.readPointIntImpl(channel, frame, segment)
      } else {
        //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
        val tempData = parentVar.readTraceIntDV( channel, NNRange(frame * factor - kernel.overhangPre, frame * factor + kernel.overhangPost, 1, segment))//, OptSegment(segment) ))
        val tempRet = convolve( DV( tempData.map(_.toLong).toArray ), kernel.kernel, overhang = OptOverhang.None )
        require( tempRet.length == 1, "something is wrong with the convolution!" )
        tempRet(0).toInt
      }

    override def readTraceIntDVImpl(channel: Int, range: NNRangeValid /*Range.Inclusive, segment: Int*/): DV[Int] =
      if(kernel == null){
          parentVar.readTraceIntDVImpl(channel, range/*, segment*/)
      } else {
          //by calling _parent.readTrace instead of _parent.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
          val realRange = new NNRangeInstantiated(range.start * factor - kernel.overhangPre, range.last * factor + kernel.overhangPost, 1, range.segment )
          val tempData = parentVar.readTraceIntDV(channel, realRange)
            //RangeFr(range.start * factor - kernel.overhangPre, range.last * factor + kernel.overhangPost, 1, OptSegment(segment) ))
//        println("tempData: " + tempData.length)
//        println("kernel: " + kernel.kernel.length)
//        println("start: " + range.start + " end: " + range.end+ " stepMs: " + range.stepMs+ " inclusive: " + range.isInclusive)
          val tempRes: DV[Long] =
            convolve( convert( tempData, Long ), kernel.kernel,
              range = OptRange.RangeOpt( new Range.Inclusive(0, (range.last - range.start)*factor, range.step*factor) ),
              overhang = OptOverhang.None )
//        println("tempRes: " + tempRes.length)
          convert( (tempRes / kernel.multiplier.toLong ), Int)
      }

//    override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame * factor, segment)
//    override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame * factor, channels, segment)



  }