package io.blue.model.odi.runtime.session

import java.util.Date
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import org.apache.commons.lang3.exception.ExceptionUtils
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.domain.runtime.session.{ Status, OdiSessionTaskLog }

class SessionTaskLog(o: OdiSessionTaskLog) {

  @JsonDeserialize(as=classOf[java.lang.Number])
  var internalId: java.io.Serializable = o.getInternalId

  var name1: String = o.getTaskName1

  var name2: String = if(o.getTaskName2 == null) "" else o.getTaskName2

  var name3: String = if(o.getTaskName3 == null) "" else o.getTaskName3

  var status: Status = o.getStatus

  var startTime: Date = o.getStartTime

  var endTime: Date = o.getEndTime

  var duration: Int = o.getDuration

  var errorMessage: String = o.getErrorMessage

  var deleteCount: Int = o.getDeleteCount
  var insertCount: Int = o.getInsertCount

  var sourceCommand: String = o.getResolvedSourceCommand
  var targetCommand: String = o.getResolvedTargetCommand

  var sourceDataServerName: String = o.getSourceDataServerName
  var targetDataServerName: String = o.getTargetDataServerName

  var sourceLogicalSchemaName: String = o.getSourceLogicalSchemaName
  var targetLogicalSchemaName: String = o.getTargetLogicalSchemaName

  var sourceContextCode: String = o.getSourceContextCode
  var targetContextCode: String = o.getTargetContextCode

  var children: List[SessionTaskLog] = o.getChildTaskLogs.toList.map(new SessionTaskLog(_))

  @JsonIgnore
  var odiSessionTaskLog: OdiSessionTaskLog = o




}