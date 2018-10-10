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

import io.blue.service.AgentService

@RestController
@RequestMapping(Array("/api/v1/agents"))
class AgentController @Autowired()(private val service: AgentService) {

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) =
    service.findAll(connectionId)

  @RequestMapping(value = Array("/{id}/test"), method = Array(RequestMethod.GET))
  def test(@RequestParam("connectionId") connectionId: Long, @PathVariable("id") id: java.lang.Number) =
    service.test(connectionId, id)


}
