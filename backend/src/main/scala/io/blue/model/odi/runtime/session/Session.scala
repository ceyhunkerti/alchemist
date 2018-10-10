package io.blue.model.odi.runtime.session

import java.util.Date
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import scala.collection.mutable.Buffer
import org.apache.commons.lang3.exception.ExceptionUtils
import com.fasterxml.jackson.annotation._
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.domain.runtime.session.{ Status, OdiSession, OdiSessionStepLog }

import io.blue.model.odi.support._
import io.blue.model.odi.runtime.session.blueprint._

class Session(o: OdiSession) extends OdiEntity(o)  {

  private val logger:Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @BeanProperty
  var id: java.lang.Number = o.getSessionId

  @BeanProperty
  var status: Status = o.getStatus

  @BeanProperty
  var startTime: Date = o.getStartTime

  @BeanProperty
  var endTime: Date = o.getEndTime

  @BeanProperty
  var errorMessage: String = o.getErrorMessage

  @BeanProperty
  var code: String = {
    var s: OdiSessionStepLog = null
    try {
      s = o.getRunningStepLog
    } catch {
      case e: Exception => logger.error(ExceptionUtils.getStackTrace(e))
    }
    if(s == null) {""} else {
      s.getSessionTaskLogs.map{log =>
        if(log.getSourceCommand != null && log.getTargetCommand != null) {
          s"""
          |In Source:
          |${if(log.getSourceCommand == null) "" else log.getSourceCommand}
          |-----------------------------------------
          |In Target
          |${if(log.getTargetCommand == null) "" else log.getTargetCommand}
        """.stripMargin
        } else {
          if(log.getSourceCommand != null) log.getSourceCommand else log.getTargetCommand
        }
      }.mkString("\n\n")
    }
  }

  @BeanProperty
  var agentName: String = o.getAgentName

  @JsonIgnore
  var sessionBlueprint: SessionBlueprint = new SessionBlueprint(o.getSessionBlueprint)

  @JsonIgnore
  var odiSession: OdiSession = o

  @JsonProperty("children")
  var sessionStepLogs: List[SessionStepLog] = _

  def withStepLogs = {
    this.sessionStepLogs = odiSession.getSessionStepLogs.toList.map(new SessionStepLog(_))
    this
  }

}