package eldis.redux.react

import _root_.japgolly.scalajs.react._
import org.scalajs.dom.raw.Element

import eldis.redux

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
  val Provider = JapgollyImpl.Provider

  type ProviderProps = JapgollyImpl.ProviderProps
  val ProviderProps = JapgollyImpl.ProviderProps

  /** The function that maps the state and the dispatcher function to the component's properties */
  type Connector[S, A, P, OP] = base.Connector[S, A, P, OP]

  type ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] = JapgollyImpl.ConnectedComponentFactory[Props, State, Backend, Node]

  /**
   * Creates the connected to state component factory.
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param cls        The component's class
   */
  @inline def connect[S, A, P, OP, S1, B](connector: redux.Dispatcher[A] => (S, OP) => P, cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[OP, S1, B, Element] =
    JapgollyImpl.connect(connector, cls)

  /**
   * Connects stateless component to the global state
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param comp       The component
   */
  @inline def connect[C, P, OP](connector: C, comp: FunctionalComponent[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): FunctionalComponent[OP] =
    JapgollyImpl.connect(C(connector), comp)

  /**
   * Connects stateless component with children to the global state
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param comp       The component
   */
  @inline def connect[C, P, OP](connector: C, comp: FunctionalComponent.WithChildren[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): FunctionalComponent.WithChildren[OP] =
    JapgollyImpl.connect(C(connector), comp)
}
