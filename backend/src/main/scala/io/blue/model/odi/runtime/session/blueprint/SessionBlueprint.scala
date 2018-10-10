package io.blue.model.odi.runtime.session.blueprint

import java.lang.Number
import java.util.Date
import scala.collection.mutable.Buffer
import scala.beans.BeanProperty
import oracle.odi.domain.runtime.step.StepType
import scala.collection.JavaConversions._
import com.fasterxml.jackson.annotation._

import oracle.odi.domain.runtime.session.blueprint.OdiSessionBlueprint
import io.blue.model.odi.runtime.scenario.Scenario

class SessionBlueprint {

  def this(o: OdiSessionBlueprint) {
    this()
    this.odiSessionBlueprint = o
    internalId = o.getInternalId
    sessionBlueprintId = o.	getSessionBlueprintId
    sourceScenarioId = o.getSourceScenarioId
  }

  var internalId: java.io.Serializable = _

  var sessionBlueprintId: Number = _

  var sourceScenarioId: Number = _

  var scenario: Scenario = _

  @JsonIgnore
  var odiSessionBlueprint: OdiSessionBlueprint = _

}