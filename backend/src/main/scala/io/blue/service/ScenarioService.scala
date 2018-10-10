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
class ScenarioService extends OdiConnector {

  @(Autowired @setter)
  private var userService: UserService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  def findById(odiInstance: OdiInstance, internalId: Number) = {
    val odiScenario = odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenario])
      .asInstanceOf[IOdiScenarioFinder]
      .findById(internalId)
      .asInstanceOf[OdiScenario]
    new Scenario(odiScenario)
  }

  def findAll(odiInstance: OdiInstance): List[Scenario] = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenario])
      .asInstanceOf[IOdiScenarioFinder]
      .findAll
      .asInstanceOf[java.util.Vector[OdiScenario]]
      .map(new Scenario(_)).toList
  }
  def findAll(connectionId: Long): List[Scenario] = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = findAll(odiInstance)
      odiInstance.close
      result
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findAllFolders(odiInstance: OdiInstance): List[ScenarioFolder] = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenarioFolder])
      .asInstanceOf[IOdiScenarioFolderFinder]
      .findAll
      .asInstanceOf[java.util.Vector[OdiScenarioFolder]]
      .map(new ScenarioFolder(_)).toList
  }
  def findAllFolders(connectionId: Long): List[ScenarioFolder] = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try{
      val result = findAllFolders(odiInstance)
      odiInstance.close
      result
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findFolderById(odiInstance: OdiInstance, internalId: Number) = {
    val result = odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenarioFolder])
      .asInstanceOf[IOdiScenarioFolderFinder]
      .findById(internalId)
      .asInstanceOf[OdiScenarioFolder]
    new ScenarioFolder(result)
  }

    def findOdiScenarioByUserProcedure(id: Number, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenario]).asInstanceOf[IOdiScenarioFinder]
      .findLatestBySourceUserProcedure(id, true)
      .asInstanceOf[OdiScenario]
  }

  def findOdiScenarioByPackage(id: Number, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenario]).asInstanceOf[IOdiScenarioFinder]
      .findLatestBySourcePackage(id, true)
      .asInstanceOf[OdiScenario]
  }

  def findOdiScenarioByMapping(id: Number, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiScenario]).asInstanceOf[IOdiScenarioFinder]
      .findLatestBySourceMapping(id, true)
      .asInstanceOf[OdiScenario]
  }



}
