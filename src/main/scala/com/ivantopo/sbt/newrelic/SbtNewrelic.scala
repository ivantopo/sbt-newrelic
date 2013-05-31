package com.ivantopo.sbt.newrelic

import sbt._
import sbt.Keys._

object SbtNewrelic extends Plugin {

  val newrelic = config("newrelic")
  lazy val newrelicSettings: Seq[Setting[_]] = inConfig(newrelic)(defaultNewrelicSettings) ++ newrelicDependencies

  object SbtNewrelicKeys {
    val newrelicVersion = SettingKey[String]("newrelic-version", "New Relic version to be used.")
    val agent = TaskKey[File]("newrelic-agent", "Location of the newrelic-agent jar to be used.")
    val jvmOptions = TaskKey[Seq[String]]("newrelic-jvmoptions", "JVM options when using newrelic.")
    val environment = SettingKey[String]("newrelic-environment", "Selected NewRelic environment from newrelic.yml")
    val configFile = SettingKey[File]("newrelic-configfile", "Location of the newrelic.yml configuration file expected by New Relic.")
  }

  import SbtNewrelicKeys._
  lazy val defaultNewrelicSettings = seq(
    newrelicVersion   :=  "2.18.0",
              agent  <<=  findAgentFile,
         jvmOptions  <<=  createJvmOptions,
        environment   :=  "development",
         configFile  <<=  (resourceDirectory in Compile) / "newrelic.yml"
  )

  lazy val newrelicDependencies = seq(
    ivyConfigurations += newrelic,
    libraryDependencies <+= (newrelicVersion in newrelic) { v =>
      "com.newrelic.agent.java" % "newrelic-agent" % v
    })

  def findAgentFile = (update) map { report: UpdateReport =>
    report.matching(moduleFilter(organization = "com.newrelic.agent.java", name = "newrelic-agent")).head
  }

  def createJvmOptions = (agent, environment, configFile) map { (agentFile: File, env: String, cfgFile: File) =>
    Seq(
      "-javaagent:" + agentFile,
      "-Dnewrelic.environment=" + env,
      "-Dnewrelic.config.file=" + cfgFile
    )
  }
}
