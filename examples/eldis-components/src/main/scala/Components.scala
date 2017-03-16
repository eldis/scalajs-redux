package examples.eldiscomponents

import scala.scalajs.js
import js.annotation._
import js.JSConverters._
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
    // All parameters
    (_: Dispatcher[Action]) => (state: State, ownProps: Unit) => state.functional,
    component
  )

  def apply() = connected(())
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
    // No dispatcher
    (state: State, ownProps: Unit) => state.functionalWithChildren,
    component
  )

  def apply(children: PropsChildren) = connected((), children)
}

object NativeFunctional {
  val component = NativeFunctionalComponent { props: JSProps =>
    <.div()(props.jsValue)
  }

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State) => state.nativeFunctional,
    component
  )

  def apply() = connected(())
}

object NativeFunctionalWithChildren {
  val component = NativeFunctionalComponent.withChildren {
    (props: JSProps, children: PropsChildren) =>
      <.div()((
        <.div()(props.jsValue) +:
        children
      ): _*)
  }

  val connected = connect(
    (_: Dispatcher[Action]) => (state: State, ownProps: js.Any) => state.nativeFunctionalWithChildren,
    component
  )

  def apply(children: ReactNode*) = connected((), js.Array(children: _*))
}

object JS {

  @JSImport("JsComponent", JSImport.Namespace)
  @js.native
  object component extends JSComponent[JSProps]

  val connected = connect(
    (state: State) => state.js,
    component
  )

  def apply(children: ReactNode*) =
    React.createElement(connected, js.Object(): js.Any, children)
}
