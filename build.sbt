organization := "com.github.ktakagaki.nounou"

name := "nounou-parent"

scalaVersion := Common.scalaVersion

//scalacOptions += "-target:jvm-1.7"

//javacOptions ++= Seq("-source", "1.7s", "-target", "1.7", "-Xlint")

//Doing the git stamping here will lead to multiple Manifest.MF,
//which causes packageBin to choke: git stamp only once, in core\build
//    import com.atlassian.labs.gitstamp.GitStampPlugin._

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq(
  //"com.typesafe.scala-logging" %% "scala-logging-slf4j_2.11" % "2.1.2",
//  "ch.qos.logback" % "logback-classic" % "1.1.7",
//  "ch.qos.logback" % "logback-classic" % "1.1.7",
//  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
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

//Doing the git stamping here will lead to multiple Manifest.MF files,
//which causes packageBin to choke: git stamp only once, in core\build
//   Seq( gitStampSettings: _* )

//Assembly of dependency artifacts using the following would be an option...
//but it is not ideal, given that the directory structure becomes very complex
//and cannot be specified as a single java classpath entry.
//We will use xerial.sbt-pack instead.
//   retrieveManaged := true

//Assembly of dependency artifacts using sbt-assembly or proguard was
//also considered, however, the fat *.jar files created are simply
//too fat, and the assembly takes too much time
//   assemblyJarName in assembly := "nounou.jar"
//
//   test in assembly := {}

packAutoSettings

packTargetDir := file("artifacts")

//packCopyDependenciesTarget := file("artifacts/dependencies")

packArchive := Seq( /*packArchiveTgz.value,*/  packArchiveZip.value )


//publishArtifact in (Compile, packageBin) := true

//publishArtifact in (Compile, packageDoc) := true

//publishArtifact in (Compile, packageSrc) := true


//assemblyOption in assembly :=
//  (assemblyOption in assembly).value.copy(includeScala = false, includeDependency = false)

//libraryDependencies ++= Seq(
//    "com.atlassian.labs" % "sbt-git-stamp" % "0.1.2"
//)


//settings = standardSettings ++ SbtOneJar.oneJarSettings

//unmanagedBase <<= baseDirectory {base => base/"lib"}