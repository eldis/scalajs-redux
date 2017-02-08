package eldis.redux.react

import scala.scalajs.js

/**
 * Library-agnostic react redux facade.
 *
 * This API is only intended for implementing connectors to React libraries.
 * You should probably use one of those instead.
 */
package object base {

  /**
   * Maps state, dispatcher function and own properties to the component's
   * properties.
   */
  type Connector[-S, A, +P, -OP] = BaseImpl.Connector[S, A, P, OP]

  /**
   * Creates the connected to state component factory.
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param cls        The component's class
   */
  @inline // TODO: F should be subtype of js.Object - fix this in scalajs-react
  def connect[S, A, R, P <: R, OP <: R, C[_ <: R] <: js.Any, F[_]: JsWrapper, FP <: js.Any, FOP <: js.Any](
    connector: Connector[S, A, P, OP]
  )(cls: C[P])(implicit FP: F[P] =:= FP, FOP: F[OP] =:= FOP): C[OP] =
    BaseImpl.connectImpl[S, A, R, P, OP, C, F, FP, FOP](connector)(cls)

  /**
   * React-redux Provider class.
   */
  def Provider: js.Any = BaseImpl.Provider
}
