package eldis.redux.examples.react

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import eldis.redux.{ Redux, ReactRedux }

object App {

  val component = ReactComponentB[Redux.Store[State, Action]]("App")
    .render { scope =>
      {
        ReactRedux.Provider(scope.props)(
          <.div()(
            <.p()("Type some text to filter the list:"),
            Filter(Filter.Props("hi!")),
            List(List.Props(Seq("a", "b", "c")))
          )
        )
      }
    }
    .build

  def apply(store: Redux.Store[State, Action]) = component(store)

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

  val connected = ReactRedux.createFactory(
    (state: State, dispatch: Redux.Dispatcher[Action]) => Props(
      value = state.filter,
      onChange = Some((v: String) => dispatch(ChangeFilter(v)))
    )
  )(component.reactClass)

  def apply(props: Props) = connected(props)

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

  val connected = ReactRedux.createFactory(
    (state: State, dispatch: Redux.Dispatcher[Action]) => Props(
      elements = state.filteredElements
    )
  )(component.reactClass)

  def apply(props: Props) = connected(props)

}
