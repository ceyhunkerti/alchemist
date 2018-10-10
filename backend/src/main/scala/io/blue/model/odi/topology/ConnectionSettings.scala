package io.blue.model.odi.topology

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import oracle.odi.domain.topology.AbstractOdiDataServer.IConnectionSettings

class ConnectionSettings {

  def this(o: IConnectionSettings) {
    this()
    jdbcUrl = o.getJdbcUrl
    driverClass = o.getDriverName
    connectionProperties = o.getConnectionProperties
    securityPrincipal = o.getSecurityPrincipal
    securityAuthentication = o.getSecurityAuthentication
  }

  var jdbcUrl: String = _

  var driverClass: String = _

  var connectionProperties: java.util.Map[String, String] = _

  var securityPrincipal: String = _

  var securityAuthentication: String = _
}