package tonek.web.model

import net.liftweb.record.{MetaMegaProtoUser, MegaProtoUser}
import util.Random
import net.liftweb.util.SecurityHelpers._
import net.liftweb.common.{Full, Box}
import xml.Elem
import net.liftweb.http.{RequestVar, SHtml, S}
import net.liftweb.util.Props
import net.liftweb.http.SHtml.BasicElemAttr
import net.liftweb.record.field.{LongField, StringField}
import tonek.web.BackUrl
import tonek.web.data.UserInfo
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.KeyedEntity
import tonek.db.DbSchema

/**
 * @author anton.safonov
 */
class User extends MegaProtoUser[User] with KeyedEntity[LongField[User]] {
  override def meta = User

  override lazy val id = new MyMappedLongClass(this) {
    override def name = "id"

    override def defaultValue = Random.nextLong()
  }

  override lazy val password = new MyPassword(this) {
    salt.set("tonek")

    override def match_?(toTest: String): Boolean =
      get == ('\f' + hash("{"+toTest+"} salt={"+salt.get+"}"))

    override def set_!(in: Box[String]) = {
      in match {
        case Full(v) if v.isEmpty => in
        case Full(v) if v.charAt(0) == '\f' => in
        case _ => super.set_!(in).map('\f' + _)
      }
    }
  }

  val gauthCode = new StringField(this, 1000)

  override def valUnique(errorMsg: => String)(email: String) = Nil
}

object User extends User with MetaMegaProtoUser[User] {
  @volatile var byEmail: (String) => Option[User] = email => DbSchema.users.where(_.email === email).headOption

  def byId(id: Long): Option[User] = DbSchema.users.where(_.id === id).headOption

  def byIds(ids: Traversable[Long]): List[User] = DbSchema.users.where(_.id in ids).toList

  def currentId(): Long = User.currentUserId.get.toLong

  def create(userInfo: UserInfo, gauthCode: String): User = {
    DbSchema.users.insert(
      User.createRecord.email(userInfo.email).firstName(userInfo.given_name).lastName(userInfo.family_name)
      .gauthCode(gauthCode)
    )
  }

  def updateGauthCode(id: Long, gauthCode: String): User = {
    update(DbSchema.users)(u =>
      where(u.id === id)
      set(u.gauthCode := gauthCode)
    )
    byId(id).get
  }

//  def update(id: Long, firstName: String, lastName: String) {
//    val query = User.where(_.id eqs id)
//      .modify(_.firstName setTo firstName)
//      .and(_.lastName setTo lastName)
//
//    query.updateOne()
//  }

  protected def userFromStringId(id: String) = byId(id.toLong)

  protected def findUserByUniqueId(id: String) = DbSchema.users.where(_.uniqueId === id).headOption

  protected def findUserByUserName(email: String) = byEmail(email)

  def surroundXhtml(el: Elem): Elem = {
    <div id="main" class="lift:surround?with=default;at=content-wrapper content">
      {el}
    </div>
  }

  override def login = {
    if (!S.post_?) {
      val backUrl = BackUrl.get
      backUrl match {
        case Full(u) if !u.isEmpty => loginRedirect(backUrl)
        case _ => loginRedirect(S.referer)
      }
    }
    super.login
  }

  override def loginXhtml = surroundXhtml(super.loginXhtml)

  override def signupXhtml(user: User) = surroundXhtml(super.signupXhtml(user))

  override def lostPasswordXhtml = surroundXhtml(super.lostPasswordXhtml)

  override def passwordResetXhtml = surroundXhtml(super.passwordResetXhtml)

  override def editXhtml(user: User) = surroundXhtml(super.editXhtml(user))

  override def changePasswordXhtml = surroundXhtml(super.changePasswordXhtml)

  override def signupFields: List[FieldPointerType] = List(email, password)

  //override protected def loginMenuLocParams = Boot.userGroup :: super.loginMenuLocParams

  //override protected def createUserMenuLocParams = Boot.userGroup :: super.createUserMenuLocParams

  override def skipEmailValidation = Props.getBool("users.email.skipAuthorization", false)

  override def standardSubmitButton(name: String, func: () => Any) = {
    SHtml.submit(name, func, BasicElemAttr("class", "ar-submit"))
  }
}

object CurrentUser extends RequestVar[User](
  if (User.loggedIn_?) {
    User.byId(User.currentId()).getOrElse(null)
  } else {
    null
  }
)
