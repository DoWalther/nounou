import sbt._

object Dependencies {

  val resolutionRepos = Seq(
    Resolver.mavenLocal,
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
  )

  val scalalog    = "com.typesafe.scala-logging"        %% "scala-logging"             % "3.1.0" exclude("org.scala-lang", "scala-reflect")
  val logback     = "ch.qos.logback"                     % "logback-classic"           % "1.1.3"
  val scalactic   = "org.scalactic"                     %% "scalactic"                 % "2.2.6" % "test"
  val scalatest   = "org.scalatest"                     %% "scalatest"                 % "2.2.6" % "test" 
  val googlegson  = "com.google.code.gson"               % "gson"                      % "2.5" 
  val commonsio   = "commons-io"                         % "commons-io"                % "2.4" 
  val eclipsejgit = "org.eclipse.jgit"                   % "org.eclipse.jgit"          % "4.2.0.201601211800-r" 
  val scalafx     = "org.scalafx"                       %%  "scalafx"                  % "8.0.60-R9" 



  val backendDependencies = Seq(scalalog, logback, scalactic, scalatest, googlegson,
    commonsio, eclipsejgit, scalafx)

}

