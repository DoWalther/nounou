package nounou.options

import scala.reflect.ClassTag

/**
  * Specifies objects/classes which handle nounou.Opt objects for specifying
  * options, and give the handling code for this.
  *
  */
object OptHandler {

  // <editor-fold defaultstate="collapsed" desc=" asOptTypeOption/isOptType ">

  def asOptTypeOption[T <: Opt](opt: Opt)(implicit tag: ClassTag[T]): Option[T] = {
    opt match {
      case x: T => Some(x)
      case _ => None
    }
  }

  def isOptType[T <: Opt](opt: Opt)(implicit tag: ClassTag[T]): Boolean = {
    opt match {
      case x: T => true
      case _ => false
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readOptXXX ">

  def readOpt[T <: Opt](opts: Seq[Opt])(implicit tag: ClassTag[T]): Option[T] = {
    val itr = opts.iterator
    var tempReturn: Option[T] = None
    while(tempReturn == None && itr.hasNext){
      tempReturn = asOptTypeOption[T](itr.next)
    }
    tempReturn
  }

  def readOptT[T <: Opt](opts: Seq[Opt], default: T)(implicit tag: ClassTag[T]): T = {
    readOpt[T](opts) match {
      case Some(x: T) => x
      case None => default
    }
  }

  def readOptInt[T <: OptInt](opts: Seq[Opt], default: Int)(implicit tag: ClassTag[T]): Int = {
    readOpt[T](opts) match {
      case Some(x: OptInt) => x.value
      case None => default
    }
  }
  def readOptDouble[T <: OptDouble](opts: Seq[Opt], default: Double)(implicit tag: ClassTag[T]): Double = {
    readOpt[T](opts) match {
      case Some(x: OptDouble) => x.value
      case None => default
    }
  }
  def readOptString[T <: OptString](opts: Seq[Opt], default: String)(implicit tag: ClassTag[T]): String = {
    readOpt[T](opts) match {
      case Some(x: OptString) => x.value
      case None => default
    }
  }
  def readOptBoolean[T <: OptBoolean](opts: Seq[Opt], default: Boolean)(implicit tag: ClassTag[T]): Boolean = {
    readOpt[T](opts) match {
      case Some(x: OptBoolean) => x.value
      case None => default
    }
  }

  // </editor-fold>

}




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
//  def readOptString[T <: OptString[T] ](opts: Seq[Opt], default: String)(implicit tag: TypeTag[T]): String = {
//    val tempRet = opts.find( (opt: Opt) => typeOf[Opt] =:= typeOf[T] )
//    tempRet match {
//      case Some(x: OptString[T]) => x.valueString
//      case None => default
//    }
//  }
//
//  def readOptDouble[T <: OptDouble[T] ](opts: Seq[Opt], default: Double)(implicit tag: TypeTag[T]): Double = {
//    val tempRet = opts.find( (opt: Opt) => typeOf[Opt] <:< typeOf[T] )
//    tempRet match {
//      case Some(x: OptDouble[T]) => x.valueDouble
//      case None => default
//    }
//  }
//
//  def readOptInt[T <: OptInt[T]](opts: Seq[Opt], default: Int)(implicit tag: TypeTag[T]): Int = {
//    val tempRet = opts.find( (opt: Opt) => typeOf[Opt] <:< typeOf[T] )
//    tempRet match {
//      case Some(x: OptInt[T]) => x.valueInt
//      case None => default
//    }
//  }
//  def readOptDouble[T <: OptDouble](opts: Seq[Opt], default: Double): Double = {
//    val tempRet = opts.find( _.isInstanceOf[T] )
//    tempRet match {
//      case Some(x: T) => x.valueDouble
//      case None => default
//    }
//  }
//
//  def readOptInt[T <: OptInt](opts: Seq[Opt], default: Int): Int = {
//    val tempRet = opts.find( _.isInstanceOf[T] )
//    tempRet match {
//      case Some(x: T) => x.valueInt
//      case None => default
//    }
//  }
