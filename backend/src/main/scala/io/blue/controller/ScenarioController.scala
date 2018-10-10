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

import io.blue.service.ScenarioService

@RestController
@RequestMapping(Array("/api/v1/scenarios"))
class ScenarioController @Autowired()(private val service: ScenarioService) {

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) =
    service.findAll(connectionId)

  @RequestMapping(value = Array("/folders"), method = Array(RequestMethod.GET))
  def findAllFolders(@RequestParam("connectionId") connectionId: Long) =
    service.findAllFolders(connectionId)

}
