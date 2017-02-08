package eldis.redux.react.japgolly

import scala.scalajs.js
import org.scalajs.dom.raw.Element
import _root_.japgolly.scalajs.react._

import eldis.redux
import redux.react.base
import base.JsWrapper

private[react] object JapgollyImpl {

  implicit val wrapperInstance =
    new JsWrapper[WrapObj] {
      override def wrap[A](a: A) = WrapObj(a)
      override def unwrap[A](fa: WrapObj[A]) = fa.v
    }

  /** The properties of Provider component. */
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

    def apply[S, A](store: redux.Store[S, A])(child: ReactNode) = {
      val provider = base.Provider.asInstanceOf[JsComponentType[ProviderProps, js.Any, Element]]
      React.createFactory(provider)(
        ProviderProps(store), child
      )
    }
  }

  /** The factory that produces component's connected to the store. */
  trait ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] {
    def apply(props: Props, children: ReactNode*): ReactComponentU[Props, State, Backend, Node]
  }

  def connect[S, A, P, OP, S1, B](connector: base.Connector[S, A, P, OP], cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[OP, S1, B, Element] =
    new ConnectedComponentFactory[OP, S1, B, Element] {
      def apply(props: OP, children: ReactNode*) = {
        React.createFactory(
          base.connect[S, A, Any, P, OP, ReactClass[?, S1, B, Element], WrapObj, WrapObj[P], WrapObj[OP]](connector)(cls)
        )(WrapObj(props), children)
      }
    }

  def connect[S, A, P, OP](connector: base.Connector[S, A, P, OP], comp: FunctionalComponent[P]): FunctionalComponent[OP] =
    base.connect(connector)(comp)

  def connect[S, A, P, OP](connector: base.Connector[S, A, P, OP], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[OP] =
    base.connect(connector)(comp)

}
