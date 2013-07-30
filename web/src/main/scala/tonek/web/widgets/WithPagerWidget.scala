package tonek.web.widgets

import net.liftweb.util.Helpers

/**
 * @author anton.safonov
 */
abstract class WithPagerWidget[T](pager: Pager[T]) extends Widget{
  lazy val btnId = "btn-q-pager-" + Helpers.nextNum
  lazy val wrpId = "wrapper-" + Helpers.nextNum

//  def draw() = {
//    val items = pager.loadPage()
//    val css: CssSel =
//      ".rss-item" #> RssItemsWidget.draw(items, true) &
//      "#rss-list-items-wrapper [id]" #> wrpId &
//      ".load-more-items" #> {
//        if (pager.hasMore) SHtml.ajaxButton(S.?("rssItems.show.more"), loadMore _, "id" -> btnId) else NodeSeq.Empty
//      } & ".init-scroll-script *" #> {
//        val invoke = SHtml.ajaxInvoke(loadMore _)
//        "$(document).ready(function() {initInfiniteScroll(function(){%s})})".format(invoke._2.toJsCmd)
//      }
//
//    css(hiddenT("rss", "rss_list_pager"))
//  }
//
//  private def loadMore(): JsCmd = {
//    val items = pager.loadPage()
//    val itemId = items.headOption.map(RssItemsWidget.getItemId _)
//    JqJsCmds.AppendHtml(wrpId, RssItemsWidget.draw(items, true)) & {
//      if (!pager.hasMore) JsCmds.Replace(btnId, NodeSeq.Empty) else JsCmds.Noop
//    } & {
//      /*if (itemId.isDefined) Run("$('.content-wrapper').scrollTo($('#%s').height(), 300);".format(wrpId)) else JsCmds.Noop*/
//      Run("loaded()")
//    } & Run("initScoresTooltips();")
//  }
}

trait Pager[T] {
  protected val pageSize = 40
  protected var currentPage = 0
  private var hasMoreElements = true

  def loadPage(): List[T] = {
    val items = loadCurrentPage()
    currentPage = currentPage + 1
    hasMoreElements = items.size == pageSize
    items
  }

  def hasMore = hasMoreElements

  protected def loadCurrentPage(): List[T]
}