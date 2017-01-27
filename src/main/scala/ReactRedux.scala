package eldis.redux

import scala.scalajs.js
import js.annotation._
import js.|

import japgolly.scalajs.react._
import org.scalajs.dom
import dom.raw.Element
import japgolly.scalajs.react.vdom.prefix_<^._
import scala.concurrent.Future

private[redux] object ReactRedux {

  /** The properties of Provider component. */
  @js.native
  trait ProviderProps extends js.Object {
    val store: js.Any = js.native
  }

  object Impl {

    object ProviderProps {
      def apply(store: js.Any): ProviderProps =
        js.Dynamic.literal(
          store = store
        ).asInstanceOf[ProviderProps]
    }

    type SelectorFactory[S, P] = js.Function2[Redux.RawDispatcher, js.UndefOr[js.Any], js.Function2[S, WrapObj[P], WrapObj[P]]]

    @JSImport("react-redux", JSImport.Namespace)
    @js.native
    object Funcs extends js.Object {
      def connectAdvanced[S, P, C <: ReactClass[P, _, _, Element]](selectorFactory: SelectorFactory[S, P]): js.Function1[C, C] = js.native
    }

    @JSImport("react-redux", "Provider")
    @js.native
    object Provider extends JsComponentType[ProviderProps, js.Any, Element]

  }

  object Provider {

    def apply[S, A](store: Redux.Store[S, A])(child: ReactNode) =
      React.createFactory(Impl.Provider)(
        Impl.ProviderProps(store), child
      )

  }

  type RawConnector[S, P] = Function1[Redux.RawDispatcher, Function1[S, P]]

  type Connector[S, A, P] = Function1[Redux.Dispatcher[A], Function1[S, P]]

  def connectRaw[S, P, C <: ReactClass[P, _, _, Element]](connector: RawConnector[S, P])(cls: C): C = {
    def mkConnector(dispatch: Redux.RawDispatcher): js.Function2[S, WrapObj[P], WrapObj[P]] =
      (state: S, _: WrapObj[P]) => WrapObj(connector(dispatch)(state))

    val f: Impl.SelectorFactory[S, P] =
      (dispatch: Redux.RawDispatcher, _: js.UndefOr[js.Any]) => mkConnector(dispatch)

    Impl.Funcs.connectAdvanced(f)(cls)
  }

  def connectImpl[S, A, P, C <: ReactClass[P, _, _, Element]](connector: Connector[S, A, P])(cls: C): C = {
    val raw: RawConnector[S, P] = d => {
      s =>
        {
          connector((a: A | Future[A]) => {
            if (a.isInstanceOf[Future[_]])
              d(Redux.wrapAction(a.asInstanceOf[Future[A]]))
            else
              d(Redux.wrapAction(a.asInstanceOf[A]))
          })(s)
        }
    }
    connectRaw(raw)(cls)
  }

  /** The factory that produces component's connected to the store. */
  trait ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] {
    def apply(props: Props, children: ReactNode*): ReactComponentU[Props, State, Backend, Node]
  }

  def connect[S, A, P, S1, B](connector: Connector[S, A, P])(cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[P, S1, B, Element] =
    new ConnectedComponentFactory[P, S1, B, Element] {
      def apply(props: P, children: ReactNode*) = {
        React.createFactory(
          connectImpl(connector)(cls)
        )(WrapObj(props), children)
      }
    }

}
