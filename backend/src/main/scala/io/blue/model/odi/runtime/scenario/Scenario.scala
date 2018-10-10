package io.blue.model.odi.runtime.scenario

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import oracle.odi.domain.runtime.scenario.OdiScenario

import io.blue.model.odi.support._

class Scenario(o: OdiScenario) extends OdiEntity(o) {

  var version: String = o.getVersion

  var description: String = o.getDescription

  @JsonIgnore
  var odiScenario: OdiScenario = odiEntity.asInstanceOf[OdiScenario]

  var hasFolder = odiScenario.getScenarioFolder != null

}