package tonek.lib

import net.liftweb.http.RequestVar

/**
 * @author anton.safonov
 */
class ExtRequestVar[T](dflt: => T) extends RequestVar(dflt){
  def test(value: T): Boolean = value.equals(this.get)
}
