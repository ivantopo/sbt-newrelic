sbtPlugin := true

version := "0.0.1"

organization := "com.ivantopo.sbt"

name := "sbt-newrelic"

publishTo := Some(Resolver.sftp("Kamon Repository", "repo.kamon.io", "/var/local/releases-repo"))