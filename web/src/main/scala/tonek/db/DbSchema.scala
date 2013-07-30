package tonek.db

import org.squeryl.Schema
import org.squeryl.internals.PrimaryKey
import tonek.web.model.User

/**
 * @author anton.safonov
 */
object DbSchema extends Schema {

  override def defaultColumnAttributesForKeyedEntityId(typeOfIdField: Class[_]) = Set(new PrimaryKey)

  val users = table[User]
}
