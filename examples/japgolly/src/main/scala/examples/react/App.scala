package eldis.redux.examples.japgolly

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import eldis.redux._
import eldis.redux.react.{ japgolly => react }
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

  val connected = {
    val connector: react.Connector[State, Action, Props, Unit] =
      (dispatch: Dispatcher[Action]) => {
        val onChange = (v: String) => {
          val p = Promise[Action]()
          val f = p.future
          println("Hello, world!")
          dispatch(f)
          val _ = p.success(ChangeFilter(v))
        }
        (state: State, ownProps: Unit) => Props(
          value = state.filter,
          onChange = Some(onChange)
        )
      }
    react.connect(
      connector,
      component.reactClass
    )
  }

  def apply() = connected(())

}

object List {

  case class Props(
    elements: Seq[String]
  )

  val component = FunctionalComponent[Props] { props =>
    <.ul()(
      props.elements.map(el => <.li()(el))
    )
  }

  val connected = {
    react.connect[State, Action, Props, Unit](
      (dispatch: Dispatcher[Action]) => (state: State, ownProps: Unit) => Props(
        elements = state.filteredElements
      ),
      component
    )
  }

  def apply() = connected(())

}
