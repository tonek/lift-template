package tonek.services

import org.apache.http.client.methods.{HttpPost, HttpPut}
import org.apache.http.impl.client.DefaultHttpClient
import net.liftweb.json._
import ext.JodaTimeSerializers
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.http.protocol.HTTP
import java.net.URLEncoder
import java.io.InputStreamReader

/**
 * @author anton.safonov
 */
class SearchService(host:String = "localhost", port: Int = 9200) {
  private implicit val formats = net.liftweb.json.DefaultFormats ++ JodaTimeSerializers.all
  private val address: String = "http://%s:%s/".format(host, port)

  def indexFeed(item: Nothing) {

    val put = new HttpPut("%s/items/item/%s".format(address, "id"))
    val json = Serialization.write(new IndexRequest(item))
    put.setEntity(new StringEntity(json, HTTP.UTF_8))
    val response = new DefaultHttpClient().execute(put)
    EntityUtils.consume(response.getEntity)
  }

  def search(query: String): List[SearchResultHit] = {
    val request = new HttpPost("%s/items/item/_search".format(address))
    request.setEntity(new StringEntity(
      "{query:{query_string:{query:\"%s\"}},sort:[{\"creationDate\":\"desc\"}],size:100}"
        .format(URLEncoder.encode(query, HTTP.UTF_8))
    ))

    val response = new DefaultHttpClient().execute(request)
    val hits = JsonParser.parse(new InputStreamReader(response.getEntity.getContent)).extract[SearchResult].hits
    hits.hits
  }
}

case class IndexRequest(text: String) {
  def this(item: Nothing) = this("test")
}

case class SearchResult(hits: SearchResultHitsContainer)
case class SearchResultHitsContainer(hits: List[SearchResultHit])
case class SearchResultHit(_id: String, _source: IndexRequest) {
  val id = _id.toLong
}
