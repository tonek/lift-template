package tonek.search

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpPut
import net.liftweb.json.JsonDSL._

/**
 * @author anton.safonov
 */
class Indexer(host: String, port: Int) {
  private val address: String = "http://%s:%s/".format(host, port)
}
