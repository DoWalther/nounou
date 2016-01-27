package nounou.options

import nounou.analysis.ThresholdOpt

/**Option to be used in [[nounou.analysis.Threshold]]
  */
case class OptBlackout(frames: Int) extends nounou.options.Opt
  with ThresholdOpt
{
  loggerRequire(frames > 0, "blackout must be >0")
}
