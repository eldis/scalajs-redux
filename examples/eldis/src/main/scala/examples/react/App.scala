package eldis.redux.examples.react

import eldis.react._
import vdom._
import vdom.prefix_<^._
import eldis.redux._
import eldis.redux.react.{ eldis => react }
import scala.concurrent._

object App {

  val component = FunctionalComponent[Store[State, Action]]("App") { store =>
    react.Provider(store)(
      <.div()(
        <.p()("Type some text to filter the list:"),
        Filter(),
        List()
      )
    )
  }

  def apply(store: Store[State, Action]) = component(store)

}

object Filter {

  case class Props(
    value: String = "",
    onChange: Option[String => Unit] = None
  )

  val component = FunctionalComponent[Props]("Filter") { props =>
    <.div()(
      <.input(
        ^.`type` := "text",
        ^.value := props.value,
        props.onChange.isDefined ?= (^.onChange ==> {
          (e: ReactEventI) => props.onChange.get.apply(e.target.value)
        })
      )()
    )
  }

  val connected = react.connect(
    (dispatch: Dispatcher[Action]) => {
      val onChange = (v: String) => {
        val p = Promise[Action]()
        val f = p.future
        dispatch(f)
        val _ = p.success(ChangeFilter(v))
      }
      (state: State, ownProps: Unit) => Props(
        value = state.filter,
        onChange = Some(onChange)
      )
    },
    component
  )

  def apply() = connected(())

}

object List {

  case class Props(
    elements: Seq[String]
  )

  val component = FunctionalComponent[Props] { (props: Props) =>
    <.ul()(
      props.elements.map(
        el => <.li()(el)
      ): _*
    )
  }

  val connected = react.connect(
    (state: State) => Props(
      elements = state.filteredElements
    ), component
  )

  def apply() = connected(Props(Nil))

}
