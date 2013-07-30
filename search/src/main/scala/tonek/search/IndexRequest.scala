package tonek.search

import org.joda.time.DateTime

/**
 * @author anton.safonov
 */
case class FeedIndexRequest(topic: String, extract: String, url: String, creationDate: DateTime)
