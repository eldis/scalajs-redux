package eldis.redux.react

import scala.scalajs.js
import _root_.eldis.react._
import _root_.eldis.redux

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
  implicit val wrapperInstance: JsObjectWrapper[Wrapped, Any] =
    new JsObjectWrapper[Wrapped, Any] {
      override def wrap[A](a: A) = Wrapped(a)
    }

  type Identity[X] = X
  implicit val identityInstance: JsObjectWrapper[Identity, js.Any] =
    new JsObjectWrapper[Identity, js.Any] {
      override def wrap[A <: js.Any](a: A) = a
    }

  @inline
  def connect[S, A, P](connector: base.Connector[S, A, P], comp: FunctionalComponent[P]): FunctionalComponent[P] =
    base.connect[S, A, P, FunctionalComponent[P], Wrapped, Wrapped[P]](connector)(comp)

  @inline
  def connect[S, A, P](connector: base.Connector[S, A, P], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[P] =
    base.connect[S, A, P, FunctionalComponent.WithChildren[P], Wrapped, Wrapped[P]](connector)(comp)

  @inline
  def connect[S, A, P <: js.Any](connector: base.Connector[S, A, P], comp: NativeFunctionalComponent[P]): NativeFunctionalComponent[P] =
    base.connect[S, A, P, NativeFunctionalComponent[P], Identity, Identity[P]](connector)(comp)

  @inline
  def connect[S, A, P <: js.Any](connector: base.Connector[S, A, P], comp: NativeFunctionalComponent.WithChildren[P]): NativeFunctionalComponent.WithChildren[P] =
    base.connect[S, A, P, NativeFunctionalComponent.WithChildren[P], Identity, Identity[P]](connector)(comp)

  @inline
  def connect[S, A, P <: js.Any](connector: base.Connector[S, A, P], comp: JSComponent[P]): JSComponent[P] =
    base.connect[S, A, P, JSComponent[P], Identity, Identity[P]](connector)(comp)

}
