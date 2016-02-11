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

trait OptDouble extends Opt {
  val value: Double
}

trait OptInt extends Opt {
  val value: Int
}
