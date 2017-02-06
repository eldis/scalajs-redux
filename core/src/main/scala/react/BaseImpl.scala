package eldis.redux.react

import scala.scalajs.js
import js.annotation._
import js.|

import scala.concurrent.Future

import eldis.redux.Redux

private[react] object BaseImpl {

  type Connector[S, A, P] = Function1[Redux.Dispatcher[A], Function1[S, P]]
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
}
