package eldis.redux

import scala.scalajs.js
import js.|
import js.annotation._
import scala.concurrent.{ Future, ExecutionContext }

private[redux] object Redux {

  type Reducer[S, A] = js.Function2[S, A, S]

  type StateGetter[S] = js.Function0[S]

  @js.native
  trait WrappedAction extends js.Object {
    val `type`: String = js.native
    val scalaJsReduxAction: js.Any = js.native
  }

  object WrappedAction {

    val ActionType = "scalaJsReduxAction"
    val AsyncActionType = "scalaJsReduxAsyncAction"

    def apply[A](a: A) = js.Dynamic.literal(
      `type` = ActionType,
      scalaJsReduxAction = a.asInstanceOf[js.Any]
    ).asInstanceOf[WrappedAction]

    def apply[A](a: Future[A]) = js.Dynamic.literal(
      `type` = AsyncActionType,
      scalaJsReduxAction = a.asInstanceOf[js.Any]
    ).asInstanceOf[WrappedAction]
  }

  type Dispatcher[A] = js.Function1[A | Future[A], Unit]

  type RawDispatcher = js.Function1[WrappedAction | js.Object, Unit]

  @js.native
  trait MiddlewareArg[S, A] extends js.Object {
    val getState: StateGetter[S] = js.native
    val dispatch: RawDispatcher = js.native
  }

  type Middleware[S, A] = js.Function1[MiddlewareArg[S, A], js.Function1[RawDispatcher, RawDispatcher]]

  type Listener = js.Function0[Unit]

  type Unsubscriber = js.Function0[Unit]

  @js.native
  trait Store[S, A] extends js.Object {
    val getState: StateGetter[S] = js.native
    val dispatch: RawDispatcher = js.native
    val subscribe: js.Function1[Listener, Unsubscriber] = js.native
    val replaceReducer: js.Function1[Reducer[S, A], Unit] = js.native
  }

  type Enhancer[S, A] = js.Function1[js.Function, js.Function3[Reducer[S, A | js.Object], js.UndefOr[S], js.UndefOr[js.Function], Store[S, A]]]

  @JSImport("redux", JSImport.Namespace)
  @js.native
  object Impl extends js.Object {
    def createStore[S, A](
      reducer: Reducer[S, A | js.Object],
      initialState: js.UndefOr[S] = js.undefined,
      enhancer: js.UndefOr[Enhancer[S, A]] = js.undefined
    ): Store[S, A] = js.native

    def applyMiddleware[S, A](xs: Middleware[S, A]*): Enhancer[S, A] = js.native
  }

  def wrapReducer[S, A](reducer: Reducer[S, A], rawReducer: js.UndefOr[Reducer[js.Any, js.Any]]): Reducer[S, A | js.Object] =
    (s: S, a: A | js.Object) => {
      val newS = rawReducer.map(r => r(s.asInstanceOf[js.Any], a.asInstanceOf[js.Any]).asInstanceOf[S]).getOrElse(s)
      val aDyn = a.asInstanceOf[js.Dynamic]
      if (aDyn.`type` != js.undefined) {
        if (aDyn.`type`.asInstanceOf[String] == WrappedAction.ActionType)
          reducer(newS, aDyn.scalaJsReduxAction.asInstanceOf[A])
        else
          newS
      } else {
        newS
      }
    }

  def asyncEnhancer[S, A](implicit ec: ExecutionContext): Enhancer[S, A] = {
    val asyncMiddleware: Middleware[S, A] = ((arg: MiddlewareArg[S, A]) => {
      ((next: RawDispatcher) =>
        {
          ((action: WrappedAction | js.Object) =>
            {
              val aDyn = action.asInstanceOf[js.Dynamic]
              if (aDyn.`type` != js.undefined && aDyn.`type`.asInstanceOf[String] == WrappedAction.AsyncActionType) {
                val f = aDyn.scalaJsReduxAction.asInstanceOf[Future[A]]
                f.onSuccess {
                  case a => {
                    arg.dispatch(WrappedAction(a))
                  }
                }
              } else
                next(action)
            }): RawDispatcher
        }): js.Function1[RawDispatcher, RawDispatcher]
    }): Middleware[S, A]
    Impl.applyMiddleware(asyncMiddleware)
  }

  def createStore[S, A](
    reducer: Reducer[S, A],
    initialState: js.UndefOr[S] = js.undefined,
    rawReducer: js.UndefOr[js.Function] = js.undefined,
    enhancer: js.UndefOr[Enhancer[S, A]] = js.undefined
  )(implicit ec: ExecutionContext): Store[S, A] = {
    val enh: Enhancer[S, A] = asyncEnhancer(ec)
    val create = enh(Impl.createStore _)
    create(wrapReducer(reducer, rawReducer.asInstanceOf[Reducer[js.Any, js.Any]]), initialState, enhancer)
  }

  def applyMiddleware[S, A](xs: Middleware[S, A]*): Enhancer[S, A] = Impl.applyMiddleware(xs: _*)

  def wrapAction[A](a: A): WrappedAction = WrappedAction(a)

  def wrapAction[A](a: Future[A]): WrappedAction = WrappedAction(a)

}
