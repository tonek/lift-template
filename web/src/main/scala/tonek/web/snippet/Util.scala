package tonek.web.snippet

import xml.NodeSeq
import tonek.web.model.User

/**
 * @author anton.safonov
 */
object Util {
  def loggedIn(xhtml: NodeSeq): NodeSeq = if (User.loggedIn_?) xhtml else NodeSeq.Empty
  def notLoggedIn(xhtml: NodeSeq): NodeSeq = if (User.loggedIn_?) NodeSeq.Empty else xhtml
  def admin(xhtml: NodeSeq): NodeSeq = if (User.loggedIn_? && User.superUser_?) xhtml else NodeSeq.Empty

}
