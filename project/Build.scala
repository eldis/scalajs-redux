import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object ScalaJSRedux {

  object Versions {
    val scala = "2.11.8"
    val japgollyReact = "0.11.3"
    val eldisReact = "0.1.0-SNAPSHOT"

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
    lazy val japgollyReact = "com.github.japgolly.scalajs-react" %%%! "core" % Versions.japgollyReact
    lazy val eldisReact = "com.github.eldis" %%%! "scalajs-react" % Versions.eldisReact

    lazy val scalatest = "org.scalatest" %%%! "scalatest" % Versions.scalatest % "test"

    lazy val jsReactRedux = Seq(
      "react" -> JsVersions.react,
      "react-dom" -> JsVersions.react,
      "redux" -> JsVersions.redux,
      "react-redux" -> JsVersions.reactRedux
    )
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
      .settings(
        requiresDOM in Test := true
      )

    def jsBundler: PC =
      _.enablePlugins(ScalaJSBundlerPlugin)
      .settings(
        enableReloadWorkflow := false,
        libraryDependencies += Dependencies.scalatest,
        npmDevDependencies in Test ++= Seq(
          "redux" -> JsVersions.redux
        )
      )

    def japgollyReact(dev: Boolean = false): PC =
      _.settings(
        libraryDependencies += Dependencies.japgollyReact,
        if(dev)
          npmDevDependencies in Compile ++= Dependencies.jsReactRedux
        else
          npmDependencies in Compile ++= Dependencies.jsReactRedux
      )

    def eldisReact(dev: Boolean = false): PC =
      _.settings(
        libraryDependencies += Dependencies.eldisReact,
        if(dev)
          npmDevDependencies in Compile ++= Dependencies.jsReactRedux
        else
          npmDependencies in Compile ++= Dependencies.jsReactRedux
      )

    def exampleProject(prjName: String, useReact: Boolean = false): PC = { p: Project =>
      p.in(file("examples") / prjName)
        .configure(scalajsProject, jsBundler)
        .settings(
          name := prjName,

          npmDevDependencies in Compile ++= Seq(
            "html-webpack-plugin" -> JsVersions.htmlWebpackPlugin,
            "html-loader" -> JsVersions.htmlLoader
          ),

          webpackConfigFile in fastOptJS := Some(baseDirectory.value / "config" / "webpack.config.js"),
          webpackConfigFile in fullOptJS := Some(baseDirectory.value / "config" / "webpack.config.js")
        )
      } compose { pc =>
        if(useReact)
          pc.configure(japgollyReact())
        else
          pc
      }

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
    lazy val core = project.in(file("core"))
      .configure(
        Settings.scalajsProject, Settings.jsBundler, Settings.publish
      )
      .settings(
        name := "scalajs-redux-core"
      )

    lazy val japgolly = project.in(file("japgolly"))
      .configure(
        Settings.scalajsProject, Settings.jsBundler, Settings.publish, Settings.japgollyReact(true)
      )
      .settings(
        name := "scalajs-redux-react-japgolly"
      )
      .dependsOn(core)

    lazy val eldis = project.in(file("eldis"))
      .configure(
        Settings.scalajsProject, Settings.jsBundler, Settings.publish, Settings.eldisReact(true)
      )
      .settings(
        name := "scalajs-redux-react-eldis"
      )
      .dependsOn(core)

    lazy val exReact = project
      .configure(
        Settings.exampleProject(
          "react",
          useReact = true)
      )
      .dependsOn(japgolly)

    lazy val exEldis = project
      .configure(
        Settings.exampleProject(
          "eldis",
          useReact = true)
      )
      .dependsOn(eldis)
  }

}
