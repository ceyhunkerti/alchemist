package io.blue.model.odi.runtime.loadplan

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanElement
import oracle.odi.domain.support.AbstractOdiEntity

import io.blue.model.odi.support.OdiEntity


class LoadPlanElement(o: OdiLoadPlanElement) {

  var stepId: Number = o.getStepId

  var name: String = o.getName

  @JsonIgnore
  var odiLoadPlanElement: OdiLoadPlanElement = o
}