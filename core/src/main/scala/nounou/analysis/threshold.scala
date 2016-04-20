package nounou.analysis

import nounou.options._
import scala.collection.mutable.ArrayBuffer

/** Thresholds a data array.
  * To be transferred to breeze in the near future, once options are cleared up.
  * Created by ktakagaki on 15/09/15.
  */
object Threshold {

//  case class OptThresholdDirection(direction: Int) extends Opt {
//    loggerRequire(-1 <= direction && direction <= 1, s"Direction must be -1, 0 or 1, not $direction")
//  }
//  val OptThresholdDirectionPositive = OptThresholdDirection(1)
//  val OptThresholdDirectionBoth = OptThresholdDirection(0)
//  val OptThresholdDirectionNegative = OptThresholdDirection(-1)

  def apply(data: Array[Double], threshold: Double, opts: OptThreshold*): Array[Int] = {

    val optBlackout = OptHandler.readOptInt[OptBlackoutInt](opts, 1)
//    var optThresholdBlackout = 1
//    for (opt <- opts) opt match {
//      case OptBlackout(frames: Int) => optThresholdBlackout = frames
//      case _ => {}
//    }

    thresholdWithBlackout(data, threshold, optBlackout)

  }

  def apply(data: Array[Double], threshold: Double): Array[Int] =
    thresholdWithBlackout(data, threshold, 1)

  def thresholdWithBlackout(data: Array[Double], threshold: Double, optThresholdBlackout: Int): Array[Int] = {

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
