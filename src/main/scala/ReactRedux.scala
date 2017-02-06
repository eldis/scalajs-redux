package eldis.redux

import scala.scalajs.js
import js.annotation._
import js.|

import org.scalajs.dom
import dom.raw.Element
import scala.concurrent.Future

/**
 * Base for ReactRedux implementation modules
 */
trait ReactRedux[F[_] <: js.Object] {
  type Connector[S, A, P] = ReactRedux.Connector[S, A, P]

  protected implicit def wrapperInstance: ReactRedux.JsObjectWrapper[F]
}

private[redux] object ReactRedux {

  type Connector[S, A, P] = Function1[Redux.Dispatcher[A], Function1[S, P]]

  trait JsObjectWrapper[F[_] <: js.Object] {
    def wrap[A](a: A): F[A]
  }

  object JsObjectWrapper {
    def apply[F[_] <: js.Object](implicit F: JsObjectWrapper[F]) = F
  }

  type RawConnector[S, P] = Function1[Redux.RawDispatcher, Function1[S, P]]

  // common JS imports and types

  type SelectorFactory[S, FP <: js.Object] = js.Function2[Redux.RawDispatcher, js.UndefOr[js.Any], js.Function2[S, FP, FP]]

  @JSImport("react-redux", JSImport.Namespace)
  @js.native
  object Funcs extends js.Object {
    def connectAdvanced[S, FP <: js.Object, C <: js.Any](
      selectorFactory: SelectorFactory[S, FP]
    ): js.Function1[C, C] = js.native
  }

  @JSImport("react-redux", "Provider")
  @js.native
  object Provider extends js.Any

  def connectRaw[S, P, C <: js.Any, F[_] <: js.Object: JsObjectWrapper](
    connector: RawConnector[S, P]
  )(cls: C): C = {
    def mkConnector(dispatch: Redux.RawDispatcher): js.Function2[S, F[P], F[P]] =
      (state: S, _: F[P]) => JsObjectWrapper[F].wrap(connector(dispatch)(state))

    val f: SelectorFactory[S, F[P]] =
      (dispatch: Redux.RawDispatcher, _: js.UndefOr[js.Any]) => mkConnector(dispatch)

    Funcs.connectAdvanced[S, F[P], C](f)(cls)
  }

  def connectImpl[S, A, P, C <: js.Any, F[_] <: js.Object: JsObjectWrapper](
    connector: Connector[S, A, P]
  )(cls: C): C = {
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
    connectRaw[S, P, C, F](raw)(cls)
  }

  import japgolly.scalajs.react._

  object Japgolly extends ReactRedux[WrapObj] {

    override protected implicit val wrapperInstance =
      new JsObjectWrapper[WrapObj] {
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

      def apply[S, A](store: Redux.Store[S, A])(child: ReactNode) = {
        val provider = ReactRedux.Provider.asInstanceOf[JsComponentType[ProviderProps, js.Any, Element]]
        React.createFactory(provider)(
          ProviderProps(store), child
        )
      }
    }

    /** The factory that produces component's connected to the store. */
    trait ConnectedComponentFactory[Props, State, +Backend, +Node <: TopNode] {
      def apply(props: Props, children: ReactNode*): ReactComponentU[Props, State, Backend, Node]
    }

    def connect[S, A, P, S1, B](connector: Connector[S, A, P], cls: ReactClass[P, S1, B, Element]): ConnectedComponentFactory[P, S1, B, Element] =
      new ConnectedComponentFactory[P, S1, B, Element] {
        def apply(props: P, children: ReactNode*) = {
          React.createFactory(
            connectImpl[S, A, P, ReactClass[P, S1, B, Element], WrapObj](connector)(cls)
          )(WrapObj(props), children)
        }
      }

    def connect[S, A, P](connector: Connector[S, A, P], comp: FunctionalComponent[P]): FunctionalComponent[P] =
      connectImpl(connector)(comp)

    def connect[S, A, P](connector: Connector[S, A, P], comp: FunctionalComponent.WithChildren[P]): FunctionalComponent.WithChildren[P] =
      connectImpl(connector)(comp)

  }
}
