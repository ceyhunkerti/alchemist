package io.blue.controller

import java.lang.Number
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

import io.blue.service.SessionService
import io.blue.model.odi.runtime.session.finder._

@RestController
@RequestMapping(Array("/api/v1/sessions"))
class SessionController @Autowired()(private val service: SessionService) {

  @RequestMapping(value = Array("/sync"), method = Array(RequestMethod.GET))
  def sync(@RequestParam("connectionId") connectionId: Long) =
    service.sync(connectionId)

  @RequestMapping(method = Array(RequestMethod.POST))
  def findByCriteria(
    @RequestBody(required=false) criteria: SessionCriteria,
    @RequestParam("connectionId") connectionId: Long
  ) = service.findByCriteria(connectionId, criteria)

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) = service.findAll(connectionId)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.GET))
  def findById(
    @PathVariable("id") id: Number,
    @RequestParam("connectionId") connectionId: Long
  ) = service.findById(id, connectionId)

  @RequestMapping(value = Array("/purge"), method = Array(RequestMethod.GET))
  def purge(@RequestParam("connectionId") connectionId: Long) = service.purge(connectionId)


}
