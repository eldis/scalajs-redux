package eldis.redux.react

import scala.scalajs.js

/**
 * Typeclass for a type constructor that wraps data in a JS object.
 *
 * Each scalajs react wrapper has its own.
 */
// TODO: F should be subtype of js.Object - fix this in scalajs-react
trait JsObjectWrapper[F[_ <: R], -R] {
  def wrap[A <: R](a: A): F[A]
}

object JsObjectWrapper {
  def apply[F[_ <: R], R](implicit F: JsObjectWrapper[F, R]) = F
}
