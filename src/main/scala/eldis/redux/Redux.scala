package eldis.redux

import scala.scalajs.js
import js.|
import js.annotation._

object Redux {

  type Reducer[S, A] = js.Function2[S, A, S]

  type StateGetter[S] = js.Function0[S]

  @js.native
  trait WrappedAction extends js.Object {
    val `type`: String = js.native
    val scalaJsReduxAction: js.Any = js.native
  }

  object WrappedAction {
    def apply[A](a: A) = js.Dynamic.literal(
      `type` = "scalaJsReduxAction",
      scalaJsReduxAction = a.asInstanceOf[js.Any]
    ).asInstanceOf[WrappedAction]
  }

  type Dispatcher[A] = js.Function1[A, js.Any]

  type RawDispatcher = Dispatcher[WrappedAction | js.Object]

  @js.native
  trait MiddlewareArg[S, A] extends js.Object {
    val getState: StateGetter[S] = js.native
    val dispatch: Dispatcher[A] = js.native
  }

  type Middleware[S, A] = js.Function1[MiddlewareArg[S, A], js.Function1[Dispatcher[A], Dispatcher[A]]]

  type Listener = js.Function0[Unit]

  type Unsubscriber = js.Function0[Unit]

  @js.native
  trait Store[S, A] extends js.Object {
    val getState: StateGetter[S] = js.native
    val dispatch: RawDispatcher = js.native
    val subscribe: js.Function1[Listener, Unsubscriber] = js.native
    val replaceReducer: js.Function1[Reducer[S, A], Unit] = js.native
  }

  type Enhancer[S, A] = js.Function1[Store[S, A], Store[S, A]]

  @JSImport("redux", JSImport.Namespace)
  @js.native
  private object Impl extends js.Object {
    def createStore[S, A](
      reducer: Reducer[S, A | js.Object],
      initialState: js.UndefOr[S] = js.undefined,
      enhancer: js.UndefOr[Enhancer[S, A | js.Object]] = js.undefined
    ): Store[S, A] = js.native
  }

  def wrapReducer[S, A](r: Reducer[S, A]): Reducer[S, A | js.Object] =
    (s: S, a: A | js.Object) => {
      val aDyn = a.asInstanceOf[js.Dynamic]
      if (aDyn.scalaJsReduxAction != js.undefined)
        r(s, aDyn.scalaJsReduxAction.asInstanceOf[A])
      else
        s
    }

  def createStore[S, A](
    reducer: Reducer[S, A],
    initialState: js.UndefOr[S] = js.undefined,
    enhancer: js.UndefOr[Enhancer[S, A | js.Object]] = js.undefined
  ): Store[S, A] = Impl.createStore(wrapReducer(reducer), initialState, enhancer)

  def createAction[A](a: A): WrappedAction = WrappedAction(a)

}
