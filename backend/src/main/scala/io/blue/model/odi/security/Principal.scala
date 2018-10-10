package io.blue.model.security

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

import oracle.odi.domain.security.OdiPrincipal
import oracle.odi.domain.support.AbstractOdiEntity

import io.blue.model.odi.support.OdiEntity


class Principal(o: OdiPrincipal) extends OdiEntity(o) {

  var isRole: Boolean = o.isRole

  var isSupervisor: Boolean = o.isSupervisor

  @JsonIgnore
  var odiPrincipal: OdiPrincipal = odiEntity.asInstanceOf[OdiPrincipal]
}