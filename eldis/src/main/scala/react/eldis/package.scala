package eldis.redux.react

import org.scalajs.dom.raw.Element

import scala.scalajs.js
import _root_.eldis.react._

package object eldis {
  /** The function that maps the state and the dispatcher function to the component's properties */

  val Provider = EldisImpl.Provider

  @inline
  def connect[S, A, P, OP](connector: base.Connector[S, A, P, OP], comp: FunctionalComponent[P]): FunctionalComponent[OP] =
    EldisImpl.connect[S, A, P, OP](connector, comp)

  @inline
  def connect[S, A, P, OP](connector: base.Connector[S, A, P, OP], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[OP] =
    EldisImpl.connect[S, A, P, OP](connector, comp)

  @inline
  def connect[S, A, P <: js.Any, OP <: js.Any](connector: base.Connector[S, A, P, OP], comp: NativeFunctionalComponent[P]): NativeFunctionalComponent[OP] =
    EldisImpl.connect[S, A, P, OP](connector, comp)

  @inline
  def connect[S, A, P <: js.Any, OP <: js.Any](connector: base.Connector[S, A, P, OP], comp: NativeFunctionalComponent.WithChildren[P]): NativeFunctionalComponent.WithChildren[OP] =
    EldisImpl.connect[S, A, P, OP](connector, comp)

  @inline
  def connect[S, A, P <: js.Any, OP <: js.Any](connector: base.Connector[S, A, P, OP], comp: JSComponent[P]): JSComponent[OP] =
    EldisImpl.connect[S, A, P, OP](connector, comp)

}
