package tonek.db

import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.Schema
import java.sql.SQLException
import tonek.common.Logging

/**
 * @author anton.safonov
 */
object ExecutedScriptsSchema extends Schema {
  val executedScripts = table[ExecutedScript]
}

object DbUpgrades extends Logging{

  private val scripts: List[UpgradeScript] = List()

  def upgrade() {
    val executedIds = initExecutedScriptAndGetAll().map(_.id).toSet
    scripts.filterNot(s => executedIds.contains(s.id)).foreach(s => executeScript(s))
  }

  private def initExecutedScriptAndGetAll(): List[ExecutedScript] = {
    log.debug("Initializing executed scripts")
    try {
      val result = inTransaction(ExecutedScript.getAll)
      log.debug("Executed scripts already exists")
      result
    } catch {
      case e: Exception => {
        inTransaction(ExecutedScriptsSchema.create)
        log.debug("Executed scripts created")
        inTransaction(ExecutedScript.getAll)
      }
    }
  }

  private def executeScript(script: UpgradeScript) {
    inTransaction{
      log.debug("Executing script " + script.id)
      script.upgradeSchema()
      ExecutedScript.register(script)
      log.debug("Script " + script.id + " executed")
    }
  }
}
