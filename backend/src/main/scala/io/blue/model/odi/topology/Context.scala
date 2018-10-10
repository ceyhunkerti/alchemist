package io.blue.model.odi.topology

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.blue.model.odi.support.OdiEntity
import scala.beans.BeanProperty

import oracle.odi.domain.topology.OdiContext

class Context(o: OdiContext) extends OdiEntity(o){

  var code: String = o.getCode

  @JsonIgnore
  var odiContext = odiEntity.asInstanceOf[OdiContext]

}