package eldis.redux.react.base

import scala.scalajs.js
import js.annotation._
import js.|

import scala.concurrent.Future

import eldis.redux.Redux

private[base] object BaseImpl {

  type RawConnector[S, P, OP] = Function1[Redux.RawDispatcher, Function2[S, OP, P]]

  // common JS imports and types

  // These properties are provided by React - we should preserve them.
  @js.native
  trait StandardProps extends js.Any {
    val children: js.Any = js.native
  }

  // FP - resulting properties (wrapped)
  // FOP - own properties (wrapped)
  // TODO: FP should be <: js.Object - fix the reason in scalajs-react
  type SelectorFactory[S, FP <: js.Any, FOP <: js.Any] = js.Function2[Redux.RawDispatcher, js.UndefOr[js.Any], js.Function2[S, FOP with StandardProps, FP with StandardProps]]

  @JSImport("react-redux", JSImport.Namespace)
  @js.native
  object Funcs extends js.Object {
    def connectAdvanced[S, FP <: js.Any, FOP <: js.Any, C <: js.Any, CConnected <: js.Any](
      selectorFactory: SelectorFactory[S, FP, FOP]
    ): js.Function1[C, CConnected] = js.native
  }

  @JSImport("react-redux", "Provider")
  @js.native
  object Provider extends js.Any

  // We need FP and type equality witness here since we can't directly
  // specify F[P] <: js.Any.
  def connectRaw[S, R, P <: R, OP <: R, C[_ <: R] <: js.Any, F[_]: JsWrapper, FP <: js.Any, FOP <: js.Any](
    connector: RawConnector[S, P, OP]
  )(cls: C[P])(implicit FP: F[P] =:= FP, FOP: F[OP] =:= FOP): C[OP] = {
    def mkConnector(dispatch: Redux.RawDispatcher): js.Function2[S, FOP with StandardProps, FP with StandardProps] =
      (state: S, ownProps: FOP with StandardProps) => {
        val wrapper = JsWrapper[F]
        val fop = ownProps.asInstanceOf[F[OP]]
        val op = wrapper.unwrap(fop)
        val fp0: F[P] = wrapper.wrap[P](connector(dispatch)(state, op))
        val fp = fp0.asInstanceOf[FP]
        // Make sure all the properties are copied!
        fp.asInstanceOf[js.Dynamic].children = ownProps.children
        fp.asInstanceOf[FP with StandardProps]
      }

    val f: SelectorFactory[S, FP, FOP] =
      (dispatch: Redux.RawDispatcher, _: js.UndefOr[js.Any]) => mkConnector(dispatch)

    Funcs.connectAdvanced[S, FP, FOP, C[P], C[OP]](f)(cls)
  }

  def connectImpl[S, A, R, P <: R, OP <: R, C[_ <: R] <: js.Any, F[_]: JsWrapper, FP <: js.Any, FOP <: js.Any](
    connector: Connector[S, A, P, OP]
  )(cls: C[P])(implicit FP: F[P] =:= FP, FOP: F[OP] =:= FOP): C[OP] = {
    val raw: RawConnector[S, P, OP] = d => {
      (s, op) =>
        {
          connector((a: A | Future[A]) => {
            if (a.isInstanceOf[Future[_]])
              d(Redux.wrapAction(a.asInstanceOf[Future[A]]))
            else
              d(Redux.wrapAction(a.asInstanceOf[A]))
          })(s, op)
        }
    }
    connectRaw[S, R, P, OP, C, F, FP, FOP](raw)(cls)
  }
}
