package examples.eldiscomponents

import scala.scalajs.js
import scala.scalajs.js.JSApp
import eldis.react._
import vdom.prefix_<^._
import org.scalajs.dom

import eldis.redux
import redux._
import redux.react.eldis._

object Main extends JSApp {
  val App = FunctionalComponent[Store[State, Action]] { (store: Store[State, Action]) =>
    Provider(store)(
      <.div()(
        <.ul()(
          <.li()(Functional()),
          <.li()(FunctionalWithChildren(
            js.Array(
              <.p()("  Child 1 OK"),
              <.p()("  Child 2 OK")
            )
          )),
          <.li()(NativeFunctional()),
          <.li()(
            NativeFunctionalWithChildren(
              js.Array[ReactNode](
                <.p()("  Child 1 OK"),
                <.p()("  Child 2 OK")
              )
            )
          ),
          <.li()(JS(
            <.p()("  Child 1 OK"),
            <.p()("  Child 2 OK")
          ))
        )
      )
    )
  }

  override def main(): Unit =
    ReactDOM.render(
      App(Store(State(
        ScalaProps("Functional OK"),
        ScalaProps("Functional with children OK"),
        JSProps("Native functional OK"),
        JSProps("Native functional with children OK"),
        JSProps("JS OK")
      ))),
      dom.document.getElementById("root")
    )
}
