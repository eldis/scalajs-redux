package eldis.redux.react

import scala.scalajs.js

/**
 * Library-agnostic react redux facade.
 *
 * This is a library-agnostic wrapper over react-redux. You probably
 * want `eldis.redux.react.japgolly` instead.
 */
package object base {

  /** The function that maps the state and the dispatcher function to the component's properties */
  type Connector[S, A, P] = BaseImpl.Connector[S, A, P]

  /**
   * Creates the connected to state component factory.
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param cls        The component's class
   */
  @inline
  def connect[S, A, P, C <: js.Any, F[_] <: js.Object: JsObjectWrapper](
    connector: Connector[S, A, P]
  )(cls: C): C =
    BaseImpl.connectImpl[S, A, P, C, F](connector)(cls)

  /**
   * React-redux Provider class.
   */
  def Provider: js.Any = BaseImpl.Provider
}
