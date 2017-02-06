package eldis

import scalajs.js
import japgolly.scalajs.react._
import org.scalajs.dom
import dom.raw.Element

/**
 * The redux facade object.
 */
package object redux {

  import scala.concurrent.{ ExecutionContext, Future }

  /** The reducer function */
  type Reducer[S, A] = Redux.Reducer[S, A]

  /** The store enhancer */
  type Enhancer[S, A] = Redux.Enhancer[S, A]

  /**
   * The redux store object.
   *
   * See [[http://redux.js.org/docs/api/Store.html the redux documentation]]
   * for detailed description.
   */
  type Store[S, A] = Redux.Store[S, A]

  /** Typed dispatcher function */
  type Dispatcher[A] = Redux.Dispatcher[A]

  /** Raw javascript dispatch function */
  type RawDispatcher = Redux.RawDispatcher

  /** Middleware function argument type */
  type MiddlewareArg[S, A] = Redux.MiddlewareArg[S, A]

  /** Middleware function */
  type Middleware[S, A] = Redux.Middleware[S, A]

  /** Action type wrapped with JS object */
  type WrappedAction = Redux.WrappedAction

  /**
   * Creates the redux store object.
   *
   * See [[http://redux.js.org/docs/api/createStore.html the redux documentation]]
   * for detailed description.
   *
   * @param reducer       The reducer function
   * @param initialState  The initial state of the store
   * @param enhancer      The store enhancer
   */
  @inline def createStore[S, A](
    reducer: Reducer[S, A],
    initialState: js.UndefOr[S] = js.undefined,
    rawReducer: js.UndefOr[js.Function] = js.undefined,
    enhancer: js.UndefOr[Enhancer[S, A]] = js.undefined
  )(implicit ex: ExecutionContext): Store[S, A] =
    Redux.createStore(reducer, initialState, rawReducer, enhancer)

  /**
   * Creates the store enhancer from the middleware functions.
   *
   * See [[http://redux.js.org/docs/api/applyMiddleware.html the redux documentation]]
   * for detailed description.
   */
  @inline def applyMiddleware[S, A] = Redux.applyMiddleware[S, A] _

  @inline def wrapAction[A](a: A) = Redux.wrapAction(a)

  @inline def wrapAction[A](a: Future[A]) = Redux.wrapAction(a)
}

package redux {
  /**
   * React redux facade.
   */
  package object react {

    /**
     * The react component that provides the store injection in to the virtual DOM.
     *
     * See [[https://github.com/reactjs/react-redux/blob/master/docs/api.md#provider-store the react-redux documentation]]
     * for detailed description.
     */
    val Provider = ReactRedux.Japgolly.Provider

    /** The function that maps the state and the dispatcher function to the component's properties */
    type Connector[S, A, P] = ReactRedux.Japgolly.Connector[S, A, P]

    type ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] = ReactRedux.Japgolly.ConnectedComponentFactory[Props, State, Backend, Node]

    /**
     * Creates the connected to state component factory.
     *
     * @param connector  The function that maps state and dispatcher function to component's properties
     * @param cls        The component's class
     */
    @inline def connect[S, A, P, S1, B](connector: Connector[S, A, P], cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[P, S1, B, Element] =
      ReactRedux.Japgolly.connect(connector, cls)

    /**
     * Connects stateless component to the global state
     *
     * @param connector  The function that maps state and dispatcher function to component's properties
     * @param comp       The component
     */
    @inline def connect[S, A, P](connector: Connector[S, A, P], comp: FunctionalComponent[P]): FunctionalComponent[P] =
      ReactRedux.Japgolly.connect(connector, comp)

    /**
     * Connects stateless component with children to the global state
     *
     * @param connector  The function that maps state and dispatcher function to component's properties
     * @param comp       The component
     */
    @inline def connect[S, A, P](connector: Connector[S, A, P], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[P] =
      ReactRedux.Japgolly.connect(connector, comp)
  }
}
