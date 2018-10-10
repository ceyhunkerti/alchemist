package io.blue.model.odi.topology

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.blue.model.odi.support.OdiEntity
import scala.beans.BeanProperty

import oracle.odi.domain.topology.OdiPhysicalAgent

object AgentStatus {
  val UP = "UP"
  val DOWN = "DOWN"
}


class PhysicalAgent extends OdiEntity{

  def this(o: OdiPhysicalAgent) = {
    this()
    init(o)
    host = o.getHostName
    port = o.getHostPort
    url = o.getURLString
    this.odiPhysicalAgent = o
  }

  @JsonIgnore
  var odiPhysicalAgent: OdiPhysicalAgent = _

  @BeanProperty
  var host: String = _

  @BeanProperty
  var port: Int = _

  @BeanProperty
  var url: String = _

  @BeanProperty
  var status: String = _
}