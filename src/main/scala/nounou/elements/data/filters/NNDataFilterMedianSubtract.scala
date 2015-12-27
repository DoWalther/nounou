package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV}
import breeze.numerics.isOdd
import breeze.signal.{OptOverhang, filterMedian}
import breeze.stats.median
import nounou.elements.data.NNData
import nounou.elements.ranges.{SampleRange, SampleRangeValid}

/**This filter applies a median subtraction, which is a non-linear form of high-pass which is
  * less biased by extreme transients like spiking.
 * @author ktakagaki
 * //@date 3/17/14.
 */
class NNDataFilterMedianSubtract( private var parenVar: NNData ) extends NNDataFilter( parenVar ) {

  private var _windowLength = 1
  private var windowLengthHalf = 0
  _active = false
  private val upstreamBuff: NNData = new NNDataFilterBuffer(parenVar)

  override def toStringImpl() = {
    if(_windowLength==1) "off/no median subtract, "
    else s"windowLength=$windowLength, "
  }
  override def toStringFullImpl() = ""



  def setWindowLength( value: Int ): Unit = {
    loggerRequire( value > 0, "Parameter windowLength must be bigger than 0, invalid value: {}", value.toString)
    loggerRequire( isOdd(value), "Parameter windowLength must be odd to account correctly for overhangs, invalid value: {}", value.toString)
    if(_windowLength != value){
      _windowLength = value
      windowLengthHalf = (_windowLength-1)/2
      setActive( if(_windowLength == 1) false else true )
      changedData()
    }
  }
  def getWindowLength(): Int = _windowLength
  def windowLength_=( value: Int ) = setWindowLength( value )
  def windowLength() = _windowLength


  // <editor-fold defaultstate="collapsed" desc=" calculate data ">

  override def readPointIntImpl(channel: Int, frame: Int, segment: Int): Int =
    if(windowLength == 1){
      upstreamBuff.readPointIntImpl(channel, frame, segment)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstreamBuff.readTraceIntDV(
                          channel,
                          new SampleRange(frame - windowLengthHalf, frame + windowLengthHalf, 1, segment) )
      median(tempData).toInt
    }

  override def readTraceIntDVImpl(channel: Int, ran: SampleRangeValid): DV[Int] =
    if(windowLength == 1){
      upstreamBuff.readTraceIntDVImpl(channel, ran)
    } else {
      //by calling upstream.readTrace instead of upstream.readTraceImpl, we can deal with cases where the kernel will overhang actual data, since the method will return zeros
      val tempData = upstreamBuff.readTraceIntDV(
        channel,
        new SampleRange( ran.start - windowLengthHalf, ran.last + windowLengthHalf, 1, ran.segment) )
      tempData(windowLengthHalf to -windowLengthHalf-1) - filterMedian(tempData, windowLength, OptOverhang.None)
    }

  //  override def readFrameImpl(frame: Int, segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, segment)
  //  override def readFrameImpl(frame: Int, channels: Vector[Int], segment: Int): Vector[Int] = super[XDataFilter].readFrameImpl(frame, channels, segment)

  // </editor-fold>

}
