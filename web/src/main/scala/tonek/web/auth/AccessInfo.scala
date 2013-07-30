package tonek.web.auth

/**
 * @author anton.safonov
 */
case class AccessInfo(access_token: String, token_type: String, expires_in: Long, id_token: String)
