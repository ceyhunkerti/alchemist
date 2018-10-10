package io.blue.model.odi.runtime.loadplan

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.{
  OdiLoadPlanStepContainer,
  OdiLoadPlanStepSerial,
  OdiLoadPlanStepParallel,
  OdiLoadPlanStepRunScenario
}


class LoadPlanStepContainer(o: OdiLoadPlanStepContainer) extends LoadPlanStep(o) {


  @JsonProperty("children")
  var childrenSteps: List[LoadPlanStep] = List()

  @JsonIgnore
  var odiLoadPlanStepContainer: OdiLoadPlanStepContainer = odiLoadPlanElement.asInstanceOf[OdiLoadPlanStepContainer]

  if (o.getChildrenSteps != null) {
    childrenSteps = o.getChildrenSteps.map{ step =>
      step match {
        case s: OdiLoadPlanStepSerial => new LoadPlanStepSerial(s)
        case p: OdiLoadPlanStepParallel => new LoadPlanStepParallel(p)
        case r: OdiLoadPlanStepRunScenario => new LoadPlanStepRunScenario(r)
        case _ => new LoadPlanStep(step)
      }
    }.toList
  }

}