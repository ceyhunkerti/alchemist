package io.blue.model.odi.runtime.loadplan

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import oracle.odi.domain.runtime.loadplan.{OdiLoadPlan, OdiLoadPlanStepSerial}

import io.blue.model.odi.support.OdiEntity
import io.blue.model.odi.runtime.scenario._


class LoadPlan(o: OdiLoadPlan) extends OdiEntity(o) {

  var avgDuration: Long = _

  var errorCount: Long = _

  var description: String = o.getDescription

  @JsonIgnore
  var odiLoadPlan: OdiLoadPlan = odiEntity.asInstanceOf[OdiLoadPlan]

  var hasFolder = odiLoadPlan.getScenarioFolder != null

  var rootStep: LoadPlanStepSerial = _

  def withSteps = {
    rootStep = new LoadPlanStepSerial(o.getRootStep)
    this
  }


}