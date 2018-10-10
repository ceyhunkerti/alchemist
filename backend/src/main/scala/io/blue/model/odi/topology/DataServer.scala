package io.blue.model.odi.topology

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.annotation._

import oracle.odi.domain.topology.OdiDataServer
import oracle.odi.domain.util.ObfuscatedString

import io.blue.model.odi.support.OdiEntity

object DataServerStatus {
  val UNKNOWN ="UNKNOWN"
  val UP = "UP"
  val DOWN = "DOWN"
}

class DataServer extends OdiEntity {

  def this(o: OdiDataServer) {
    this()
    init(o)
    username = o.getUsername
    physicalSchemas = o.getPhysicalSchemas.map(new PhysicalSchema(_))
    instance = o.getServerInstanceName
    connectionSettings = new ConnectionSettings(o.getConnectionSettings)
    if(o.getPassword != null) {
      password = java.lang.String.valueOf(o.getPassword.getObfuscatedValue)
    }
    status = DataServerStatus.UNKNOWN
  }

  var username: String = _

  var password: String = _

  var instance: String = _

  var physicalSchemas: Iterable[PhysicalSchema] = _

  @JsonIgnore
  var connectionSettings: ConnectionSettings = _

  var status: String = _
}