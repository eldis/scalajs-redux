package examples.eldiscomponents

import scala.scalajs.js

@js.native
trait JSProps extends js.Object {
  def jsValue: String
}

object JSProps {
  def apply(value: String): JSProps =
    js.Dynamic.literal(
      jsValue = value
    ).asInstanceOf[JSProps]
}
