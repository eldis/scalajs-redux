package examples.eldiscomponents

import eldis.redux._

import scala.concurrent.ExecutionContext.Implicits.global

case class State(
  functional: ScalaProps,
  functionalWithChildren: ScalaProps,
  nativeFunctional: JSProps,
  nativeFunctionalWithChildren: JSProps,
  baseIdentity: JSProps,
  baseWrapped: ScalaProps,
  js: JSProps
)

object Store {

  def reducer(s: State, a: Action): State = s

  def apply(state: State): Store[State, Action] = {
    createStore[State, Action](
      reducer _,
      state
    )
  }

}
