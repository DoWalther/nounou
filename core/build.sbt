import com.atlassian.labs.gitstamp.GitStampPlugin._

organization := "com.github.ktakagaki.nounou"

name := "nounou-core"

scalaVersion := Common.scalaVersion

publishMavenStyle := true

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq(
//  "ch.qos.logback" % "logback-classic" % "1.1.3",
//  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  //breeze from custom *.jar
  //"org.scalanlp" %% "breeze" % "0.11.2",
  //"org.scalanlp" %% "breeze-natives" % "0.11.2",
  "com.github.ktakagaki.breeze" % "breeze_2.11" % "0.13-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.1.7" % "test",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.1.1.201511131810-r",
  "commons-io" % "commons-io" % "2.4"
)

Seq( gitStampSettings: _* )


//unmanagedBase <<= baseDirectory {base => base/"lib"}