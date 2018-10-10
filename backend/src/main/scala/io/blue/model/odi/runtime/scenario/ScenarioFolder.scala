package io.blue.model.odi.runtime.scenario

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import oracle.odi.domain.runtime.scenario.{ OdiScenario, OdiScenarioFolder }

import io.blue.model.odi.support._
import io.blue.model.odi.runtime.loadplan._


class ScenarioFolder(o: OdiScenarioFolder) extends OdiEntity(o) {

  @JsonIgnore
  var odiScenarioFolder: OdiScenarioFolder = odiEntity.asInstanceOf[OdiScenarioFolder]

  var subFolders: List[ScenarioFolder] = if(odiScenarioFolder.getSubFolders != null) {
    odiScenarioFolder.getSubFolders.map(new ScenarioFolder(_)).toList
  } else {
    List()
  }

  var loadPlans: List[LoadPlan] = if(odiScenarioFolder.getLoadPlans != null) {
    odiScenarioFolder.getLoadPlans.map(new LoadPlan(_)).toList
  } else {
    List()
  }

  var scenarios: List[Scenario] = if(odiScenarioFolder.getScenarios != null) {
    odiScenarioFolder.getScenarios.map(new Scenario(_)).toList
  } else {
    List()
  }

}