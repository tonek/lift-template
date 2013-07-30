package tonek.web.data.request

import tonek.web.auth.{RequestWrapper, AccessInfo}
import tonek.web.data.UserInfo

/**
 * @author anton.safonov
 */
object GoogleUserInfoRequest {
  def apply(accessInfo: AccessInfo): UserInfo = {
    new RequestWrapper(accessInfo, "https://www.googleapis.com/oauth2/v1/userinfo").jsonResult[UserInfo]
  }
}

object GoogleRssTagsRequest {
  def apply(accessToken: String): List[RssTagInfo] = {
    new RequestWrapper(accessToken, "http://www.google.com/reader/api/0/tag/list?output=json")
      .jsonResult[Tags].tags
  }
}

object GoogleRssFeedsRequest {
  def apply(accessToken: String): List[RssSubscriptionInfo] = {
    new RequestWrapper(accessToken, "http://www.google.com/reader/api/0/subscription/list?output=json")
      .jsonResult[RssSubscriptions].subscriptions
  }
}
