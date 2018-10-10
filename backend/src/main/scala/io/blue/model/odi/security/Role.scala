package io.blue.model.security

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

import oracle.odi.domain.security.OdiRole



class Role(o: OdiRole) extends Principal(o) {

  @JsonIgnore
  var odiRole: OdiRole = odiEntity.asInstanceOf[OdiRole]
}