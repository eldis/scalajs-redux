package examples.eldiscomponents

import scala.scalajs.js
import js.annotation._
import eldis.react._
import vdom.prefix_<^._

import eldis.redux
import redux._
import redux.react.eldis._

object Functional {
  val component = FunctionalComponent { props: ScalaProps =>
    <.div()(props.value)
  }

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State) => state.functional,
    component
  )

  def apply(props: ScalaProps) = connected(props)
}

object FunctionalWithChildren {
  val component = FunctionalComponent.withChildren {
    (props: ScalaProps, children: PropsChildren) =>
      {
        <.div()((
          <.div()(props.value) +:
          children
        ): _*)
      }
  }

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State) => state.functionalWithChildren,
    component
  )

  def apply(props: ScalaProps, children: PropsChildren) = connected(props, children)
}

object NativeFunctional {
  val component = NativeFunctionalComponent { props: JSProps =>
    <.div()(props.jsValue)
  }

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State) => state.nativeFunctional,
    component
  )

  def apply(props: JSProps) = connected(props)
}

object NativeFunctionalWithChildren {
  val component = NativeFunctionalComponent.withChildren {
    (props: JSProps, children: PropsChildren) =>
      {
        println(children.size + " children")

        <.div()((
          <.div()(props.jsValue) +:
          children
        ): _*)
      }
  }

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State) => state.nativeFunctionalWithChildren,
    component
  )

  def apply(props: JSProps, children: ReactNode*) = connected(props, js.Array(children: _*))
}

object JS {

  @JSImport("JsComponent", JSImport.Namespace)
  @js.native
  object component extends JSComponent[JSProps]

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State) => state.js,
    component
  )

  def apply(props: JSProps, children: ReactNode*) =
    React.createElement(connected, props, js.Array(children: _*))
}
