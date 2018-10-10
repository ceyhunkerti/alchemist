package io.blue.service

import java.text.NumberFormat
import java.lang.Number
import scala.collection.JavaConversions._
import scala.annotation.meta.setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.exception.ExceptionUtils

import io.blue.model.odi.repository.WorkRepository
import io.blue.model.{Connection, User}
import io.blue.exception._
import io.blue.odi.OdiConnector

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.core.OdiInstance
import oracle.odi.core.repository.Repository
import oracle.odi.runtime.agent.RuntimeAgent

import io.blue.model.odi.topology._

import oracle.odi.domain.topology.OdiPhysicalAgent
import oracle.odi.domain.topology.finder.{ IOdiPhysicalAgentFinder, IOdiLogicalAgentFinder }
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker


@Service
@Transactional
class AgentInvokerService extends OdiConnector {

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var agentService: AgentService = _

  private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  def findAgentById(odiInstance: OdiInstance, connection: io.blue.model.Connection, internalId: Number) = {
    if (internalId == -1) {
      new RuntimeAgent(odiInstance, connection.odiUsername, connection.odiPassword.toCharArray)
    } else {
      agentService.findById(internalId, odiInstance).odiPhysicalAgent
    }
  }

  def invokeStartScenario(connectionId: Long, options: Map[String, String]) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    val name = options("name")
    val version = options("version")
    val contextCode = options("contextCode")
    val agentId = NumberFormat.getInstance.parse(options("agentId"))

    try {
      if (agentId == -1) {
        val invoker = new RuntimeAgent(odiInstance, connection.odiUsername, connection.odiPassword.toCharArray)
        invoker.startScenario(
          name, version, null, null, contextCode, 5, null, true
        )
      } else {
        val agent = agentService.findById(agentId, odiInstance).odiPhysicalAgent
        val invoker = new RemoteRuntimeAgentInvoker(
          agent.getURLString, connection.odiUsername, connection.odiPassword.toCharArray
        )
        invoker.invokeStartScenario(
          name, version, null, null, contextCode, 5, null, true, odiInstance.getWorkRepository.getName
        )
      }
      odiInstance.close
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
    true
  }

  def invokeStartLoadPlan(connectionId: Long, options: Map[String, String]) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    val name = options("name")
    val contextCode = options("contextCode")
    val agentId = NumberFormat.getInstance.parse(options("agentId"))
    try {
      val agent = agentService.findById(agentId, odiInstance).odiPhysicalAgent
      val invoker = new RemoteRuntimeAgentInvoker(
        agent.getURLString, connection.odiUsername, connection.odiPassword.toCharArray
      )
      invoker.invokeStartLoadPlan(
        name, contextCode, null, null, odiInstance.getWorkRepository.getName, 5
      )
      odiInstance.close
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
    true
  }

}
