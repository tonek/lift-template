package tonek.web.data.request

/**
 * @author anton.safonov
 */
case class Tags(tags: List[RssTagInfo])
case class RssTagInfo(id: String, sortid: String)