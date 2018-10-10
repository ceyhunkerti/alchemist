package io.blue.service

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

import io.blue.model.odi.runtime.scenario._
import io.blue.model.odi.repository.WorkRepository
import io.blue.model.{Connection, User}
import io.blue.exception._
import io.blue.odi.OdiConnector

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.domain.runtime.scenario.finder.{ IOdiScenarioFinder, IOdiScenarioFolderFinder}
import oracle.odi.domain.runtime.scenario.{ OdiScenario, OdiScenarioFolder }
import oracle.odi.core.OdiInstance


@Service
@Transactional
class ExecutableService extends OdiConnector {

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var scenarioService: ScenarioService = _

  @(Autowired @setter)
  private var loadPlanService: LoadPlanService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var agentInvoker: AgentInvokerService = _


  def findAllFolders(odiInstance: OdiInstance) = scenarioService.findAllFolders(odiInstance)
  def findAllFolders(connectionId: Long) = scenarioService.findAllFolders(connectionId)

  def findAll(connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val folders = findAllFolders(odiInstance)
      val scenarios = scenarioService.findAll(odiInstance).filter(!_.hasFolder)
      val loadPlans = loadPlanService.findAll(odiInstance).filter(!_.hasFolder)
      odiInstance.close
      Map("folders" -> folders, "scenarios" -> scenarios, "loadPlans" -> loadPlans)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def run(connectionId: Long, options: Map[String, String]) {
    options get "type" match {
      case Some("SCENARIO") => invokeStartScenario(connectionId, options)
      case Some("LOAD_PLAN") =>invokeStartLoadPlan(connectionId, options)
      case _ =>
    }
  }

  def invokeStartScenario(connectionId: Long, options: Map[String, String]) =
    agentInvoker.invokeStartScenario(connectionId, options)

  def invokeStartLoadPlan(connectionId: Long, options: Map[String, String]) =
    agentInvoker.invokeStartLoadPlan(connectionId, options)

}
