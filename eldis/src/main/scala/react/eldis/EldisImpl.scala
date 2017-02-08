package eldis.redux.react.eldis

import scala.scalajs.js
import js.|
import js.annotation._
import _root_.eldis.react._

import _root_.eldis.redux
import redux.react.base
import base.JsWrapper

private[react] object EldisImpl {

  @js.native
  trait ProviderProps extends js.Object {
    val store: js.Any = js.native
  }

  object ProviderProps {
    def apply(store: js.Any): ProviderProps =
      js.Dynamic.literal(
        store = store
      ).asInstanceOf[ProviderProps]
  }

  object Provider {

    def apply[S, A](store: redux.Store[S, A])(children: ReactNode*) = {
      val provider = base.Provider.asInstanceOf[JSComponent[ProviderProps]]
      React.createElement(
        provider,
        ProviderProps(store),
        children: _*
      )
    }
  }

  // We need two instances here - native and scala components receive
  // their props in different ways
  implicit val wrapperInstance: JsWrapper[Wrapped] =
    new JsWrapper[Wrapped] {
      override def wrap[A](a: A) = Wrapped(a)
      override def unwrap[A](fa: Wrapped[A]) = fa.get
    }

  type Identity[X] = X
  implicit val identityInstance: JsWrapper[Identity] =
    new JsWrapper[Identity] {
      override def wrap[A](a: A) = a
      override def unwrap[A](fa: Identity[A]) = fa
    }

  @inline
  def connect[S, A, P, OP](connector: base.Connector[S, A, P, OP], comp: FunctionalComponent[P]): FunctionalComponent[OP] =
    base.connect[S, A, Any, P, OP, FunctionalComponent, Wrapped, Wrapped[P], Wrapped[OP]](connector)(comp)

  @inline
  def connect[S, A, P, OP](connector: base.Connector[S, A, P, OP], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[OP] =
    base.connect[S, A, Any, P, OP, FunctionalComponent.WithChildren, Wrapped, Wrapped[P], Wrapped[OP]](connector)(comp)

  @inline
  def connect[S, A, P <: js.Any, OP <: js.Any](connector: base.Connector[S, A, P, OP], comp: NativeFunctionalComponent[P]): NativeFunctionalComponent[OP] =
    base.connect[S, A, js.Any, P, OP, NativeFunctionalComponent, Identity, Identity[P], Identity[OP]](connector)(comp)

  @inline
  def connect[S, A, P <: js.Any, OP <: js.Any](connector: base.Connector[S, A, P, OP], comp: NativeFunctionalComponent.WithChildren[P]): NativeFunctionalComponent.WithChildren[OP] =
    base.connect[S, A, js.Any, P, OP, NativeFunctionalComponent.WithChildren, Identity, Identity[P], Identity[OP]](connector)(comp)

  @inline
  def connect[S, A, P <: js.Any, OP <: js.Any](connector: base.Connector[S, A, P, OP], comp: JSComponent[P]): JSComponent[OP] =
    base.connect[S, A, js.Any, P, OP, JSComponent, Identity, Identity[P], Identity[OP]](connector)(comp)

}
