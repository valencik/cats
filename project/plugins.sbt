val sbtTypelevelVersion = "0.7.2"
addSbtPlugin("org.typelevel" % "sbt-typelevel" % sbtTypelevelVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % sbtTypelevelVersion)
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.7")
addSbtPlugin("com.github.tkawachi" % "sbt-doctest" % "0.10.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.5")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.12.0")

resolvers +=
  "Sonatype OSS Snapshots".at("https://s01.oss.sonatype.org/content/repositories/snapshots")
addSbtPlugin("pink.cozydev" % "protosearch-sbt" % "0.0-c102b5d-SNAPSHOT")
