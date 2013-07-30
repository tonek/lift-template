package tonek.web

import net.liftweb.util._
import org.joda.time.format.DateTimeFormatterBuilder
import xml.NodeSeq
import java.util.{Calendar, Date}
import net.liftweb.http.{S, Templates}
import org.joda.time.DateTime

package object widgets extends WidgetsTrait

trait WidgetsTrait extends TimeHelpers with StringHelpers with ListHelpers
with SecurityHelpers with BindHelpers with HttpHelpers
with IoHelpers with BasicTypesHelpers
with ClassHelpers with ControlHelpers {
  lazy val dateFormat = new DateTimeFormatterBuilder()
    .appendDayOfMonth(1).appendLiteral(' ').appendMonthOfYearShortText().appendLiteral(' ')
    .appendTwoDigitYear(2050).toFormatter

  lazy val timeFormat = new DateTimeFormatterBuilder()
    .appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter

  def hiddenT(path: List[String]): NodeSeq = Templates("templates-hidden" :: path).get
  def hiddenT(path: String*): NodeSeq = hiddenT(path.toList)

  def format(date: Date): String = formatLong(date.getTime)
  def formatLong(timestamp: Long): String = S.?("dateFormat.long").format(
    dateFormat.print(timestamp), timeFormat.print(timestamp)
  )

  def format(calendar: Calendar): String = formatLong(calendar.getTimeInMillis)

  def format(dateTime: DateTime): String = formatLong(dateTime.getMillis)
}
