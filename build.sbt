name := "nounous"

scalaVersion := "2.11.2"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.7" % "test",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-api" % "2.1.2",
  //"com.github.fommil.netlib" % "core" % "1.1.2",
  //"org.scalafx" % "scalafx_2.11" % "8.0.0-R4",
  "org.scalanlp" %% "breeze" % "0.8.1",
  "org.scalanlp" %% "breeze-natives" % "0.8.1",
  "org.scalanlp" %% "breeze-macros" % "0.3.1",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.7.1.201504261725-r",
  "commons-io" % "commons-io" % "2.4"
)

unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

resolvers ++= Seq(
//  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
//  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/" ,
  //"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases")
    )
