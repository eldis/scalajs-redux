package eldis.redux

import scala.scalajs.js
import js.annotation._
import js.|

import japgolly.scalajs.react._
import org.scalajs.dom
import dom.raw.Element
import japgolly.scalajs.react.vdom.prefix_<^._

/**
 * React redux facade.
 */
object ReactRedux {

  /** The properties of Provider component. */
  @js.native
  trait ProviderProps extends js.Object {
    val store: js.Any = js.native
  }

  private object Impl {

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

  /**
   * The react component that provides the store injection in to the virtual DOM.
   *
   * See [[https://github.com/reactjs/react-redux/blob/master/docs/api.md#provider-store the react-redux documentation]]
   * for detailed description.
   */
  object Provider {

    /**
     * Creates the store provider.
     *
     * @param store The store that will be injected in the virtual DOM
     */
    def apply[S, A](store: Redux.Store[S, A])(child: ReactNode) =
      React.createFactory(Impl.Provider)(
        Impl.ProviderProps(store), child
      )

  }

  private type RawConnector[S, P] = Function2[S, Redux.RawDispatcher, P]

  /** The function that maps the state and the dispatcher function to the component's properties */
  type Connector[S, A, P] = Function2[S, Redux.Dispatcher[A], P]

  private def connectRaw[S, P, C <: ReactClass[P, _, _, Element]](connector: RawConnector[S, P])(cls: C): C = {
    def mkConnector(dispatch: Redux.RawDispatcher): js.Function2[S, WrapObj[P], WrapObj[P]] =
      (state: S, _: WrapObj[P]) => WrapObj(connector(state, dispatch))

    val f: Impl.SelectorFactory[S, P] =
      (dispatch: Redux.RawDispatcher, _: js.UndefOr[js.Any]) => mkConnector(dispatch)

    Impl.Funcs.connectAdvanced(f)(cls)
  }

  private def connectImpl[S, A, P, C <: ReactClass[P, _, _, Element]](connector: Connector[S, A, P])(cls: C): C = {
    val raw: RawConnector[S, P] = (s, d) => connector(s, (a: A) => d(Redux.createAction(a)))
    connectRaw(raw)(cls)
  }

  /** The factory that produces component's connected to the store. */
  trait ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] {
    def apply(props: Props, children: ReactNode*): ReactComponentU[Props, State, Backend, Node]
  }

  /**
   * Creates the connected to state component factory.
   *
   * @param connector  The function that maps state and dispatcher function to component's properties
   * @param cls        The component's class
   */
  def connect[S, A, P, S1, B](connector: Connector[S, A, P])(cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[P, S1, B, Element] =
    new ConnectedComponentFactory[P, S1, B, Element] {
      def apply(props: P, children: ReactNode*) = {
        React.createFactory(
          connectImpl(connector)(cls)
        )(WrapObj(props), children)
      }
    }

}
