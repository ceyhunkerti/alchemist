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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.core.OdiInstance
import oracle.odi.core.repository.Repository
import oracle.odi.domain.runtime.session.{OdiSession, Status}
import oracle.odi.domain.runtime.session.finder.IOdiSessionFinder
import oracle.odi.domain.runtime.session.finder.{ IOdiSessionFinder, OdiSessionCriteria }
import oracle.odi.domain.runtime.purge.support.PurgeLogServiceImpl

import io.blue.model.odi.runtime.lpi._
import io.blue.model.odi.runtime.session._
import io.blue.model.odi.runtime.session.finder._
import io.blue.model.odi.repository.WorkRepository
import io.blue.model.{Connection, User}
import io.blue.exception._
import io.blue.odi.OdiConnector

@Service
@Transactional
class SessionService extends OdiConnector{

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var loadPlanService: LoadPlanService = _

  @(Autowired @setter)
  private var loadPlanRunService: LoadPlanRunService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  def sync(connectionId: Long) = {
    loadPlanService.sync(connectionId)
    loadPlanRunService.sync(connectionId)
  }

  def findByCriteria(connectionId: Long ,criteria: SessionCriteria) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)

    try {
      var c = new OdiSessionCriteria
      if (criteria != null) {
        if(criteria.statuses != null) {
          c.setStatuses(criteria.statuses.map(Status.valueOf(_)).toArray)
        }
        if(criteria.agentNames != null) {
          c.setAgentNames(criteria.agentNames.toArray, true)
        }
        c.setStartTime(criteria.startTime)
        c.setEndTime(criteria.endTime)
        c.setSubmitterName(criteria.submitterName)
        c.setScenarioName(criteria.scenarioName)
      }
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiSession])
        .asInstanceOf[IOdiSessionFinder]
        .findByCriteria(c, 20)
        .asInstanceOf[java.util.Vector[OdiSession]]
        .map(new Session(_))
      odiInstance.close
      result.toList
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findAll(connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiSession])
        .asInstanceOf[IOdiSessionFinder]
        .findAll
        .asInstanceOf[java.util.Vector[OdiSession]]
        .map(new Session(_)).toList
      odiInstance.close
      result
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findById(id: Number, connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiSession])
        .asInstanceOf[IOdiSessionFinder]
        .findBySessionId(id)
        .asInstanceOf[OdiSession]
      val session = (new Session(result)).withStepLogs
      odiInstance.close
      session
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def purge(connectionId: Long) = {
    import oracle.odi.domain.runtime.purge.support.PurgeLogServiceReport


    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    val s = new PurgeLogServiceImpl(odiInstance)
    val c = new OdiSessionCriteria
    c.setName("*")
    try {
      val callback = new PurgeLogServiceReport
      s.purgeStandaloneSessions(c, callback)
      true
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

}
