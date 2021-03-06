import com.tuplejump.sbt.yeoman.Yeoman
import com.atlassian.labs.gitstamp.GitStampPlugin._
import play.PlayImport.PlayKeys.playRunHooks

Seq( gitStampSettings: _* )

name := """geotracker-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "JBoss repository" at "https://repository.jboss.org/nexus/content/repositories/"

Yeoman.yeomanSettings

Yeoman.forceGrunt := false

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "mysql" % "mysql-connector-java" % "5.1.32",
  "com.mohiva" %% "play-silhouette" % "2.0",
  "net.codingwell" %% "scala-guice" % "4.0.0-beta5",
  "io.argonaut" %% "argonaut" % "6.0.4",
  "com.jcabi" % "jcabi-manifests" % "1.1",
  jdbc,
  cache,
  filters
)

unmanagedResourceDirectories in Compile <+= baseDirectory( _ / "app" )

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-language:reflectiveCalls"
)
