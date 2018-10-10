package io.blue.service

import java.lang.Number
import java.util.Date
import scala.reflect.ClassTag
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.annotation.meta.setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.fasterxml.jackson.databind.ObjectMapper

import io.blue.AppInit
import io.blue.exception._
import io.blue.model._
import io.blue.odi.OdiConnector
import io.blue.exception.SystemUserNotAllowedException
import io.blue.model.odi.runtime.scenario.Scenario

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.core.OdiInstance
import oracle.odi.domain.project._
import oracle.odi.domain.xrefs.expression.Expression
import oracle.odi.domain.topology.OdiLogicalSchema
import oracle.odi.domain.topology.finder._
import oracle.odi.domain.topology._
import oracle.odi.domain.project.finder._
import oracle.odi.domain.project.OdiFolder
import oracle.odi.generation.support._
import oracle.odi.domain.runtime.scenario.OdiScenario

import org.apache.commons.lang3.exception.ExceptionUtils


@Service
@Transactional
class MiscService extends OdiConnector{

  private val logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var scenarioService: ScenarioService = _


  def findLogicalSchema(name: String, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiLogicalSchema])
      .asInstanceOf[IOdiLogicalSchemaFinder]
      .findByName(name)
      .asInstanceOf[OdiLogicalSchema]
  }

  def findTechnology(code: String, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiTechnology])
      .asInstanceOf[IOdiTechnologyFinder]
      .findByCode(code)
      .asInstanceOf[OdiTechnology]
  }

  def findFolders(name: String, projectCode: String, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiFolder])
      .asInstanceOf[IOdiFolderFinder]
      .findByName(name, projectCode)
      .asInstanceOf[java.util.Vector[OdiFolder]]
      .toList
  }

  def findProcedure(name: String, projectCode: String, folderName: String, odiInstance: OdiInstance) = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiUserProcedure])
      .asInstanceOf[IOdiUserProcedureFinder]
      .findByName(name, projectCode, folderName)
      .asInstanceOf[java.util.Vector[OdiUserProcedure]]
      .toList
  }

  def createProcedures(connectionId: Long, projectCode: String, folderName: String, logicalSchema: String, procs: Map[String, String]) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    var folder = findFolders(folderName, projectCode, odiInstance)(0)
    val (em, xm, xs) = getOdiTxm(odiInstance)
    val scenGen = new OdiScenarioGeneratorImpl(odiInstance)
    try {
      procs foreach { case (name, content) =>
        var proc = new OdiUserProcedure(folder, name)
        var line = proc.addLine("Run")
        var cmd = new OdiProcedureLineCmd
        cmd.setExpression(new Expression(s"begin ${content}; end;", null, Expression.SqlGroupType.NONE))
        cmd.setLogicalSchema(findLogicalSchema(logicalSchema, odiInstance))
        line.setOnTargetCommand(cmd)
        proc.setDefaultTargetTechnology(findTechnology("ORACLE", odiInstance))
        em.persist(proc)

        val scen = scenGen.generateScenario(proc.asInstanceOf[IOdiScenarioSource], s"SCEN_PROC_${name}", "001")
        em.persist(scen)
      }
      commit(xm, xs)
      em.close
      odiInstance.close
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
    true
  }

  def findScenarios(connectionId: Long, projectCode: String, folderName: String, types: List[String]) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val folders = findFolders(folderName, projectCode, odiInstance)
      if (folders == null || folders.isEmpty || folders.size != 1) {
        throw new RuntimeException("Invalid project/folder!")
      }
      val folder = folders(0)
      var result = Map[String, List[Scenario]]()

      if (types contains "procedure") {
        result += ("procedure" -> folder.getUserProcedures.map{ x =>
          val scenario = scenarioService.findOdiScenarioByUserProcedure(x.getInternalId.asInstanceOf[Number], odiInstance)
          if (scenario != null) { new Scenario(scenario) } else { null }
        }.filter(_ != null).toList)
      }
      if (types contains "package") {
        result += ("package" -> folder.getPackages.map{ x =>
          val scenario = scenarioService.findOdiScenarioByPackage(x.getInternalId.asInstanceOf[Number], odiInstance)
          if (scenario != null) { new Scenario(scenario) } else { null }
        }.filter(_ != null).toList)
      }
      if (types contains "mapping") {
        result += ("mapping" -> folder.getPackages.map{ x =>
          val scenario = scenarioService.findOdiScenarioByMapping(x.getInternalId.asInstanceOf[Number], odiInstance)
          if (scenario != null) { new Scenario(scenario) } else { null }
        }.filter(_ != null).toList)
      }
      odiInstance.close
      result
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }

  }

}