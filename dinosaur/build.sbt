
name := "dinosaur"

version := "0.1.0"

organization := "io.whaling"

licenses += ("WTFPL", url("http://www.wtfpl.net/txt/copying/"))

nativeLinkStubs := true

enablePlugins(ScalaNativePlugin)

scalaVersion := "2.11.12"

scalacOptions ++= Seq("-feature")

nativeMode := "release"

nativeGC := "immix"
//nativeLinkingOptions += "-static -lrt -lunwind -lunwind-x86_64 -lgc"

scalafmtOnCompile in ThisBuild := true

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.0-SNAP10" % "test"
