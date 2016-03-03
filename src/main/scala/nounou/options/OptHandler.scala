package nounou.options

import scala.reflect.ClassTag

/**
  * Specifies objects/classes which handle nounou.Opt objects for specifying
  * options, and give the handling code for this.
  *
  */
trait OptHandler {

  //ToDo 2: resolve type erasure warning!
//  def readOptObject[T <: Opt](opts: Seq[Opt], default: T): T = { //(implicit tag: ClassTag[T])
//    val tempRet = opts.find( _.isInstanceOf[T] )
//    tempRet match {
//      case Some(x: T) => x
//      case None => default
//    }
//  }

//  def readOptMarker[T <: OptMarker](opts: Seq[Opt], default: T): T = { //(implicit tag: ClassTag[T])
//    val tempRet = opts.find( _.isInstanceOf[T] )
//    tempRet match {
//      case Some(x: T) => x
//      case None => default
//    }
//  }
  def readOptString[T <: OptString](opts: Seq[Opt], default: String): String = { //(implicit tag: ClassTag[T])
  val tempRet = opts.find( _.isInstanceOf[T] )
    tempRet match {
      case Some(x: T) => x.valueString
      case None => default
    }
  }

  def readOptDouble[T <: OptDouble](opts: Seq[Opt], default: Double): Double = {
    val tempRet = opts.find( _.isInstanceOf[T] )
    tempRet match {
      case Some(x: T) => x.valueDouble
      case None => default
    }
  }

  def readOptInt[T <: OptInt](opts: Seq[Opt], default: Int): Int = {
    val tempRet = opts.find( _.isInstanceOf[T] )
    tempRet match {
      case Some(x: T) => x.valueInt
      case None => default
    }
  }

}
