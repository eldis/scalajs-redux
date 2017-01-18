import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object ScalaJSRedux {

  object Versions {
    val scala = "2.11.8"
    val scalaJsReact = "0.11.3"

    val scalatest = "3.0.1"
  }

  object JsVersions {
    val htmlWebpackPlugin = "~2.26.0"
    val htmlLoader = "~0.4.3"

    val react = "~15.4.2"
    val redux = "~3.6.0"
    val reactRedux = "~5.0.2"
  }

  object Dependencies {
    lazy val scalaJsReact = "com.github.japgolly.scalajs-react" %%%! "core" % Versions.scalaJsReact

    lazy val scalatest = "org.scalatest" %%%! "scalatest" % Versions.scalatest % "test"
  }

  object Settings {
    type PC = Project => Project

    def commonProject: PC =
      _.settings(
        scalaVersion := Versions.scala,
        organization := "com.github.eldis"
      )

    def scalajsProject: PC =
      _.configure(commonProject)
      .enablePlugins(ScalaJSPlugin)

    def jsBundler: PC =
      _.enablePlugins(ScalaJSBundlerPlugin)
        .settings(
        enableReloadWorkflow := false
      )

    def publish: PC =
      _.settings(
        publishMavenStyle := true,
        publishTo := {
          val nexus = "https://oss.sonatype.org/"
          if (isSnapshot.value)
            Some("snapshots" at nexus + "content/repositories/snapshots")
          else
            Some("releases"  at nexus + "service/local/staging/deploy/maven2")
        }
      )
  }

  object Projects {
    lazy val scalaJsRedux = project.in(file("."))
      .configure(Settings.scalajsProject, Settings.jsBundler, Settings.publish)
      .settings(
        name := "scalajs-redux",

        requiresDOM in Test := true,

        libraryDependencies ++= Seq(
          Dependencies.scalaJsReact,
          Dependencies.scalatest
        ),

        npmDevDependencies in Test ++= Seq(
          "redux" -> JsVersions.redux
        )
      )
  }

}
