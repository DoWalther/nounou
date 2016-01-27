package nounou.options

/**
  * Specifies objects/classes which handle nounou.Opt objects for specifying
  * options, and give the handling code for this.
  *
  */
trait OptHandler {

  def readOptObject[T <: Opt](opts: Seq[Opt], default: T): T = {
    val tempRet = opts.find( _.isInstanceOf[T] )
    tempRet match {
      case Some(x: T) => x
      case None => default
    }
  }

//  def readOptDouble[T <: OptDouble](opts: Seq[Opt], default: Double): Double = {
//    val tempRet = opts.find( _.isInstanceOf[T] )
//    tempRet match {
//      case Some(x: T) => x
//      case None => default
//    }
//  }


}
