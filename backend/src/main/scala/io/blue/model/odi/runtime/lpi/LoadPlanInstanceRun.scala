package io.blue.model.odi.runtime.lpi

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.{ Status, OdiLoadPlanInstanceRunId }
import oracle.odi.domain.runtime.lpi.{
  OdiLoadPlanInstanceStepSerial,
  OdiLoadPlanInstanceStepParallel,
  OdiLoadPlanInstanceStepContainer
}

import io.blue.model.odi.support.OdiEntity

class LoadPlanInstanceRun(o: OdiLoadPlanInstanceRun, withSteps: Boolean = false) extends OdiEntity(o) {

  var id: String = {
    val i = internalId.asInstanceOf[OdiLoadPlanInstanceRunId]
    s"${i.getLoadPlanInstance}-${i.getRunCount}"
  }

  var runCount: Long = o.getRunCount

  var status: OdiLoadPlanInstanceRun.Status = o.getStatus

  var startTime: Date = o.getStartTime

  var endTime: Date = o.getEndTime

  @JsonSerialize
  def duration =
    (if(endTime == null) (new Date).getTime else endTime.getTime) - startTime.getTime

  var avgDuration: Long = _

  var loadPlanInstance: LoadPlanInstance = new LoadPlanInstance(o.getLoadPlanInstance, withSteps)

  @JsonIgnore
  var odiLoadPlanInstanceRun: OdiLoadPlanInstanceRun = o

  var logs: LoadPlanInstanceStepLog = _

  var errorMessage: String = o.getErrorMessage

  def withLogs = {
    // val root = loadPlanInstance.rootStep
    // def findLogs(stepLog: LoadPlanInstanceStepLog): LoadPlanInstanceStepLog = {

    //   if(stepLog != null && stepLog.odiLoadPlanInstanceStepLog != null) {
    //     var step = stepLog.loadPlanInstanceStep
    //     step match {
    //       case s: LoadPlanInstanceStepContainer =>
    //         if (!s.childrenSteps.isEmpty) {
    //           s.childrenSteps.foreach { loadPlanInstanceStep =>
    //             var odiLog = loadPlanInstanceStep.odiLoadPlanInstanceStep.getLog(odiLoadPlanInstanceRun)
    //             var log = new LoadPlanInstanceStepLog(odiLog)
    //             if (odiLog == null) {
    //               log.loadPlanInstanceStep = loadPlanInstanceStep
    //             }
    //             stepLog.children ::= findLogs(log)
    //           }
    //         }
    //       case _ =>
    //     }
    //   }
    //   return stepLog
    // }
    // logs = findLogs(rootLog)
    this
  }

}