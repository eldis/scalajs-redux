package eldis.redux.react

import org.scalajs.dom.raw.Element

import scala.scalajs.js
import _root_.eldis.react._

package object eldis {
  /** The function that maps the state and the dispatcher function to the component's properties */

  val Provider = EldisImpl.Provider

  @inline
  def connect[C, P, OP](connector: C, comp: FunctionalComponent[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): FunctionalComponent[OP] =
    EldisImpl.connect(C(connector), comp)

  @inline
  def connect[C, P, OP](connector: C, comp: FunctionalComponent.WithChildren[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): FunctionalComponent.WithChildren[OP] =
    EldisImpl.connect(C(connector), comp)

  @inline
  def connect[C, P <: js.Any, OP <: js.Any](connector: C, comp: NativeFunctionalComponent[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): NativeFunctionalComponent[OP] =
    EldisImpl.connect(C(connector), comp)

  @inline
  def connect[C, P <: js.Any, OP <: js.Any](connector: C, comp: NativeFunctionalComponent.WithChildren[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): NativeFunctionalComponent.WithChildren[OP] =
    EldisImpl.connect(C(connector), comp)

  @inline
  def connect[C, P <: js.Any, OP <: js.Any](connector: C, comp: JSComponent[P])(
    implicit
    C: base.ConnectorLike[C, _, _, P, OP]
  ): JSComponent[OP] =
    EldisImpl.connect(C(connector), comp)
}
