package nounou.options

import nounou.util.LoggingExt

/**
  * Created by ktakagaki on 16/01/15.
  */
trait Opt extends breeze.util.Opt with LoggingExt {

  val shortDescription = "Short description of the option"
  val longDescriptionImpl = "Long description of the option to continue after the short description"
  lazy val longDescription = shortDescription + longDescriptionImpl

  /**Checks whether other required options are set
    */
  def checkRequirements(opts: Opt*): Boolean = true

}


trait  OptString extends Opt{
  val value: String
}

//abstract class Marker(val value: String) extends Opt
//trait OptMarker extends Opt {
//  val value: Marker
//}

trait OptDouble extends Opt {
  val value: Double
}

trait OptInt extends Opt {
  val value: Int
}

trait OptBoolean extends Opt {
  val value: Boolean
}
