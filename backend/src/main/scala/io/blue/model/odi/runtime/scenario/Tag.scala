package io.blue.model.odi.runtime.scenario

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.scenario.{Tag => OdiTag}

class Tag(o: OdiTag) {

  var name: String = o.getName

  var version: String = o.getVersion

  @JsonIgnore
  var odiTag: OdiTag = o

}