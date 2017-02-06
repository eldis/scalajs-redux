package eldis

import scalajs.js

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
