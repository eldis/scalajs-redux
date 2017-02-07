package eldis.redux.react.base

import scala.scalajs.js
import js.annotation._
import js.|

import scala.concurrent.Future

import eldis.redux.Redux

private[base] object BaseImpl {

  type Connector[S, A, P] = Function1[Redux.Dispatcher[A], Function1[S, P]]
  type RawConnector[S, P] = Function1[Redux.RawDispatcher, Function1[S, P]]

  // common JS imports and types

  // TODO: FP should be <: js.Object - fix the reason in scalajs-react
  type SelectorFactory[S, FP <: js.Any] = js.Function2[Redux.RawDispatcher, js.UndefOr[js.Any], js.Function2[S, FP, FP]]

  @JSImport("react-redux", JSImport.Namespace)
  @js.native
  object Funcs extends js.Object {
    def connectAdvanced[S, FP <: js.Any, C <: js.Any](
      selectorFactory: SelectorFactory[S, FP]
    ): js.Function1[C, C] = js.native
  }

  @JSImport("react-redux", "Provider")
  @js.native
  object Provider extends js.Any

  // We need FP and type equality witness here since we can't directly
  // specify F[P] <: js.Any.
  def connectRaw[S, P, C <: js.Any, F[_]: JsWrapper, FP <: js.Any](
    connector: RawConnector[S, P]
  )(cls: C)(implicit FP: F[P] =:= FP): C = {
    def mkConnector(dispatch: Redux.RawDispatcher): js.Function2[S, FP, FP] =
      (state: S, _: FP) => FP(JsWrapper[F].wrap[P](connector(dispatch)(state)))

    val f: SelectorFactory[S, FP] =
      (dispatch: Redux.RawDispatcher, _: js.UndefOr[js.Any]) => mkConnector(dispatch)

    Funcs.connectAdvanced[S, FP, C](f)(cls)
  }

  def connectImpl[S, A, P, C <: js.Any, F[_]: JsWrapper, FP <: js.Any](
    connector: Connector[S, A, P]
  )(cls: C)(implicit FP: F[P] =:= FP): C = {
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
    connectRaw[S, P, C, F, FP](raw)(cls)
  }
}
