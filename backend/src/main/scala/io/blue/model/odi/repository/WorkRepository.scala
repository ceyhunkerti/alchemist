package io.blue.model.odi.repository

import java.util.Date
import javax.validation.constraints.{NotNull}
import com.fasterxml.jackson.annotation._


class WorkRepository {

  def this(id: Long) {
    this()
    this.id = id
  }

  var id: Long = _

  var name: String = _
}
