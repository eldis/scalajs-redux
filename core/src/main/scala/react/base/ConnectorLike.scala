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
 * Dispatcher => (State, OwnProps) => Props
 * Dispatcher => State => Props
 * Dispatcher => Props
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

  implicit def noDispatcherConnectorLike[S, P, OP] =
    ConnectorLike[Function2[S, OP, P], S, Nothing, P, OP](
      f => _ => f
    )

  // We can't make OP a Unit, for example, since some implementations
  // require it to be a js.Any. So we leave it free.
  implicit def noOwnConnectorLike[S, A, P, OP] =
    ConnectorLike[Function1[redux.Dispatcher[A], Function1[S, P]], S, A, P, OP](
      f => d => {
        val g = f(d)
        (s, _) => g(s)
      }
    )

  implicit def stateOnlyConnectorLike[S, P, OP] =
    ConnectorLike[Function1[S, P], S, Nothing, P, OP](
      f => _ => (s, _) => f(s)
    )

  implicit def dispatcherOnlyConnectorLike[S, A, P, OP] =
    ConnectorLike[Function1[redux.Dispatcher[A], P], S, A, P, OP](
      f => d => {
        val result = f(d)
        (_, _) => result
      }
    )
}
