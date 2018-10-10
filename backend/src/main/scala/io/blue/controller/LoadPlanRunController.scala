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

import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.{ OdiLoadPlanInstanceRunId }

import io.blue.service.LoadPlanRunService

@RestController
@RequestMapping(Array("/api/v1/load-plan-runs"))
class LoadPlanRunController @Autowired()(private val service: LoadPlanRunService) {

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) =
    service.findAll(connectionId)

  @RequestMapping(value = Array("/last"), method = Array(RequestMethod.GET))
  def findLast(@RequestParam("connectionId") connectionId: Long) =
    service.findLast(connectionId)

  @RequestMapping(value = Array("/load-plan/{id}"), method = Array(RequestMethod.GET))
  def findAllByLoadPlan(@PathVariable("id") id: Long, @RequestParam("connectionId") connectionId: Long) =
    service.findAllByLoadPlan(connectionId, id)


  @RequestMapping(value = Array("/sync"), method = Array(RequestMethod.GET))
  def sync(@RequestParam("connectionId") connectionId: Long) =
    service.sync(connectionId)

  @RequestMapping(value = Array("/with-logs"), method = Array(RequestMethod.POST))
  def findOneWithLogs(
    @RequestParam("connectionId") connectionId: Long,
    @RequestBody(required=true) id: OdiLoadPlanInstanceRunId
  ) = service.findOneWithLogs(connectionId, id)

  @RequestMapping(value = Array("/clear-step-log/{globalId}"), method = Array(RequestMethod.POST))
  def clearLpiRunStepLog(
    @RequestParam("connectionId") connectionId: Long,
    @PathVariable("globalId") globalId: String,
    @RequestBody(required=true) id: OdiLoadPlanInstanceRunId
  ) = service.clearLpiRunStepLog(connectionId, id, globalId)

}
