package io.blue.model.odi.runtime.loadplan

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStepParallel


class LoadPlanStepParallel(o: OdiLoadPlanStepParallel) extends LoadPlanStepContainer(o) {

  val stepType = "PARALLEL"

  @JsonIgnore
  var odiLoadPlanStepParallel: OdiLoadPlanStepParallel = odiLoadPlanElement.asInstanceOf[OdiLoadPlanStepParallel]
}