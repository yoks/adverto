import sbt.Keys._

name := "adverto"

version := "1.0"

organization in ThisBuild := "pw.anisimov.adverto"

val akkaVersion = "2.4.0"
val akkaStreamsVersion = "1.0"
val scalaLangVersion = "2.11.7"

scalaVersion := scalaLangVersion

libraryDependencies ++= Seq(
  // Akka Dependencies
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,

  // LevelDB
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",

  // Reactive Stream Dependencies
  "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamsVersion,

  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "org.slf4j" % "slf4j-log4j12" % "1.7.12",

  // Test Dependencies
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamsVersion % "test",
  "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaStreamsVersion % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.1.1" % "test"
)

bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/adverto.conf""""
batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Dconfig.file=%SUNSET_HOME%\\conf\\adverto.conf"""

enablePlugins(JavaAppPackaging)

fork := true