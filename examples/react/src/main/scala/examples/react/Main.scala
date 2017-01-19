package eldis.redux.examples.react

import scala.scalajs.js
import js.annotation._
import scala.scalajs.js.JSApp
import japgolly.scalajs.react._
import org.scalajs.dom

object Main extends JSApp {

  /**
   * Scalajs-react searches its dependencies in the global namespace,
   * so we must provide them to it.
   */
  private object Dependencies {

    @JSImport("react", JSImport.Namespace)
    @js.native
    object React extends js.Object {}

    @JSImport("react-dom", JSImport.Namespace)
    @js.native
    object ReactDOM extends js.Object {}

    def setup = {
      js.Dynamic.global.React = React
      js.Dynamic.global.ReactDOM = ReactDOM
    }
  }

  def main(): Unit = {
    Dependencies.setup

    ReactDOM.render(
      App(
        Store(
          Seq(
            "Red Hat",
            "Canonical",
            "Apple",
            "Microsoft"
          )
        )
      ),
      dom.document.getElementById("root")
    )
  }

}
