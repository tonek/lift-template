package tonek.web.auth

import net.liftweb.http.{S, RedirectResponse}
import net.liftweb.http.rest.RestHelper
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import net.liftweb.util.Props
import scala._
import collection.JavaConverters.seqAsJavaListConverter
import io.Source
import net.liftweb.common.Full
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import net.liftweb.json.JsonParser
import tonek.web.model.User
import tonek.web.data.request.GoogleUserInfoRequest

/**
 * @author anton.safonov
 */

object OauthAuthorizer extends RestHelper {
  serve {
    case "auth" :: "oauthcallback" :: "google" :: Nil Get req => {
      req.param("error") match {
        case Full(msg) =>  RedirectResponse("/?oauth_err=" + S.encodeURL(msg))
        case _ => {
          val code = (req.param("code") ?~ "code missing" ~> 400).openTheBox
          val tokenReq = new HttpPost("https://accounts.google.com/o/oauth2/token")
          val nvps = List(
            new BasicNameValuePair("code", code),
            new BasicNameValuePair("client_id", Props.get("oauth.google.client_id").open_!),
            new BasicNameValuePair("client_secret", Props.get("oauth.google.client_secret").open_!),
            new BasicNameValuePair("redirect_uri", S.hostAndPath + "/auth/oauthcallback/google"),
            new BasicNameValuePair("grant_type", "authorization_code")
          )
          tokenReq.setEntity(new UrlEncodedFormEntity(nvps.asJava))
          val httpclient = new DefaultHttpClient()
          val tokenResponse = httpclient.execute(tokenReq)
          val entity = tokenResponse.getEntity
          val json = Source.fromInputStream(entity.getContent).getLines().mkString(" ")
          val accessInfo = JsonParser.parse(json).extract[AccessInfo]

          val userInfo = GoogleUserInfoRequest(accessInfo)

          val user = User.byEmail(userInfo.email) match {
            case None => User.create(userInfo, accessInfo.access_token)
            case Some(u) => User.updateGauthCode(u.id.get, accessInfo.access_token)
          }

          User.logUserIn(user)

          EntityUtils.consume(entity)

          RedirectResponse("/")
        }
      }
    }
  }
}

