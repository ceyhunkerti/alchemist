package io.blue.service

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
import oracle.odi.domain.runtime.session.OdiSession
import oracle.odi.domain.runtime.session.finder.IOdiSessionFinder

import io.blue.model.odi.topology._

import oracle.odi.domain.topology.OdiPhysicalAgent
import oracle.odi.domain.topology.finder.{ IOdiPhysicalAgentFinder, IOdiLogicalAgentFinder }
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker


@Service
@Transactional
class AgentService extends OdiConnector{

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var loadPlanService: LoadPlanService = _

  private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  def findById(internalId: Number, odiInstance: OdiInstance) = {
    val agent = odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiPhysicalAgent])
      .asInstanceOf[IOdiPhysicalAgentFinder]
      .findById(internalId)
      .asInstanceOf[OdiPhysicalAgent]
    new PhysicalAgent(agent)
  }

  def findAll(connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    var result: List[PhysicalAgent] = null

    try {
      result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiPhysicalAgent])
        .asInstanceOf[IOdiPhysicalAgentFinder]
        .findAll//(true)
        .asInstanceOf[java.util.Vector[OdiPhysicalAgent]]
        .map(new PhysicalAgent(_)).toList
      odiInstance.close
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }

    result
    // result.map{agent =>
    //   try {
    //     test(connectionId, agent.internalId.asInstanceOf[Number])
    //     agent.status = AgentStatus.UP
    //   } catch {
    //     case e: Exception => agent.status = AgentStatus.DOWN
    //   }
    //   agent
    // }.toList
  }

  def test(connectionId: Long, id: Number) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance= getOdiInstance(connection)
    var agentVersion: String = null
    var agent: PhysicalAgent = null

    try {
      val finder = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiPhysicalAgent]).asInstanceOf[IOdiPhysicalAgentFinder]

      val pa = finder.findById(id).asInstanceOf[OdiPhysicalAgent]
      agent = new PhysicalAgent(pa)

      val ai = new RemoteRuntimeAgentInvoker(agent.url, connection.odiUsername, connection.odiPassword.toCharArray)
      agentVersion = ai.invokeIsAlive
      odiInstance.close
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
    (agent, agentVersion)
  }

}
