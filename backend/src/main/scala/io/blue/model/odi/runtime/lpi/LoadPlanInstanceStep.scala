package io.blue.model.odi.runtime.lpi

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.{OdiLoadPlanInstanceStep, OdiLoadPlanInstanceRun}



class LoadPlanInstanceStep(o: OdiLoadPlanInstanceStep) extends LoadPlanInstanceElement(o) {

  var order: Int = o.getOrder

  var isEnabled: Boolean = o.isEnabled

  @JsonIgnore
  var odiLoadPlanInstanceStep: OdiLoadPlanInstanceStep = o

  var log: LoadPlanInstanceStepLog = _

  def getLog(run: OdiLoadPlanInstanceRun) = {
    new LoadPlanInstanceStepLog(this.odiLoadPlanInstanceStep.getLog(run))
  }

  def setLog(run: OdiLoadPlanInstanceRun): LoadPlanInstanceStep = {
    log = getLog(run)
    this
  }

  def setLog(run: LoadPlanInstanceRun): LoadPlanInstanceStep = setLog(run.odiLoadPlanInstanceRun)

}