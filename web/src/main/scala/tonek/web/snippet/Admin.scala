package tonek.web.snippet

import net.liftweb.util.Props
import tonek.web.WidgetsTrait
import scala.xml.{Text, NodeSeq}
import net.liftweb.common.Full
import tonek.web.model.User

/**
 * @author anton.safonov
 */
object Admin extends WidgetsTrait{
  def autoLogin: NodeSeq = {
    Props.getLong("auto.login") match {
      case Full(userId) => {
        User.byId(userId) match {
          case Some(user) => {
            User.logUserIn(user)
            Text("Logged in " + user.niceName)
          }
          case _ => Text("User not found: " + userId)
        }
      }
      case _ => Text("Auto login disabled")
    }
  }
}
