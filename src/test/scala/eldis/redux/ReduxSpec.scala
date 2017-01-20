package eldis.redux

import org.scalatest._
import scala.concurrent.Promise
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.util.Try

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

    def mkStore = createStore[State, Action](reducer _, State())

    it("must initialize state with defaults") {
      mkStore.getState() shouldBe State()
    }

    it("must dispatch actions") {

      val store = mkStore

      store.dispatch(wrapAction(ChangeNum(100)))
      store.getState().num shouldBe 100

      store.dispatch(wrapAction(ChangeStr("test")))
      store.getState().str shouldBe "test"
    }

    it("must dispatch async actions") {
      val store = mkStore

      val p = Promise[Action]()
      val f = p.future
      store.dispatch(wrapAction(f))

      store.getState() shouldBe State()

      p.success(ChangeNum(1))

      store.getState().num shouldBe 1
    }

  }

}
