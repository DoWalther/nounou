package nounou.options

trait OptThreshold extends Opt

trait OptBlackout extends OptThreshold
case class OptBlackoutInt(override val value: Int)
  extends OptInt(value) with OptBlackout{

  loggerRequire(value > 0, "blackout must be >0")
}

//
//
///**Option to be used in [[nounou.analysis.Threshold]]
//  */
//case class OptBlackout(frames: Int) extends nounou.options.Opt
//  with ThresholdOpt
//{
//  loggerRequire(frames > 0, "blackout must be >0")
//}
