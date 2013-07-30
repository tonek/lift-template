package tonek.common

import org.slf4j.{Marker, Logger, LoggerFactory}

/**
 * @author anton.safonov
 */
trait Logging {
  protected lazy val log: Log = new Log(LoggerFactory.getLogger(this.getClass))
}

class Log(logger: Logger)  {

  def assertLog(assertion: Boolean, msg: => String) = if (assertion) info(msg)

  def trace(msg: => AnyRef) = if (logger.isTraceEnabled) logger.trace(String.valueOf(msg))
  def trace(msg: => AnyRef, t: Throwable) = if (logger.isTraceEnabled) logger.trace(String.valueOf(msg), t)
  def trace(msg: => AnyRef, marker:  Marker) = if (logger.isTraceEnabled) logger.trace(marker,String.valueOf(msg))
  def trace(msg: => AnyRef, t: Throwable, marker: => Marker) = if (logger.isTraceEnabled) logger.trace(marker,String.valueOf(msg), t)
  def isTraceEnabled = logger.isTraceEnabled

  def debug(msg: => AnyRef) = if (logger.isDebugEnabled) logger.debug(String.valueOf(msg))
  def debug(msg: => AnyRef, t:  Throwable) = if (logger.isDebugEnabled) logger.debug(String.valueOf(msg), t)
  def debug(msg: => AnyRef, marker: Marker) = if (logger.isDebugEnabled) logger.debug(marker, String.valueOf(msg))
  def debug(msg: => AnyRef, t: Throwable, marker: Marker) = if (logger.isDebugEnabled) logger.debug(marker, String.valueOf(msg), t)
  def isDebugEnabled = logger.isDebugEnabled

  def info(msg: => AnyRef) = if (logger.isInfoEnabled) logger.info(String.valueOf(msg))
  def info(msg: => AnyRef, t: => Throwable) = if (logger.isInfoEnabled) logger.info(String.valueOf(msg), t)
  def info(msg: => AnyRef, marker: Marker) = if (logger.isInfoEnabled) logger.info(marker,String.valueOf(msg))
  def info(msg: => AnyRef, t: Throwable, marker: Marker) = if (logger.isInfoEnabled) logger.info(marker,String.valueOf(msg), t)
  def isInfoEnabled = logger.isInfoEnabled

  def warn(msg: => AnyRef) = if (logger.isWarnEnabled) logger.warn(String.valueOf(msg))
  def warn(msg: => AnyRef, t: Throwable) = if (logger.isWarnEnabled) logger.warn(String.valueOf(msg), t)
  def warn(msg: => AnyRef, marker: Marker) = if (logger.isWarnEnabled) logger.warn(marker,String.valueOf(msg))
  def warn(msg: => AnyRef, t: Throwable, marker: Marker) = if (logger.isWarnEnabled) logger.warn(marker,String.valueOf(msg), t)
  def isWarnEnabled = logger.isWarnEnabled

  def error(msg: => AnyRef) = if (logger.isErrorEnabled) logger.error(String.valueOf(msg))
  def error(msg: => AnyRef, t: Throwable) = if (logger.isErrorEnabled) logger.error(String.valueOf(msg), t)
  def error(msg: => AnyRef, marker: Marker) = if (logger.isErrorEnabled) logger.error(marker,String.valueOf(msg))
  def error(msg: => AnyRef, t: Throwable, marker: Marker) = if (logger.isErrorEnabled) logger.error(marker,String.valueOf(msg), t)
  def isErrorEnabled = logger.isErrorEnabled

}
