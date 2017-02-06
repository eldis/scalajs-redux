package eldis.redux.react

import scala.scalajs.js

/**
 * Typeclass for a type constructor that wraps data in a JS object.
 *
 * Each scalajs react wrapper has its own.
 */
trait JsObjectWrapper[F[_] <: js.Object] {
  def wrap[A](a: A): F[A]
}

object JsObjectWrapper {
  def apply[F[_] <: js.Object](implicit F: JsObjectWrapper[F]) = F
}
