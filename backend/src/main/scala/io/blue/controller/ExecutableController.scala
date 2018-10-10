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

import io.blue.service.ExecutableService

@RestController
@RequestMapping(Array("/api/v1/executables"))
class ExecutableController @Autowired()(private val service: ExecutableService) {

  @RequestMapping(value = Array("/folders"), method = Array(RequestMethod.GET))
  def findAllFolders(@RequestParam("connectionId") connectionId: Long) =
    service.findAllFolders(connectionId)

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) = service.findAll(connectionId)

  @RequestMapping(value = Array("/run"), method = Array(RequestMethod.POST))
  def invokeStartScenario(
    @RequestParam("connectionId") connectionId: Long,
    @RequestBody options: Map[String, String]
  ) = service.run(connectionId, options)

}
