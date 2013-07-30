package tonek.web.auth

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import oauth.signpost.OAuth
import io.Source
import org.apache.http.util.EntityUtils
import net.liftweb.json.{JsonParser, DefaultFormats}

/**
 * @author anton.safonov
 */
class RequestWrapper(accessToken: String, url: String) {
  def this(accessInfo: AccessInfo, url: String) = this(accessInfo.access_token, url)

  private implicit val formats = DefaultFormats

  private val client = new DefaultHttpClient()

  def stringResult(): String = {
    val get = new HttpGet(url)
    get.addHeader(OAuth.HTTP_AUTHORIZATION_HEADER, "Bearer " + accessToken)
    val response = client.execute(get)
    val entity = response.getEntity
    val result = Source.fromInputStream(entity.getContent).getLines().mkString("\n")
    EntityUtils.consume(entity)

    result
  }

  def jsonResult[T](implicit mf: scala.reflect.Manifest[T]): T = {
      val stringRes = stringResult()
      println(stringRes)
      JsonParser.parse(stringRes).extract[T](formats, mf)
    }
}
