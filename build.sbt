organization := "com.github.ktakagaki.nounou"

name := "nounou-parent"

scalaVersion := Common.scalaVersion

import com.atlassian.labs.gitstamp.GitStampPlugin._

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
//  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "com.github.ktakagaki.breeze" % "breeze_2.11" % "0.13-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.1.7" % "test",
  "com.google.code.gson" % "gson" % "2.3.1",
  "commons-io" % "commons-io" % "2.4",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.1.1.201511131810-r"
)


//lazy val root = project.in( file(".") )
//  .aggregate(core, gui/*, examples*/).dependsOn(core, gui/*, examples*/)

lazy val core     = project.in( file("core") )

lazy val gui      = project.in( file("gui") ).dependsOn(core)

//lazy val examples = project.in( file("examples") ).dependsOn(core)

publishMavenStyle := true

Seq( gitStampSettings: _* )

//publishArtifact in (Compile, packageBin) := true

//publishArtifact in (Compile, packageDoc) := true

//publishArtifact in (Compile, packageSrc) := true



//libraryDependencies ++= Seq(
//    "com.atlassian.labs" % "sbt-git-stamp" % "0.1.2"
//)

//assemblyJarName in assembly := "nounou.jar"
//
//test in assembly := {}

//settings = standardSettings ++ SbtOneJar.oneJarSettings

//unmanagedBase <<= baseDirectory {base => base/"lib"}