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
  @inline // TODO: F should be subtype of js.Object - fix this in scalajs-react
  def connect[S, A, P, C <: js.Any, F[_], FP <: js.Any](
    connector: Connector[S, A, P]
  )(cls: C)(implicit F: JsObjectWrapper[F, P], FP: F[P] =:= FP): C =
    BaseImpl.connectImpl[S, A, P, C, F, FP](connector)(cls)

  /**
   * React-redux Provider class.
   */
  def Provider: js.Any = BaseImpl.Provider
}
