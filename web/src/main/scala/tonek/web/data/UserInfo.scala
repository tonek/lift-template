package tonek.web.data

/**
 * @author anton.safonov
 */
case class UserInfo(id: String,
                    email: String,
                    verified_email: Boolean,
                    name: String,
                    given_name: Option[String],
                    family_name: String,
                    link: Option[String],
                    picture: Option[String],
                    gender: Option[String])
