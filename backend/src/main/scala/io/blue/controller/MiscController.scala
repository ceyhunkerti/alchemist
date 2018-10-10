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

import io.blue.service.MiscService

@RestController
@RequestMapping(Array("/api/v1/misc"))
class MiscController @Autowired()(private val service: MiscService) {

  @RequestMapping(value = Array("/procedure"), method = Array(RequestMethod.POST))
  def createProcedures(
    @RequestParam("connectionId") connectionId: Long,
    @RequestParam("projectCode") projectCode: String,
    @RequestParam("folderName") folderName: String,
    @RequestParam("logicalSchema") logicalSchema: String,
    @RequestBody procs: Map[String, String]
  ) = service.createProcedures(connectionId, projectCode, folderName, logicalSchema, procs)

  @RequestMapping(value = Array("/scenarios"), method = Array(RequestMethod.POST))
  def findScenarios(
    @RequestParam("connectionId") connectionId: Long,
    @RequestParam("projectCode") projectCode: String,
    @RequestParam("folderName") folderName: String,
    @RequestBody types: List[String]
  ) = service.findScenarios(connectionId, projectCode, folderName, types)


}
