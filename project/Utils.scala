import sbt._
import scala.Console.{RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE}


object Utils {
  type Log = String => Unit

  def job(body: => Boolean): Unit = {
    if (!body) sys.error("Did not succeed")
  }


  def git(args: Any*) = Process("git" +: args.map(_.toString)).!!.trim


  def colorLog(state: State, level: Level.Value = Level.Info): Log = {
    val logger = state.log
    if (logger.ansiCodesSupported) { msg =>
      logger.log(level, msg
        .replace("[RED]", RED)
        .replace("[GREEN]", GREEN)
        .replace("[YELLOW]", YELLOW)
        .replace("[BLUE]", BLUE)
        .replace("[MAGENTA]", MAGENTA)
        .replace("[CYAN]", CYAN)
        .replace("[WHITE]", WHITE))
    } else { msg =>
      logger.log(level, msg
        .replace("[RED]", "")
        .replace("[GREEN]", "")
        .replace("[YELLOW]", "")
        .replace("[BLUE]", "")
        .replace("[MAGENTA]", "")
        .replace("[CYAN]", "")
        .replace("[WHITE]", ""))
    }
  }

  // an SBT AbstractLogger that logs to /dev/nul
  object NopLogger extends AbstractLogger {
    def getLevel = Level.Error
    def setLevel(newLevel: Level.Value): Unit = {}
    def setTrace(flag: Int): Unit = {}
    def getTrace = 0
    def successEnabled = false
    def setSuccessEnabled(flag: Boolean): Unit = {}
    def control(event: ControlEvent.Value, message: => String): Unit = {}
    def logAll(events: Seq[LogEvent]): Unit = {}
    def trace(t: => Throwable): Unit = {}
    def success(message: => String): Unit = {}
    def log(level: Level.Value, message: => String): Unit = {}
  }
}
