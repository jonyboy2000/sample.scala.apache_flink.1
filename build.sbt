resolvers in ThisBuild ++= Seq("Apache Repository" at "https://repository.apache.org/content/repositories/releases/",
  Resolver.mavenLocal)

name := "PositionService"

version := "1.1-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"

libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

mainClass in assembly := Some("com.aqr.position.MainRunner")

val flinkVersion = "1.0.3"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion)

libraryDependencies ++= flinkDependencies

run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run))

publishArtifact in Test := true

