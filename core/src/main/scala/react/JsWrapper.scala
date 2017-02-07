package eldis.redux.react

import scala.scalajs.js

/**
 * A typeclass, representing a strategy for passing props to React components.
 *
 * This is required since JS libraries expect props and state to be
 * plain JS objects.
 *
 * Each scalajs react wrapper has its own implementation of this pattern.
 * Additionally, in some cases wrapping is not required (for example,
 * when connecting a native component).
 *
 * Unless you're implementing a connector for a Scala React wrapper,
 * you probably don't need to use this.
 */
trait JsWrapper[F[_]] {
  def wrap[A](a: A): F[A]
}

object JsWrapper {
  def apply[F[_]](implicit F: JsWrapper[F]) = F
}
