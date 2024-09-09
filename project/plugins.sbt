val sbtTypelevelVersion = "0.7.7"
addSbtPlugin("org.typelevel" % "sbt-typelevel" % sbtTypelevelVersion)
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % sbtTypelevelVersion)
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.7")
addSbtPlugin("io.github.sbt-doctest" % "sbt-doctest" % "0.11.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.18.2")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.6")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

libraryDependencySchemes += "com.lihaoyi" %% "geny" % VersionScheme.Always

resolvers +=
  "Sonatype OSS Snapshots".at("https://s01.oss.sonatype.org/content/repositories/snapshots")
addSbtPlugin("pink.cozydev" % "protosearch-sbt" % "0.0-b662d74-SNAPSHOT")
