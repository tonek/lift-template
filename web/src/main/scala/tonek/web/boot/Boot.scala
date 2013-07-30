package tonek.web.boot

import net.liftweb._
import common._
import http._
import js.JsCmds.Run
import java.util.Locale
import sitemap.Loc.{Test, Hidden}
import sitemap.{**, SiteMap, Menu}
import util._
import Helpers._
import java.lang.reflect.InvocationTargetException
import tonek.web.auth.OauthAuthorizer
import tonek.web.model.User
import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.Session
import java.sql.DriverManager
import net.liftweb.squerylrecord.RecordTypeMode._
import tonek.util.PostgreSqlAdapter
import tonek.db.DbUpgrades

class Boot {

  def boot() {

    Logger.setup = Full(() => Log4j.withFile(getClass.getResource("/log4j.properties")))
    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    initDb()

    LiftRules.ajaxDefaultFailure = Empty
    LiftRules.ajaxRetryCount = Full(0)
    val russianLocale = new Locale("ru", "RU")
    Locale.setDefault(russianLocale)
    LiftRules.localeCalculator = _ => russianLocale
    LiftRules.resourceNames = "i18n/resources" :: LiftRules.resourceNames
    LiftRules.resourceNames = "i18n/misc" :: LiftRules.resourceNames

    // where to search snippet
    LiftRules.addToPackages("tonek.web")

    buildSiteMap()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(
      () =>
        LiftRules.jsArtifacts.show("ajax-loader").cmd &
          Run("$('.ajax-submit').attr('disabled','true')")
    )

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(
      () =>
        LiftRules.jsArtifacts.hide("ajax-loader").cmd &
          Run("$('.ajax-submit').removeAttr('disabled')")
    )
    LiftRules.noticesAutoFadeOut.default.set(
      (notices: NoticeType.Value) => Full(5 seconds, 1 seconds)
    )

    LiftRules.ajaxPostTimeout = 30000

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })

    LiftRules.exceptionHandler.prepend {
      case (_, r, exception) if exception.isInstanceOf[InvocationTargetException] => {
        val e = exception.asInstanceOf[InvocationTargetException]
        val target = e.getTargetException
        if(target != null && target.getMessage != null && target.getMessage.contains("not found")) {
          RedirectResponse("/404")
        } else {
          RedirectResponse("/error")
        }
      }
    }

    LiftRules.dispatch.append(OauthAuthorizer)

    rewriteRequests()
  }

  def rewriteRequests() {
//    LiftRules.statefulRewrite.append(
//      NamedPF("AnyReadRewriter") {
//        case RewriteRequest(ParsePath("feed" :: url :: Nil, _, _, _), _, _) =>
//          RewriteResponse(ParsePath("view" :: "feed" :: Nil, "", true, false), Map("url" -> url), true)
//      }
//    )
  }

  def buildSiteMap() {
    val loggedIn = Test(r => User.loggedIn_?)
    val superUser = Test(r => User.superUser_?)
    val autoLoginDefined = Test(r => Props.getLong("auto.login").isDefined)

    val userSiteMap: List[Menu] = List(
      User.loginMenuLoc,
      User.createUserMenuLoc,
      User.lostPasswordMenuLoc,
      User.resetPasswordMenuLoc,
      User.editUserMenuLoc,
      User.changePasswordMenuLoc,
      User.validateUserMenuLoc,
      User.logoutMenuLoc
    ).flatten(a => a)

    val entries = List(
      Menu.i("Index") / "index",
      Menu.i("Search") / "search",
      Menu.i("OAuth") / "auth" / "oauthcallback" / ** >> Hidden,
      Menu.i("Auto login") / "autoLogin"  / ** >> autoLoginDefined
    ) ::: userSiteMap

    LiftRules.setSiteMap(SiteMap(entries: _*))
  }

  def initDb() {
    val url = Props.get("db.url").get
    val driver = Props.get("db.driver").get
    Class.forName(driver)
    SquerylRecord.initWithSquerylSession(Session.create(DriverManager.getConnection(url), new PostgreSqlAdapter))

    S.addAround(new LoanWrapper {
      override def apply[T](f: => T): T = {
        val resultOrExcept = inTransaction {
          try {
            Right(f)
          } catch {
            case e: LiftFlowOfControlException => Left(e)
          }
        }

        resultOrExcept match {
          case Right(result) => result
          case Left(except) => throw except
        }
      }
    })
    DbUpgrades.upgrade()
//    inTransaction(DbSchema.create)
  }
}