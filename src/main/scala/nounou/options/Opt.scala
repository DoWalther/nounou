package nounou.options

import nounou.util.LoggingExt

/**
  * function(var1, var2, var3,   Opt1(val1), Opt2(Val2), Opt3(Val3), ....)
  * function(var1, var2, var3,   "Opt1" -> "val1" , "Opt2" -> "val2", "Opt3" -> "val3", ....)
  *
  * Created by ktakagaki on 16/01/15.
  */
trait Opt extends breeze.util.Opt with LoggingExt {

//  val name: String
//  val shortDescription: String // = "Short description of the option"
//  val longDescriptionImpl: String // = "Long description of the option to continue after the short description"
//  final def longDescription = shortDescription + longDescriptionImpl

//  /**Checks whether other required options are set
//    */
//  def checkRequirements(opts: Opt*): Boolean = true

}


trait OptString extends Opt{
  val valueString: String
}
trait OptStringDependent extends Opt{
  def valueString(opts: Seq[Opt]): String
}

//abstract class Marker(val value: String) extends Opt
//trait OptMarker extends Opt {
//  val value: Marker
//}

trait OptDouble extends Opt {
  val valueDouble: Double
}

trait OptInt extends Opt {
  val valueInt: Int
}

trait OptBoolean extends Opt {
  val value: Boolean
}
