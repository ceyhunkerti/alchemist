package io.blue.model.odi.runtime.lpi

import java.lang.Number
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceElement
import oracle.odi.domain.support.AbstractOdiEntity

import io.blue.model.odi.support.OdiEntity
import io.blue.model.odi.runtime.loadplan.LoadPlan


class LoadPlanInstanceElement(o: OdiLoadPlanInstanceElement) {

  var stepId: Number = o.getStepId

  var name: String = o.getName

  @JsonIgnore
  var odiLoadPlanInstanceElement: OdiLoadPlanInstanceElement = o
}