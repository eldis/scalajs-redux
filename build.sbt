import ScalaJSRedux._

scalaVersion in ThisBuild := Versions.scala

lazy val root = Projects.root

lazy val core = Projects.core
lazy val japgolly = Projects.japgolly
lazy val eldis = Projects.eldis

lazy val exJapgolly = Projects.exJapgolly
lazy val exEldis = Projects.exEldis
