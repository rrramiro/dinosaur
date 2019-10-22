import scala.sys.process._

val dockerBuild = taskKey[Unit]("docker build")

val dinosaur = (project in file("dinosaur"))

val root = (project in file(".")).settings(
  dockerBuild := {
    "docker build . -t dinosaur" !
  },
  test in Test := (test in Test).dependsOn(dockerBuild).value,
  libraryDependencies ++= Seq(
    "com.softwaremill.sttp" %% "core" % "1.6.4" % "test",
    "com.softwaremill.sttp" %% "async-http-client-backend-future" % "1.6.4"  % "test",
    "com.whisk" %% "docker-testkit-scalatest" % "0.9.9" % "test",
    "com.whisk" %% "docker-testkit-impl-docker-java" % "0.9.9" % "test",
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"
  )
)
