package io.blue.controller

import java.util.Date
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse
import java.lang.Number

import javax.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation._
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.annotation._

import io.blue.service.SecurityService

@RestController
@RequestMapping(Array("/api/v1/security"))
class SecurityController @Autowired()(private val service: SecurityService) {

  @RequestMapping(value= Array("/users"), method = Array(RequestMethod.GET))
  def findUsers(@RequestParam("connectionId") connectionId: Long) =
    service.findUsers(connectionId)

  @RequestMapping(value= Array("/roles"), method = Array(RequestMethod.GET))
  def findRoles(@RequestParam("connectionId") connectionId: Long) =
    service.findRoles(connectionId)

  @RequestMapping(value= Array("/users/{id}"), method = Array(RequestMethod.DELETE))
  def deleteUser(@PathVariable("id") id: Number, @RequestParam("connectionId") connectionId: Long) =
    service.deleteUser(id, connectionId)

  @RequestMapping(value= Array("/users/clone/{id}"), method = Array(RequestMethod.GET))
  def cloneUser(@PathVariable("id") id: Number, @RequestParam("connectionId") connectionId: Long) =
    service.cloneUser(id, connectionId)

  @RequestMapping(value= Array("/users/rename"), method = Array(RequestMethod.PUT))
  def renameUser(
    @RequestBody params: Map[String, String],
    @RequestParam("connectionId") connectionId: Long
  ) = service.renameUser(params get "name" get, params get "newName" get, connectionId)


}
