package eldis.redux.examples.japgolly

sealed trait Action

case class ChangeFilter(v: String) extends Action
