package tonek.web.data.request

/**
 * @author anton.safonov
 */
case class RssSubscriptions(subscriptions: List[RssSubscriptionInfo])

case class RssSubscriptionInfo(id: String, title: String, sortid: String, firstitemmsec: String,
                               htmlUrl: Option[String], categories: List[RssSubscriptionCategory]) {
  //remove 'feed/'
  lazy val url = id.substring(5)

  def toInfo: RssItemInfo = RssItemInfo(url, title)
}

case class RssSubscriptionCategory(id: String, label: String)

case class RssItemInfo(url: String, title: String)
