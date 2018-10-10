package io.blue.model.security

import java.lang.Number
import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

import oracle.odi.domain.security.OdiUser
import oracle.odi.domain.support.AbstractOdiEntity

class User(o: OdiUser) extends Principal(o) {

  var userId: Number = o.getUserId

  @JsonIgnore
  var odiUser: OdiUser = odiEntity.asInstanceOf[OdiUser]
}