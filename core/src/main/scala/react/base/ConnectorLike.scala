package eldis.redux.react.base

import eldis.redux

/**
 * A typeclass for connector functions.
 *
 * This is necessary to make inference work. It also provides a better
 * syntax.
 *
 * Supported forms:
 *
 * ```
 * (Dispatcher) => (State, OwnProps) => Props
 * (State, OwnProps) => Props
 * State => Props
 * ```
 */
trait ConnectorLike[C, P, -OP] {

  type State
  type Action

  def apply(c: C): Connector[State, Action, P, OP]
}

object ConnectorLike {

  def apply[C, S, A, P, OP](f: Function1[C, Connector[S, A, P, OP]]) =
    new ConnectorLike[C, P, OP] {
      type State = S
      type Action = A
      def apply(c: C) = f(c)
    }

  implicit def fullConnectorLike[S, A, P, OP] =
    ConnectorLike[Function1[redux.Dispatcher[A], Function2[S, OP, P]], S, A, P, OP](
      identity
    )

  implicit def viewConnectorLike[S, P, OP] =
    ConnectorLike[Function2[S, OP, P], S, Nothing, P, OP](
      f => _ => f
    )

  implicit def noOwnViewConnectorLike[S, P, OP] =
    ConnectorLike[Function1[S, P], S, Nothing, P, OP](
      f => _ => (s: S, _: OP) => f(s)
    )
}
