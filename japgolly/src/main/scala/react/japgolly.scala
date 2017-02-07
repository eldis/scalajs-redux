package eldis.redux.react

import _root_.japgolly.scalajs.react._
import org.scalajs.dom.raw.Element

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
  type Connector[S, A, P] = base.Connector[S, A, P]

  type ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] = JapgollyImpl.ConnectedComponentFactory[Props, State, Backend, Node]

  /**
   * Creates the connected to state component factory.
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param cls        The component's class
   */
  @inline def connect[S, A, P, S1, B](connector: Connector[S, A, P], cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[P, S1, B, Element] =
    JapgollyImpl.connect(connector, cls)

  /**
   * Connects stateless component to the global state
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param comp       The component
   */
  @inline def connect[S, A, P](connector: Connector[S, A, P], comp: FunctionalComponent[P]): FunctionalComponent[P] =
    JapgollyImpl.connect(connector, comp)

  /**
   * Connects stateless component with children to the global state
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param comp       The component
   */
  @inline def connect[S, A, P](connector: Connector[S, A, P], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[P] =
    JapgollyImpl.connect(connector, comp)

}
