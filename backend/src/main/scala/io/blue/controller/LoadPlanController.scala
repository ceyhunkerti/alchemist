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

import io.blue.service.LoadPlanService

@RestController
@RequestMapping(Array("/api/v1/load-plans"))
class LoadPlanController @Autowired()(private val service: LoadPlanService) {

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) =
    service.findAll(connectionId)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long, @PathVariable("id") id: java.lang.Number) =
    service.findOne(connectionId, id)

  @RequestMapping(value = Array("/sync"), method = Array(RequestMethod.GET))
  def sync(@RequestParam("connectionId") connectionId: Long) =
    service.sync(connectionId)

  @RequestMapping(value = Array("/collect-stats"), method = Array(RequestMethod.GET))
  def collectStats(@RequestParam("connectionId") connectionId: Long) =
    service.collectStats(connectionId)

}
