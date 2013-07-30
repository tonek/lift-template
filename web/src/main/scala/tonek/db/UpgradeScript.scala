package tonek.db

import org.squeryl.internals.PrimaryKey

/**
 * @author anton.safonov
 */
trait UpgradeScript {
  def upgradeSchema()
  def id: String
}