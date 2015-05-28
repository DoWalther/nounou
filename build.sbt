name := "nounou"

scalaVersion := "2.11.6"

publishMavenStyle := true

//publishArtifact in (Compile, packageBin) := true

//publishArtifact in (Compile, packageDoc) := true

//publishArtifact in (Compile, packageSrc) := true

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.7" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.scalanlp" %% "breeze" % "0.11.2",
  "org.scalanlp" %% "breeze-natives" % "0.11.2",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.7.1.201504261725-r",
  "commons-io" % "commons-io" % "2.4"
)
//  "org.scalanlp" %% "breeze-macros" % "0.3.1",
//   unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

resolvers ++= Seq(
    Resolver.mavenLocal,
//    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
    )

assemblyJarName in assembly := "nounou.jar"

test in assembly := {}

//settings = standardSettings ++ SbtOneJar.oneJarSettings