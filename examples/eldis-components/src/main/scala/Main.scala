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
          <.li()(Functional(ScalaProps("Functional Failed"))),
          <.li()(FunctionalWithChildren(
            ScalaProps("Functional with children Failed"),
            js.Array(
              <.p()("&nbsp;&nbsp;Child1 OK"),
              <.p()("&nbsp;&nbsp;Child2 OK")
            )
          )),
          <.li()(NativeFunctional(JSProps("Native functional Failed"))),
          <.li()(NativeFunctionalWithChildren(JSProps("Native functional with children Failed"), js.Array[ReactNode](
            <.p()("&nbsp;&nbsp;Child OK")
          ))),
          <.li()(JS(
            JSProps("JS Failed"),
            <.p()("&nbsp;&nbsp; Child OK")
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
