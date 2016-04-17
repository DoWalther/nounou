resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases")
)

//resolvers +=
//  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.7.0-SNAPSHOT")

//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.1")

//addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("com.atlassian.labs" % "sbt-git-stamp" % "0.1.2")
