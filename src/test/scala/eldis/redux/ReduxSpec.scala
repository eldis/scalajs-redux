package eldis.redux

import org.scalatest._

class ReduxSpec extends FunSpec with Matchers {

  case class State(
    num: Int = 0,
    str: String = ""
  )

  sealed trait Action
  case class ChangeNum(v: Int) extends Action
  case class ChangeStr(v: String) extends Action

  describe("Store") {

    import Redux._

    def reducer(s: State, a: Action): State =
      a match {
        case ChangeNum(v) => s.copy(num = v)
        case ChangeStr(v) => s.copy(str = v)
      }

    var store = createStore[State, Action](reducer _, State())

    it("can return the state that it holds") {
      store.getState() shouldBe State()
    }

    it("can dispatch actions") {

      store.dispatch(createAction(ChangeNum(100)))
      store.getState().num shouldBe 100

      store.dispatch(createAction(ChangeStr("test")))
      store.getState().str shouldBe "test"
    }

  }

}
