package io.blue.controller

import java.lang.Number
import org.springframework.security.access.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation._
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.annotation._

import io.blue.service.ContextService

@RestController
@RequestMapping(Array("/api/v1/contexts"))
class ContextController @Autowired()(private val service: ContextService) {

  @RequestMapping(method = Array(RequestMethod.GET))
  def findAll(@RequestParam("connectionId") connectionId: Long) =
    service.findAll(connectionId)

  // @RequestMapping(method = Array(RequestMethod.POST))
  // def create(@PathVariable("connectionId") connectionId: Long, @RequestBody context: Context) =
  //   contextService.create(connectionId, context)

  // @RequestMapping(value = Array("/{id}"), method = Array(RequestMethod.DELETE))
  // def delete(@PathVariable("connectionId") connectionId: Long, @PathVariable("id") id: Number) =
  //   contextService.delete(connectionId, id)

}