package nounou.options

import nounou.util.LoggingExt

/**
  * function(var1, var2, var3,   Opt1(val1), Opt2(Val2), Opt3(Val3), ....)
  * function(var1, var2, var3,   "Opt1" -> "val1" , "Opt2" -> "val2", "Opt3" -> "val3", ....)
  *
  * Created by ktakagaki on 16/01/15.
  */
trait Opt extends breeze.util.Opt with LoggingExt

class OptInt(val value: Int) extends Opt
class OptDouble(val value: Double) extends Opt
class OptString(val value: String) extends Opt
class OptBoolean(val value: Boolean) extends Opt
class OptAutomatic extends Opt
class OptNone extends Opt






  //val tpe = typeTag[T]

//  val name: String
//  val shortDescription: String // = "Short description of the option"
//  val longDescriptionImpl: String // = "Long description of the option to continue after the short description"
//  final def longDescription = shortDescription + longDescriptionImpl

//  /**Checks whether other required options are set
//    */
//  def checkRequirements(opts: Opt*): Boolean = true



//trait OptType[T] extends Opt {
//  implicit val tpe: TypeTag[T]
//}
//
//
//trait OptString[T] extends OptType[T]{ // <: Opt
//  val valueString: String
//
//}
////trait OptStringDependent extends Opt{
////  def valueString(opts: Seq[Opt]): String
////}
//
////abstract class Marker(val value: String) extends Opt
////trait OptMarker extends Opt {
////  val value: Marker
////}
//
//trait OptDouble[T] extends OptType[T] {
//  val valueDouble: Double
//}
//
//trait OptInt[T] extends OptType[T] {
//  val valueInt: Int
//}
//
//trait OptBoolean[T] extends OptType[T] {
//  val value: Boolean
//}
