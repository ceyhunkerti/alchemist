package io.blue.controller

import java.util.Date
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse


import javax.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation._
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.annotation._

import io.blue.model.Connection
import io.blue.service.ConnectionService

@RestController
@RequestMapping(Array("/api/v1/connections"))
class ConnectionController @Autowired()(private val connectionService: ConnectionService) {

  @RequestMapping(method = Array(RequestMethod.GET) )
  def findAll = connectionService.findMyConnections

  @RequestMapping(method = Array(RequestMethod.POST))
  def create(@RequestBody connection: Connection) =
    connectionService.create(connection)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.PUT))
  def update(@PathVariable("id") id: Long, @RequestBody connection: Connection) =
    connectionService.update(id, connection)

  @RequestMapping(value = Array("/test"), method = Array(RequestMethod.POST))
  def test(@RequestBody connection: Connection): Boolean =
    connectionService.test(connection)

  @RequestMapping(value = Array("/test/{id}"), method = Array(RequestMethod.GET))
  def test(@PathVariable("id") id: Long): Boolean = connectionService.test(id)

  @RequestMapping(value = Array("/work-repos"), method = Array(RequestMethod.POST))
  def findWorkRepos(@RequestBody connection: Connection) =
    connectionService.findWorkRepos(connection)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.DELETE))
  def delete(@PathVariable("id") id: Long) = connectionService.delete(id)

  @RequestMapping(value=Array("/export/{id}"), method=Array(RequestMethod.GET))
  def export(@PathVariable("id") id: Long, response: HttpServletResponse): Unit = {
    val now = new Date
    val df = new SimpleDateFormat("yyyyMMddHHmmss")
    val date = df.format(now)
    val (exp, connection) = connectionService.export(id)
    response.setContentType("application/octet-stream; charset=utf-8")
    response.setHeader("Content-Disposition", s"attachment; filename='connection.${connection.name}.${date}.json'")
    response.getWriter().print(exp)
  }

}
