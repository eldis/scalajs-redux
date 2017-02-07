package eldis.redux.react

import org.scalajs.dom.raw.Element

import scala.scalajs.js
import _root_.eldis.react._

package object eldis {
  /** The function that maps the state and the dispatcher function to the component's properties */
  type Connector[S, A, P] = base.Connector[S, A, P]

  val Provider = EldisImpl.Provider

  type ProviderProps = EldisImpl.ProviderProps
  val ProviderProps = EldisImpl.ProviderProps

  @inline
  def connect[S, A, P](connector: base.Connector[S, A, P], comp: FunctionalComponent[P]): FunctionalComponent[P] =
    EldisImpl.connect[S, A, P](connector, comp)

  @inline
  def connect[S, A, P](connector: base.Connector[S, A, P], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[P] =
    EldisImpl.connect[S, A, P](connector, comp)

  @inline
  def connect[S, A, P <: js.Any](connector: base.Connector[S, A, P], comp: NativeFunctionalComponent[P]): NativeFunctionalComponent[P] =
    EldisImpl.connect[S, A, P](connector, comp)

  @inline
  def connect[S, A, P <: js.Any](connector: base.Connector[S, A, P], comp: NativeFunctionalComponent.WithChildren[P]): NativeFunctionalComponent.WithChildren[P] =
    EldisImpl.connect[S, A, P](connector, comp)

  @inline
  def connect[S, A, P <: js.Any](connector: base.Connector[S, A, P], comp: JSComponent[P]): JSComponent[P] =
    EldisImpl.connect[S, A, P](connector, comp)

}
