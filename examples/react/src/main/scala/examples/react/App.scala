package eldis.redux.examples.react

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import eldis.redux._
import scala.concurrent._

object App {

  val component = ReactComponentB[Store[State, Action]]("App")
    .render { scope =>
      {
        react.Provider(scope.props)(
          <.div()(
            <.p()("Type some text to filter the list:"),
            Filter(),
            List()
          )
        )
      }
    }
    .build

  def apply(store: Store[State, Action]) = component(store)

}

object Filter {

  case class Props(
    value: String = "",
    onChange: Option[String => Unit] = None
  )

  val component = ReactComponentB[Props]("Filter")
    .render { scope =>
      {
        def onChange(e: ReactEventI): Option[Callback] =
          scope.props.onChange.map { cb =>
            Callback {
              cb(e.target.value)
            }
          }
        <.div()(
          <.input(
            ^.`type` := "text",
            ^.value := scope.props.value,
            ^.onChange ==>? onChange
          )
        )
      }
    }
    .build

  val connected = react.connect(
    (dispatch: Dispatcher[Action]) => {
      val onChange = (v: String) => {
        val p = Promise[Action]()
        val f = p.future
        println("Hello, world!")
        dispatch(f)
        val _ = p.success(ChangeFilter(v))
      }
      (state: State) => Props(
        value = state.filter,
        onChange = Some(onChange)
      )
    }
  )(component.reactClass)

  def apply() = connected(Props(""))

}

object List {

  case class Props(
    elements: Seq[String]
  )

  val component = ReactComponentB[Props]("List")
    .render { scope =>
      <.ul()(
        scope.props.elements.map(el => <.li()(el))
      )
    }
    .build

  val connected = react.connect(
    (dispatch: Dispatcher[Action]) => (state: State) => Props(
      elements = state.filteredElements
    )
  )(component.reactClass)

  def apply() = connected(Props(Nil))

}
