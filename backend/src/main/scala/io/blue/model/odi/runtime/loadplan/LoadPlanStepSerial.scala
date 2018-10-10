package io.blue.model.odi.runtime.loadplan

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStepSerial


class LoadPlanStepSerial(o: OdiLoadPlanStepSerial) extends LoadPlanStepContainer(o) {

  val stepType = "SERIAL"

  @JsonIgnore
  var odiLoadPlanStepSerial: OdiLoadPlanStepSerial = odiLoadPlanElement.asInstanceOf[OdiLoadPlanStepSerial]
}