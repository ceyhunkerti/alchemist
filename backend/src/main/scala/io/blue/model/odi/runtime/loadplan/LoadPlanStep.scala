package io.blue.model.odi.runtime.loadplan

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.{OdiLoadPlanStep}



class LoadPlanStep(o: OdiLoadPlanStep) extends LoadPlanElement(o) {

  var order: Int = o.getOrder

  var isEnabled: Boolean = o.isEnabled

  @JsonIgnore
  var odiLoadPlanStep: OdiLoadPlanStep = odiLoadPlanElement.asInstanceOf[OdiLoadPlanStep]

}