package eldis.redux.react

import org.scalajs.dom.raw.Element
import scala.scalajs.js

import _root_.japgolly.scalajs.react._

import eldis.redux
import base.JsWrapper

/**
 * A wrapper over react-redux, compatible with japgolly's scalajs-react
 * package.
 */
package object japgolly {

  /**
   * The react component that provides the store injection in to the virtual DOM.
   *
   * See [[https://github.com/reactjs/react-redux/blob/master/docs/api.md#provider-store the react-redux documentation]]
   * for detailed description.
   */
  object Provider {

    def apply[S, A](store: redux.Store[S, A])(child: ReactNode) = {
      val provider = base.Provider.asInstanceOf[JsComponentType[ProviderProps, js.Any, Element]]
      React.createFactory(provider)(
        ProviderProps(store), child
      )
    }
  }

  /** The properties of Provider component. */
  @js.native
  trait ProviderProps extends js.Object {
    val store: js.Any = js.native
  }

  object ProviderProps {
    def apply(store: js.Any): ProviderProps =
      js.Dynamic.literal(
        store = store
      ).asInstanceOf[ProviderProps]
  }

  /** The factory that produces component's connected to the store. */
  trait ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] {
    def apply(props: Props, children: ReactNode*): ReactComponentU[Props, State, Backend, Node]
  }

  /**
   * Creates the connected to state component factory.
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param cls        The component's class
   */
  @inline def connect[C, P, OP, S1, B](
    connector: C, cls: ReactClass[P, S1, B, Element]
  )(implicit C: base.ConnectorLike[C, P, OP]): ConnectedComponentFactory[OP, S1, B, Element] =

    new ConnectedComponentFactory[OP, S1, B, Element] {
      def apply(props: OP, children: ReactNode*) = {
        React.createFactory(
          base.connect[C.State, C.Action, Any, P, OP, ReactClass[?, S1, B, Element], WrapObj, WrapObj[P], WrapObj[OP]](
            C(connector)
          )(cls)
        )(WrapObj(props), children)
      }
    }

  /**
   * Connects stateless component to the global state
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param comp       The component
   */
  @inline def connect[C, P, OP](connector: C, comp: FunctionalComponent[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): FunctionalComponent[OP] =
    base.connect(C(connector))(comp)

  /**
   * Connects stateless component with children to the global state
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param comp       The component
   */
  @inline def connect[C, P, OP](connector: C, comp: FunctionalComponent.WithChildren[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): FunctionalComponent.WithChildren[OP] =
    base.connect(C(connector))(comp)

  private implicit val wrapperInstance =
    new JsWrapper[WrapObj] {
      override def wrap[A](a: A) = WrapObj(a)
      override def unwrap[A](fa: WrapObj[A]) = fa.v
    }
}
