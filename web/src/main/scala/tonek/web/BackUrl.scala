package tonek.web

import net.liftweb.http.{S, RequestVar}
import net.liftweb.common.Box

/**
 * @author anton.safonov
 */
object BackUrl extends RequestVar[Box[String]](S.param("back_url")) {

  def append(url: String): String = {
    val result = new StringBuilder(url)
    if (url.contains("?")) {
      result.append("&")
    } else {
      result.append("?")
    }
    result.append("back_url=").append(S.encodeURL(S.uriAndQueryString.openOr(""))).toString()
  }
}
