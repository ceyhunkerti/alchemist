package io.blue.controller

import java.util.Date
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation._

import io.blue.model.Pulse
import io.blue.service.PulseService
import io.blue.exception.UniqueConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

@RestController
@RequestMapping(Array("/api/v1/pulses"))
class PulseController  @Autowired()(private val service: PulseService) {

  @RequestMapping(method = Array(RequestMethod.GET) )
  def findAll(
    @RequestParam("connectionId") connectionId: Long
  ) = service.findAll(connectionId)

  @RequestMapping(value = Array("/active"), method = Array(RequestMethod.GET) )
  def findActive(
    @RequestParam(name="connectionId", required=false) connectionId: java.lang.Long
  ) = service.findActive(connectionId)

  @RequestMapping(method = Array(RequestMethod.POST) )
  def create(
    @RequestParam("connectionId") connectionId: Long,
    @RequestBody pulse: Pulse
  ) = service.create(pulse, connectionId)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.POST) )
  def update(
    @PathVariable("id") id: Long,
    @RequestBody pulse: Pulse
  ) = service.update(id, pulse)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.DELETE))
  def delete(@PathVariable("id") id: Long) = service.delete(id)

  @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.PUT))
  def setActive(
    @PathVariable("id") id: Long,
    @RequestParam("active") active: Boolean
  ) = service.setActive(id, active)

  @RequestMapping(value = Array("/pulse"), method = Array(RequestMethod.GET))
  def pulse = service.pulse

}