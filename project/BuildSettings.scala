import sbt._
import Keys._


object BuildSettings {
  val VERSION = "1.0-SNAPSHOT"

  lazy val basicSettings = Seq(
    version               := NightlyBuildSupport.buildVersion(VERSION),
    homepage              := Some(new URL("http://www.lin-magdeburg.de/en/departments/systemphysiology/index.jsp")),
    organization          := "de.lin_magdeburg",
    organizationHomepage  := Some(new URL("http://www.lin-magdeburg.de/")),
    description           := "Nounou is a JVM-based interface for loading " +
      "neurophysiological data",
    startYear             := Some(2011),
    licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := "2.11.7",
    resolvers             ++= Dependencies.resolutionRepos,
    scalacOptions         := Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-target:jvm-1.8",
      "-language:_",
      "-Xlog-reflective-calls"
    )
  )

  lazy val nounouModuleSettings =
    basicSettings

}

