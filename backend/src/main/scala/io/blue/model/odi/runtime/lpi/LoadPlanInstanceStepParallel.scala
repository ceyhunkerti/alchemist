package io.blue.model.odi.runtime.lpi

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceStepParallel


class LoadPlanInstanceStepParallel(o: OdiLoadPlanInstanceStepParallel) extends LoadPlanInstanceStepContainer(o) {

  val stepType = "PARALLEL"

  @JsonIgnore
  var odiLoadPlanInstanceStepParallel: OdiLoadPlanInstanceStepParallel = o

}