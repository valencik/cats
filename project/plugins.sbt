val sbtTypelevelVersion = "0.7.4"
addSbtPlugin("org.typelevel" % "sbt-typelevel" % sbtTypelevelVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % sbtTypelevelVersion)
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.7")
addSbtPlugin("io.github.sbt-doctest" % "sbt-doctest" % "0.11.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.5")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.12.0")

libraryDependencySchemes += "com.lihaoyi" %% "geny" % VersionScheme.Always

resolvers +=
  "Sonatype OSS Snapshots".at("https://s01.oss.sonatype.org/content/repositories/snapshots")
addSbtPlugin("pink.cozydev" % "protosearch-sbt" % "0.0-ba9d057-SNAPSHOT")
