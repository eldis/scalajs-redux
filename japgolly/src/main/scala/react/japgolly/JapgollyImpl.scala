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

  def connect[S, A, P, S1, B](connector: base.Connector[S, A, P], cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[P, S1, B, Element] =
    new ConnectedComponentFactory[P, S1, B, Element] {
      def apply(props: P, children: ReactNode*) = {
        React.createFactory(
          base.connect[S, A, P, ReactClass[P, S1, B, Element], WrapObj, WrapObj[P]](connector)(cls)
        )(WrapObj(props), children)
      }
    }

  def connect[S, A, P](connector: base.Connector[S, A, P], comp: FunctionalComponent[P]): FunctionalComponent[P] =
    base.connect(connector)(comp)

  def connect[S, A, P](connector: base.Connector[S, A, P], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[P] =
    base.connect(connector)(comp)

}
