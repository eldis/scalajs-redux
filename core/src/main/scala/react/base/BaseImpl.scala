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
    def mkConnector(dispatch: Redux.RawDispatcher): js.Function2[S, FOP with StandardProps, FP with StandardProps] = {
      // Only do this once!
      val selectorImpl = connector(dispatch)
      (state: S, ownProps: FOP with StandardProps) => {
        val wrapper = JsWrapper[F]
        val fop = ownProps.asInstanceOf[F[OP]]
        val op = wrapper.unwrap(fop)
        val fp0: F[P] = wrapper.wrap[P](selectorImpl(state, op))
        val fp = fp0.asInstanceOf[FP]
        // Make sure all the properties are copied!
        fp.asInstanceOf[js.Dynamic].children = ownProps.children
        fp.asInstanceOf[FP with StandardProps]
      }
    }

    val f: SelectorFactory[S, FP, FOP] =
      (dispatch: Redux.RawDispatcher, _: js.UndefOr[js.Any]) => mkConnector(dispatch)

    val memoF: SelectorFactory[S, FP, FOP] = memoizeSelectorFactory(f)

    Funcs.connectAdvanced[S, FP, FOP, C[P], C[OP]](memoF)(cls)
  }

  def memoize2[A, B, C](f: js.Function2[A, B, C]): js.Function2[A, B, C] = {

    // This is quite dirty, but we have quite a lot of type bounds as we are.
    def jsEq[A](x: A, y: A): Boolean =
      x.asInstanceOf[js.Any] eq y.asInstanceOf[js.Any]

    var prevA: Option[A] = None
    var prevB: Option[B] = None
    var prevC: Option[C] = None
    (a: A, b: B) => {
      if (prevA.exists(jsEq(a, _)) && prevB.exists(jsEq(b, _))) {
        // Can do this since C is always Some if A and B are Some.
        prevC.get
      } else {
        val c = f(a, b)
        prevA = Some(a)
        prevB = Some(b)
        prevC = Some(c)
        c
      }
    }
  }

  /**
   * Simple factory memoization - consecutive call with the same (eq) arguments
   * doesn't run `f`.
   */
  def memoizeSelectorFactory[S, FP <: js.Any, FOP <: js.Any](
    f: SelectorFactory[S, FP, FOP]
  ): SelectorFactory[S, FP, FOP] = {
    val g = (dispatch: Redux.RawDispatcher, opts: js.UndefOr[js.Any]) =>
      memoize2(f(dispatch, opts))

    memoize2(g)
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
