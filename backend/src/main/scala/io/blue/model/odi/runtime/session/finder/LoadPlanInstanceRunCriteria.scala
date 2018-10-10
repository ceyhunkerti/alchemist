package io.blue.model.odi.runtime.session.finder

import java.util.Date
import javax.validation.constraints.{NotNull}
import com.fasterxml.jackson.annotation._


class LoadPlanInstanceRunCriteria {
  var statuses: Array[String] = _
  var loadPlanName: String = _
  var startTime: Date = _
}
