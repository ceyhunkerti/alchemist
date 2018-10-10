package io.blue.model.odi.runtime.session

import java.util.Date
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import org.apache.commons.lang3.exception.ExceptionUtils
import com.fasterxml.jackson.annotation._
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.domain.runtime.session.{ Status, OdiSession, OdiSessionStepLog }

class SessionStepLog(o: OdiSessionStepLog) {

  var name: String = o.getName

  var status: Status = o.getStatus

  var startTime: Date = o.getStartTime

  var endTime: Date = o.getEndTime

  @JsonIgnore
  var odiSessionStepLog: OdiSessionStepLog = o

  @JsonProperty("children")
  var sessionTaskLogs: List[SessionTaskLog] = o.getSessionTaskLogs.toList.map(new SessionTaskLog(_))
}