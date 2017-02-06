import ScalaJSRedux._

scalaVersion in ThisBuild := Versions.scala

lazy val core = Projects.core
lazy val japgolly = Projects.japgolly

lazy val exReact = Projects.exReact
