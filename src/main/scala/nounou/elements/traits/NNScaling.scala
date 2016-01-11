package nounou.elements.traits

import breeze.linalg.DenseVector
import nounou.elements.NNElement


/**
  * This trait encapsulates scaling and unit information for
  * NNData and NNDataChannel objects
  * electrophysiological and imaging recordings.
  *
 * Created by Kenta on 12/15/13.
 */
class NNScaling(
                 /**The minimum extent down to which the data runs*/
                 val minValue: Int,
                 /**The maximum extent up to which the data runs */
                 val maxValue: Int,
                 /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
                     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
                     * absoluteGain must take into account the extra bits used to pad Int values.
                     */
                 @deprecated
                 val absGain: Double,
                 /**Used to calculate the absolute value (mV, etc) based on internal representation.<p>
                     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(absolute value)=(internal value)*dataAbsoluteGain + dataAbsoluteOffset
                     */
                 @deprecated
                 val absOffset: Double,
                 /**The name of the absolute units, as a String (eg mv).
                     */
                 val unit: String,
                 /**The number (eg 1024) multiplied to original raw data from the recording instrument
                     *(usu 14-16 bit) to obtain internal Int representation.
                     */
                 @deprecated
                 val xBits: Int = 1024) extends NNElement {

  override def toStringImpl() = s"min/max=${minValue}/${maxValue}, " +
    s"absGain/Offset/Unit=${absGain}/${absOffset}/${unit}, xBits=${xBits}, "
  override def toStringFullImpl() = ""

  /**(xBits:Int).toDouble
    */
  @deprecated
  final lazy val xBitsD = xBits.toDouble


  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  @deprecated
  final def convertIntToAbsolute(data: Int) = data.toDouble * absGain + absOffset
  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  @deprecated
  final def convertIntToAbsolute(data: DenseVector[Int]): DenseVector[Double] =
    DenseVector( data.toArray.map( convertIntToAbsolute _ ) )

  @deprecated
  final def convertIntToAbsolute(data: Array[Int]): Array[Double] = data.map( convertIntToAbsolute _ )

  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  @deprecated
  final def convertAbsoluteToInt(dataAbs: Double) = ((dataAbs - absOffset) / absGain).toInt //ToDo 4: change to multiply?

  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
    * absUnit (e.g. "mV")
    */
  @deprecated
  final def convertAbsoluteToInt(dataAbs: DenseVector[Double]): DenseVector[Int] = dataAbs.map( convertAbsoluteToInt _ )
  @deprecated
  final def convertAbsoluteToInt(dataAbs: Array[Double]): Array[Int] = dataAbs.map( convertAbsoluteToInt _ )

//  override def isCompatible(that: NNElement): Boolean = {
//    that match {
//      case x: NNDataScale => {
//        (this.xBits == x.xBits) && (this.absGain == x.absGain) && (this.absOffset == x.absOffset) && (this.absUnit == x.absUnit) &&
//          (this.maxValue == x.maxValue) && (this.minValue == x.minValue)
//      }
//      case _ => false
//    }
//  }

  override def equals(that: Any) = that match {
    case x: NNScaling => minValue == x.minValue && maxValue == x.maxValue &&
      absGain == x.absGain && absOffset == x.absOffset &&
      unit == x.unit && xBits == x.xBits
    case _ => false
  }

  override def hashCode = minValue + maxValue*41 + (absGain * 41.112 + absOffset * 41.112).toInt + unit.hashCode + xBits * 411

}

object NNScaling {

  val raw: NNScaling = new NNScaling( Int.MinValue, Int.MaxValue, 1d, 0d, "Raw scaling")

  def apply(minValue: Int, maxValue: Int, absGain: Double, absOffset: Double,  absUnit: String): NNScaling =
    new NNScaling( minValue, maxValue, absGain, absOffset, absUnit)

}