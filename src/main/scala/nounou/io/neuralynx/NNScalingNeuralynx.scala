package nounou.io.neuralynx

import breeze.linalg.DenseVector
import nounou.elements.traits.NNScaling

/**
  * This trait extends [[nounou.elements.traits.NNScaling]] to include
  * values <=> short conversion for writing to Neuralynx files.
  *
  */
class NNScalingNeuralynx(override val unit: String, val absolutePerShort: Double) extends NNScaling(unit) {

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl() = s"unit=${unit}"

  override def toStringFullImpl() = ""

  // </editor-fold>

  @transient
  lazy val shortPerAbsolute = 1d / absolutePerShort

  // <editor-fold defaultstate="collapsed" desc=" conversion functions absolute <=> neuralynx Short integer ">

  /**
    * Converts data in the neuralynx internal representation (Short) to absolute units (Double)
    */
  final def convertShortToAbsolute(data: Short) = data.toDouble * absolutePerShort

  /**
    * Converts data in the neuralynx internal representation (Short) to absolute units (Double)
    */
  final def convertShortToAbsolute(data: DenseVector[Short]): DenseVector[Double] =
    DenseVector( convertShortToAbsolute( data.toArray ) )

  /**
    * Converts data in the neuralynx internal representation (Short) to absolute units (Double)
    */
  final def convertShortToAbsolute(data: Array[Short]): Array[Double] = data.map( convertShortToAbsolute _ )

  /**
    * Converts data in absolute units (Double) to neuralynx internal representation (Short)
    */
  final def convertAbsoluteToShort(data: Double) = (data * shortPerAbsolute).toShort

  /**
    * Converts data in absolute units (Double) to neuralynx internal representation (Short)
    */
  final def convertAbsoluteToShort(data: DenseVector[Double]): DenseVector[Short] =
    DenseVector( convertAbsoluteToShort( data.toArray ) )

  /**
    * Converts data in absolute units (Double) to neuralynx internal representation (Short)
    */
  final def convertAbsoluteToShort(data: Array[Double]): Array[Short] = data.map( convertAbsoluteToShort _ )

  // </editor-fold>

  override def equals(that: Any) = that match {
    case x: NNScalingNeuralynx => unit == x.unit
    case _ => false
  }

  override def hashCode = unit.hashCode + absolutePerShort.hashCode()

}