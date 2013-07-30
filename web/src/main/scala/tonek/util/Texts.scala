package tonek.util

import net.liftweb.record.field.StringField
import xml._
import net.liftweb.util.Html5
import scala.xml.transform.{RuleTransformer, RewriteRule}
import xml.Text
import java.net.{URI, URL}
import tonek.common.Logging

/**
 * @author anton.safonov
 */
object Texts extends Logging {
  private val allowedTags = Set(
    "span", "b", "ol", "ul", "li", "pre", "code", "i", "p", "div", "blockquote", "img", "a", "br", "strong", "em"
  )

  val clearTagsRule = new RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case e: Elem if allowedTags.contains(e.label) => {
        e.copy(child = transform(e.child.toSeq))
      }
      case _: Text => n
      case e: Elem => Elem(null, "p", Null, TopScope, transform(e.child.toSeq): _*)
      case _ => Nil
    }
  }

  def wysiwygToHtmlEditable(text: String): String = text

  def wysiwygToHtml(field: StringField[_]): NodeSeq = Unparsed(field.is)

  def wysiwygToHtmlShorten(field: StringField[_]): NodeSeq = {
    val text = clearHtmlTags(field.is)
    val maxLen = 200
    if (text.length() <= maxLen) {
      Text(text)
    } else {
      val endIndex = text.substring(0, maxLen).lastIndexOf(' ')
      Text(text.substring(0, if (endIndex > 0) endIndex else maxLen) + " ...")
    }
  }

  def htmlToWysiwyg(html: String): String = cleanHtmlStr(html)

  def clearHtmlTags(html: String): String = {
    Html5.parse(html).map(clearHtmlTags).getOrElse("")
  }

  def toElement(html: String): Option[Elem] = Html5.parse("<span>" + html + "</span>").toOption

  def clearHtmlTags(html: NodeSeq): String = {
    html.map(transformToString).mkString(" ")
  }

  def fixUrls(html: String, pageUrl: String): NodeSeq = {
    Html5.parse(html).map(
      e => new RuleTransformer(fixAttrs(pageUrl)).transform(e)
    ).getOrElse(NodeSeq.Empty)
  }

  def fixAttrs(pageUrl: String): RewriteRule = {
    val baseUrl = new URL(pageUrl)

    new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case e: Elem if e.label == "a" && !isAbsoluteLink(e, "href") => {
          e.copy(child = transform(e.child.toSeq), attributes = getAbsUrl(e, "href"))
        }
        case e: Elem if e.label == "img" && !isAbsoluteLink(e, "src") => {
          e.copy(child = transform(e.child.toSeq), attributes = getAbsUrl(e, "src"))
        }
        case e: Elem if e.label == "iframe" && !isAbsoluteLink(e, "src") => {
          e.copy(child = transform(e.child.toSeq), attributes = getAbsUrl(e, "src"))
        }
        case e: Elem if e.attributes.asAttrMap.contains("style") => {
          e.copy(child = transform(e.child.toSeq), attributes = clearAttr(e, "style"))
        }
        case e => e
      }

      def getAbsUrl(e: Elem, attr: String): MetaData = {
        val newUrl = try {
          new URL(baseUrl, e.attributes.asAttrMap(attr)).toString
        } catch {
          case ex: Exception => {
            log.debug("Cannot build new url for " + e.attributes.get(attr), ex)
            "#"
          }
        }
        MetaData.update(e.attributes, e.scope, new UnprefixedAttribute(attr, newUrl, Null))
      }

      def clearAttr(e: Elem, attr: String): MetaData = {
        MetaData.update(e.attributes, e.scope, new UnprefixedAttribute(attr, "", Null))
      }
    }
  }

  def prepareHtml(html: String, maxLen: Int): String = {
    if (html.length > maxLen) {
      val result = clearHtmlTags(html)
      if (result.length > maxLen) result.substring(0, maxLen - 5) + " ..." else result
    } else html
  }

  private def isAbsoluteLink(e: Elem, attr: String): Boolean = {
    e.attributes.asAttrMap.get(attr) match {
      case Some(v)  => {
        try {
          new URI(v).isAbsolute
        } catch {
          case e: Exception => {
            log.debug("Cannot check whether url is absolute for " + v, e)
            false
          }
        }
      }
      case _ => false
    }
  }

  private def transformToString(n: Node): String = n match {
    case e: Text => e.text + " "
    case e: Elem => e.child.map(transformToString(_)).mkString(" ")
    case _ => ""
  }

  private def cleanHtmlStr(html: String): String = {
    val parsed = Html5.parse(prepareWysiwyg(html))
    new RuleTransformer(clearTagsRule).transform(parsed.get).toString().replaceAll("<br></br>", "<br/>")
  }

  private def prepareWysiwyg(html: String): String = {
    if (!html.startsWith("<span>")) {
      "<span>" + html + "</span>"
    } else {
      html
    }
  }

}
