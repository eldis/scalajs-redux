package eldis.redux

import eldis.redux.Redux.{ WrappedAction => WA }
import org.scalatest._
import scalajs.js
import scala.concurrent.Promise
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import js.|

class ReduxSpec extends AsyncFunSpec with Matchers {

  case class State(
    num: Int = 0,
    str: String = ""
  )

  sealed trait Action
  case class ChangeNum(v: Int) extends Action
  case class ChangeStr(v: String) extends Action

  describe("Store") {

    def reducer(s: State, a: Action): State = {
      a match {
        case ChangeNum(v) => s.copy(num = v)
        case ChangeStr(v) => s.copy(str = v)
      }
    }

    def mkStore(enhancer: js.UndefOr[Enhancer[State, Action]] = js.undefined) = createStore[State, Action](reducer _, State(), enhancer)

    it("must initialize state with defaults") {
      mkStore().getState() shouldBe State()
    }

    it("must dispatch actions") {

      val store = mkStore()

      store.dispatch(wrapAction(ChangeNum(100)))
      store.getState().num shouldBe 100

      store.dispatch(wrapAction(ChangeStr("test")))
      store.getState().str shouldBe "test"
    }

    it("must dispatch async actions") {
      val store = mkStore()

      val p = Promise[Action]()
      val f = p.future
      store.dispatch(wrapAction(f))

      store.getState() shouldBe State()

      p.success(ChangeNum(1))

      store.getState().num shouldBe 1
    }

    it("must support middleware") {
      var lastAction: Option[Action] = None
      val mw = { (arg: MiddlewareArg[State, Action]) =>
        { (dispatch: RawDispatcher) =>
          { (a: WA | js.Object) =>
            val action = a.asInstanceOf[WA]
            if (action.`type` == WA.ActionType)
              lastAction = Some(action.scalaJsReduxAction.asInstanceOf[Action])
            dispatch(action)
          }: RawDispatcher
        }: js.Function1[RawDispatcher, RawDispatcher]
      }: Middleware[State, Action]

      val store = mkStore(applyMiddleware(Seq(mw)))
      store.dispatch(wrapAction(ChangeNum(100)))
      lastAction shouldBe Some(ChangeNum(100))

      val p = Promise[Action]()
      val f = p.future
      store.dispatch(wrapAction(f))
      p.success(ChangeStr("str"))
      lastAction shouldBe Some(ChangeStr("str"))
    }
  }
}

