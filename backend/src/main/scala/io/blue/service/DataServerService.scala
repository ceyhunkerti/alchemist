package io.blue.service

import java.util.Optional
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

import io.blue.model.odi.topology._

import oracle.odi.domain.topology.OdiDataServer
import oracle.odi.domain.topology.finder.{ IOdiDataServerFinder }
import oracle.odi.runtime.agent.RuntimeAgent
import oracle.odi.domain.topology.finder.IOdiPhysicalAgentFinder
import oracle.odi.domain.topology.OdiPhysicalAgent
import oracle.odi.domain.topology.AbstractOdiDataServer.{JdbcSettings => OdiJdbcSettings}
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker
import oracle.odi.domain.util.ObfuscatedString

@Service
@Transactional
class DataServerService extends OdiConnector{

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var userService: UserService = _

  private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)


  def findAll(connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    var result: List[DataServer] = null

    try {
      result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiDataServer])
        .asInstanceOf[IOdiDataServerFinder]
        .findAll(true)
        .asInstanceOf[java.util.Vector[OdiDataServer]]
        .map(new DataServer(_)).toList
      odiInstance.close
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
    result.toList
  }

  def test(connectionId: Long, dataServerId: Number, agentId: Optional[Number]): Boolean = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance= getOdiInstance(connection)
    try {
      if (!agentId.isPresent) {
        val odiAgent = new RuntimeAgent(odiInstance, connection.odiUsername, connection.odiPassword.toCharArray)
        odiAgent.testDataServer(dataServerId)
      } else {
        val finder = odiInstance
          .getTransactionalEntityManager
          .getFinder(classOf[OdiPhysicalAgent]).asInstanceOf[IOdiPhysicalAgentFinder]
        val odiAgent = finder.findById(agentId.get).asInstanceOf[OdiPhysicalAgent]
        val invoker = new RemoteRuntimeAgentInvoker(odiAgent.getURLString, connection.odiUsername, connection.odiPassword.toCharArray)
        invoker.invokeTestDataServer(dataServerId)
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

}
