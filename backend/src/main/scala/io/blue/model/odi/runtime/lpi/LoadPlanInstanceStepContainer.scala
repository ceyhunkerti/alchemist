package io.blue.model.odi.runtime.lpi

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.{
  OdiLoadPlanInstanceStepContainer,
  OdiLoadPlanInstanceStepSerial,
  OdiLoadPlanInstanceStepParallel
}


class LoadPlanInstanceStepContainer(o: OdiLoadPlanInstanceStepContainer) extends LoadPlanInstanceStep(o) {


  @JsonProperty("children")
  var childrenSteps: List[LoadPlanInstanceStep] = List()

  @JsonIgnore
  var odiLoadPlanInstanceStepContainer: OdiLoadPlanInstanceStepContainer = o

  if (o.getChildrenSteps != null) {
    childrenSteps = o.getChildrenSteps.map{ step =>
      step match {
        case s: OdiLoadPlanInstanceStepSerial => new LoadPlanInstanceStepSerial(s)
        case p: OdiLoadPlanInstanceStepParallel => new LoadPlanInstanceStepParallel(p)
        case _ => new LoadPlanInstanceStep(step)
      }
    }.toList
  }

}