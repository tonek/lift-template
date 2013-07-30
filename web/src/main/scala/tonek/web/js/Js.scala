package tonek.web.js

import xml.NodeSeq
import net.liftweb.http.js.{HtmlFixer, JsCmd}

/**
 * @author anton.safonov
 */
object Js {

  case class ReplaceById(id: String, html: NodeSeq) extends JsCmd with HtmlFixer {
    def toJsCmd = ReplaceWith("#"+id, html).toJsCmd
  }

  case class ReplaceWith(selector: String, html: NodeSeq) extends JsCmd with HtmlFixer {
    def toJsCmd = "$('" + selector + "').replaceWith(" + fixHtmlFunc("inline", html)(s => s) + ");"
  }

  case class NavigateToBody(selector: String) extends JsCmd {
    def toJsCmd = "$.scrollTo($('%s').position().top - (window.innerHeight / 3), 300);".format(selector)
  }

  case class NavigateTo(wrapper: String, selector: String) extends JsCmd {
    def toJsCmd = "$('%s').scrollTo($('%s').position().top - (window.innerHeight / 3), 300);".format(wrapper, selector)
  }

  object InitValidation extends JsCmd {
    def toJsCmd = "$('.validatable').validate();"
  }

  object CloseAlerts extends JsCmd {
    def toJsCmd = "$('.alert-message.error .close').click();"
  }

  case class RemoveElement(id: String) extends JsCmd {
    def toJsCmd = "$('#%s').remove();".format(id)
  }

  case class AddClass(id: String, clazz: String) extends JsCmd {
    def toJsCmd = "$('#%s').addClass('%s')".format(id, clazz)
  }

  case class RemoveClass(id: String, clazz: String) extends JsCmd {
    def toJsCmd = "$('#%s').removeClass('%s')".format(id, clazz)
  }

}
