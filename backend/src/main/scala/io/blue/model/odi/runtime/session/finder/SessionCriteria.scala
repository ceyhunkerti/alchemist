package io.blue.model.odi.runtime.session.finder

import java.util.Date
import javax.validation.constraints.{NotNull}
import com.fasterxml.jackson.annotation._


class SessionCriteria {
  var name: String = _
  var statuses: List[String] = _
  var startTime: Date = _
  var endTime: Date = _
  var submitterName: String = _
  var agentNames: List[String] = _
  var scenarioName: String = _
}
