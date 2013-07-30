package tonek.web.snippet.topbar

import net.liftweb.util.Helpers._
import java.net.URLEncoder
import net.liftweb.http.S
import net.liftweb.util.Props

/**
 * @author anton.safonov
 */

object AuthButtons {
  def render = ".oauth-button-ref [href]" #> gauthUrl()

  private def gauthUrl(): String = gauthUrl(URLEncoder.encode(S.hostAndPath, "UTF-8"))

  private def gauthUrl(baseUrl: String): String = {
    "https://accounts.google.com/o/oauth2/auth?" +
      "scope=" +
      "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+" +
      "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+" +
      S.encodeURL("http://www.google.com/reader/api/0/+") +
      S.encodeURL("http://www.google.com/reader/atom/") +
      "&redirect_uri=" + baseUrl + "%2Fauth%2Foauthcallback%2Fgoogle" +
      "&response_type=code" +
      "&client_id=" + Props.get("oauth.google.client_id").open_!
  }
}
