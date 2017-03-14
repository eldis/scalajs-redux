package eldis.redux.react

import org.scalajs.dom.raw.Element

import scala.scalajs.js
import _root_.eldis.react._

import _root_.eldis.redux
import base.JsWrapper

package object eldis {
  /** The function that maps the state and the dispatcher function to the component's properties */

  object Provider {
    def apply[S, A](store: redux.Store[S, A])(children: ReactNode*): ReactNode = {
      val baseProvider = base.Provider.asInstanceOf[JSComponent[ProviderProps]]
      React.createElement(
        baseProvider,
        ProviderProps(store),
        children
      )
    }
  }

  @inline
  def connect[C, P, OP](connector: C, comp: FunctionalComponent[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): FunctionalComponent[OP] =
    base.connect[C.State, C.Action, Any, P, OP, FunctionalComponent, Wrapped, Wrapped[P], Wrapped[OP]](
      C(connector)
    )(comp)

  @inline
  def connect[C, P, OP](connector: C, comp: FunctionalComponent.WithChildren[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): FunctionalComponent.WithChildren[OP] =
    base.connect[C.State, C.Action, Any, P, OP, FunctionalComponent.WithChildren, Wrapped, Wrapped[P], Wrapped[OP]](
      C(connector)
    )(comp)

  @inline
  def connect[C, P <: js.Any, OP <: js.Any](connector: C, comp: NativeFunctionalComponent[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): NativeFunctionalComponent[OP] =
    base.connect[C.State, C.Action, js.Any, P, OP, NativeFunctionalComponent, Identity, Identity[P], Identity[OP]](
      C(connector)
    )(comp)

  @inline
  def connect[C, P <: js.Any, OP <: js.Any](connector: C, comp: NativeFunctionalComponent.WithChildren[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): NativeFunctionalComponent.WithChildren[OP] =
    base.connect[C.State, C.Action, js.Any, P, OP, NativeFunctionalComponent.WithChildren, Identity, Identity[P], Identity[OP]](
      C(connector)
    )(comp)

  @inline
  def connect[C, P <: js.Any, OP <: js.Any](connector: C, comp: JSComponent[P])(
    implicit
    C: base.ConnectorLike[C, P, OP]
  ): JSComponent[OP] =
    base.connect[C.State, C.Action, js.Any, P, OP, JSComponent, Identity, Identity[P], Identity[OP]](
      C(connector)
    )(comp)

  @js.native
  private trait ProviderProps extends js.Object {
    val store: js.Any = js.native
  }

  private object ProviderProps {
    def apply(store: js.Any): ProviderProps =
      js.Dynamic.literal(
        store = store
      ).asInstanceOf[ProviderProps]
  }

  // We need two instances here - native and scala components receive
  // their props in different ways
  private implicit val wrapperInstance: JsWrapper[Wrapped] =
    new JsWrapper[Wrapped] {
      override def wrap[A](a: A) = Wrapped(a)
      override def unwrap[A](fa: Wrapped[A]) = fa.get
    }

  type Identity[X] = X
  private implicit val identityInstance: JsWrapper[Identity] =
    new JsWrapper[Identity] {
      override def wrap[A](a: A) = a
      override def unwrap[A](fa: Identity[A]) = fa
    }
}
