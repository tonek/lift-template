package tonek.web.model

import net.liftweb.record.field.LongField
import scala.util.Random
import net.liftweb.squerylrecord.KeyedRecord
import org.squeryl.annotations.Column
import net.liftweb.record.Record

/**
 * @author anton.safonov
 */
trait LongRandomKeyedRecord[T <: LongRandomKeyedRecord[T]] extends Record[T] with KeyedRecord[Long]  {
  self: T =>
  @Column(name = "id")
  val idField = new LongField(self) {
    override def defaultValue = Random.nextLong()
  }
}
