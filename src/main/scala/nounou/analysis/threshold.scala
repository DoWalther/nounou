package nounou.analysis

import nounou.NN.OptThresholdBlackout
import nounou.Opt

import scala.collection.mutable.ArrayBuffer

/** Thresholds a data array.
  * To be transferred to breeze in the near future, once options are cleared up.
  * Created by ktakagaki on 15/09/15.
  */
object threshold {

//  case class OptThresholdDirection(direction: Int) extends Opt {
//    loggerRequire(-1 <= direction && direction <= 1, s"Direction must be -1, 0 or 1, not $direction")
//  }
//  val OptThresholdDirectionPositive = OptThresholdDirection(1)
//  val OptThresholdDirectionBoth = OptThresholdDirection(0)
//  val OptThresholdDirectionNegative = OptThresholdDirection(-1)

  def apply(data: Array[Double], threshold: Double, opts: Opt*): Array[Int] = {

    // <editor-fold defaultstate="collapsed" desc=" Handle options ">

    var optThresholdBlackout = 1
    for( opt <- opts ) opt match {
      case OptThresholdBlackout(frames: Int) => optThresholdBlackout = frames
      case _ => {}
    }

    // </editor-fold>

    /** Values to return */
    val tempReturn: ArrayBuffer[Int] =  new ArrayBuffer[Int]()

    var count = 0
    var thresholdTriggered = false

    //main loop
    while(count < data.length){
      //if a threshold has been crossed previously, fast forward until under threshold again
      if(thresholdTriggered){
        if( data(count) < threshold ) thresholdTriggered= false
        count += 1

      } else {
        //if the threshold is newly crossed
        if( data(count) >= threshold ){
          thresholdTriggered = true
          tempReturn.append( count )
          count += optThresholdBlackout
        }else{
          count += 1
        }

      }

    }

    tempReturn.toArray
  }



}
