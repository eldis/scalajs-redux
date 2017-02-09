package eldis.redux.examples.react

import scala.scalajs.js
import js.annotation._
import scala.scalajs.js.JSApp
import eldis.react._
import org.scalajs.dom

object Main extends JSApp {

  def main(): Unit = {
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
