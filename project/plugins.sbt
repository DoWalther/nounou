resolvers ++= Seq(
  Resolver.url("typesafe-ivy-releases-for-online-crossbuild", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns),
  Resolver.url("typesafe-ivy-snapshots-for-online-crossbuild", url("http://repo.typesafe.com/typesafe/ivy-snapshots/"))(Resolver.defaultIvyPatterns),
  Resolver.url("typesafe-repository-for-online-crossbuild", url("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Resolver.defaultIvyPatterns),
  Resolver.url("Sonatype snapshots", url("https://oss.sonatype.org/content/repositories/snapshots/"))(Resolver.defaultIvyPatterns),
  Resolver.url("sonatype-releases", url("https://oss.sonatype.org/content/repositories/releases/"))(Resolver.defaultIvyPatterns)
)

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.1")

//addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

//addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")
