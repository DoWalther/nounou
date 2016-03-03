package nounou.elements.data.traits

import nounou.elements.NNElement

object NNScaling {
  implicit def scalingElementToScaling(scalingElement: NNScalingElement) = scalingElement.scaling()
}

/**
  * This trait encapsulates data unit information for NNData and NNDataChannel objects
  * electrophysiological and imaging recordings.
  *
  */
class NNScaling( /**
                   * The name of the absolute units, as a String (eg mV).
                   */
                 val unit: String) extends NNElement {

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl() = s"unit=${unit}"

  override def toStringFullImpl() = ""

  // </editor-fold>

//  /**(xBits:Int).toDouble
//    */
//  @deprecated
//  final lazy val xBitsD = xBits.toDouble


//  // <editor-fold defaultstate="collapsed" desc=" deprecated conversion functions int <=> absolute ">
//
//  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
//    * absUnit (e.g. "mV")
//    */
//  @deprecated
//  final def convertIntToAbsolute(data: Int) = data.toDouble * absGain + absOffset
//
//  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
//    * absUnit (e.g. "mV")
//    */
//  @deprecated
//  final def convertIntToAbsolute(data: DenseVector[Int]): DenseVector[Double] =
//    DenseVector( data.toArray.map( convertIntToAbsolute _ ) )
//
//  @deprecated
//  final def convertIntToAbsolute(data: Array[Int]): Array[Double] = data.map( convertIntToAbsolute _ )
//
//  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
//    * absUnit (e.g. "mV")
//    */
//  @deprecated
//  final def convertAbsoluteToInt(dataAbs: Double) = ((dataAbs - absOffset) / absGain).toInt //ToDo 4: change to multiply?
//
//  /**Converts data in the internal representation (Int) to absolute units (Double), with unit of
//    * absUnit (e.g. "mV")
//    */
//  @deprecated
//  final def convertAbsoluteToInt(dataAbs: DenseVector[Double]): DenseVector[Int] = dataAbs.map( convertAbsoluteToInt _ )
//
//  @deprecated
//  final def convertAbsoluteToInt(dataAbs: Array[Double]): Array[Int] = dataAbs.map( convertAbsoluteToInt _ )
//
//  // </editor-fold>

  override def equals(that: Any) = that match {
    case x: NNScaling => unit == x.unit
    case _ => false
  }

  override def hashCode = unit.hashCode

}


//object NNScaling {
//
//  val raw: NNScaling = new NNScaling( Int.MinValue, Int.MaxValue, 1d, 0d, "Raw scaling")
//
//  def apply(minValue: Int, maxValue: Int, absGain: Double, absOffset: Double,  absUnit: String): NNScaling =
//    new NNScaling( minValue, maxValue, absGain, absOffset, absUnit)
//
//}