package io.blue.model.odi.runtime.loadplan

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStep
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStepRunScenario

import io.blue.model.odi.runtime.scenario.Tag

class LoadPlanStepRunScenario(o: OdiLoadPlanStepRunScenario) extends LoadPlanStep(o) {

  val stepType = "SCENARIO"

  var tag: Tag = new Tag(o.getScenarioTag)

  @JsonIgnore
  var odiLoadPlanStepRunScenario: OdiLoadPlanStepRunScenario =
    odiLoadPlanElement.asInstanceOf[OdiLoadPlanStepRunScenario]
}