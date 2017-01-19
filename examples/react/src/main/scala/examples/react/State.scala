package eldis.redux.examples.react

import scala.concurrent.ExecutionContext.Implicits.global
import eldis.redux.Redux

case class State(
  filter: String,
  elements: Seq[String],
  filteredElements: Seq[String]
)

object Store {

  def reducer(s: State, a: Action): State = a match {
    case ChangeFilter(v) =>
      s.copy(
        filter = v,
        filteredElements = s.elements.filter(v.isEmpty() || _.toLowerCase.contains(v.toLowerCase))
      )
  }

  def apply(elements: Seq[String]): Redux.Store[State, Action] = {
    Redux.createStore(
      reducer _,
      State(
        filter = "",
        elements = elements,
        filteredElements = elements
      )
    )
  }

}
