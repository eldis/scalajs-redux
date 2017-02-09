package eldis.redux.examples.react

sealed trait Action

case class ChangeFilter(v: String) extends Action
