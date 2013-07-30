package tonek.db

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.record.field.{DateTimeField, StringField}
import net.liftweb.squerylrecord.RecordTypeMode._
import java.util.Calendar
import org.squeryl.annotations._

/**
 * @author anton.safonov
 */
class ExecutedScript extends Record[ExecutedScript] with KeyedRecord[String] {
  def meta = ExecutedScript

  @Column(name = "id")
  val idField = new StringField(this, 100)
  val date = new DateTimeField(this)
}

object ExecutedScript extends ExecutedScript with MetaRecord[ExecutedScript] {
  def getAll: List[ExecutedScript] = from(ExecutedScriptsSchema.executedScripts)(select(_)).toList

  def register(script: UpgradeScript) {
    val executed = ExecutedScript.createRecord.date(Calendar.getInstance()).idField(script.id)
    ExecutedScriptsSchema.executedScripts.insert(executed)
  }
}
