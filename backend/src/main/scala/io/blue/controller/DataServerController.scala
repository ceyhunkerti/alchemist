package io.blue.controller

import java.util.Optional
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

import io.blue.service.DataServerService

@RestController
@RequestMapping(Array("/api/v1/data-servers"))
class DataServerController @Autowired()(private val service: DataServerService) {

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) =
    service.findAll(connectionId)

  @RequestMapping(value = Array("/{dataServerId}/test"), method = Array(RequestMethod.GET))
  def test(
    @PathVariable("dataServerId") dataServerId: Number,
    @RequestParam("connectionId") connectionId: Long,
    @RequestParam("agentId") agentId: Optional[Number]
  ) = service.test(connectionId, dataServerId, agentId)


}
