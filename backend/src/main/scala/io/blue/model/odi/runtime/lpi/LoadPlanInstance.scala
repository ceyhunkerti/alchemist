package io.blue.model.odi.runtime.lpi

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstance
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import oracle.odi.domain.support.AbstractOdiEntity

import io.blue.model.odi.support.OdiEntity
import io.blue.model.odi.runtime.loadplan.LoadPlan

class LoadPlanInstance(o: OdiLoadPlanInstance, withSteps: Boolean = false) extends OdiEntity{

  init(o)

  var loadPlan = new LoadPlan(o.getLoadPlan)

  @JsonIgnore
  var odiLoadPlanInstance: OdiLoadPlanInstance = o

  var rootStep: LoadPlanInstanceStepSerial = _

  var runCount: Int = o.getRuns.size

  if (withSteps) {
    rootStep = new LoadPlanInstanceStepSerial(o.getRootStep)
  }

}