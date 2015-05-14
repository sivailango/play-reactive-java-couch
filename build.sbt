name := "play-reactive-java-couch"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "com.couchbase.client" % "couchbase-client" % "1.4.2",
  "com.google.code.gson" % "gson" % "2.3.1",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "jquery" % "1.9.1"
)
