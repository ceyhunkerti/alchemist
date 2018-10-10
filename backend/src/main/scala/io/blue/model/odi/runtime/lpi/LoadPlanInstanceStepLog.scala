package io.blue.model.odi.runtime.lpi

import java.lang.Number
import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.{
  OdiLoadPlanInstanceStepLog,
  OdiLoadPlanInstanceRun,
  OdiLoadPlanInstanceStepSerial,
  OdiLoadPlanInstanceStepParallel,
  OdiLoadPlanInstanceStepRunScenario
}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize


class LoadPlanInstanceStepLog(o: OdiLoadPlanInstanceStepLog) {

  @JsonIgnore
  var odiLoadPlanInstanceStepLog: OdiLoadPlanInstanceStepLog = o

  var startTime: Date = _
  var endTime: Date = _
  var status: OdiLoadPlanInstanceRun.Status = _
  var sessionId: Number = _
  var children = List[LoadPlanInstanceStepLog]()
  var loadPlanInstanceStep: LoadPlanInstanceStep = _

  @JsonDeserialize(as=classOf[java.lang.Number])
  var internalId: java.io.Serializable = _

  @JsonDeserialize(as=classOf[java.lang.Number])
  var numericId: java.io.Serializable = _

  var globalId: String = _

  if (o != null) {
    globalId = o.getGlobalId
    numericId = o.getNumericId
    internalId = o.getInternalId
    startTime = o.getStartTime
    endTime = o.getEndTime
    status = o.getStatus
    sessionId = o.getSessionId
    // loadPlanInstanceStep = o.getLoadPlanInstanceStep match {
    //   case s: OdiLoadPlanInstanceStepSerial => new LoadPlanInstanceStepSerial(s)
    //   case p: OdiLoadPlanInstanceStepParallel => new LoadPlanInstanceStepParallel(p)
    //   case x: OdiLoadPlanInstanceStepRunScenario => new LoadPlanInstanceStep(x)
    // }
  }

  @JsonSerialize
  def duration = if(startTime != null) {
      (if(endTime == null) (new Date).getTime else endTime.getTime) - startTime.getTime
    } else 0
}