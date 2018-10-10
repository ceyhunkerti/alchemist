package io.blue.service

import java.text.NumberFormat
import java.util.UUID
import java.io.File
import scala.io.Source
import scala.collection.JavaConversions._
import scala.annotation.meta.setter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.apache.commons.io.{ IOUtils, FileUtils }
import org.springframework.web.multipart.MultipartFile
import org.apache.commons.lang3.exception.ExceptionUtils

import oracle.odi.core.OdiInstance
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.support.{ImportServiceImpl, ExportServiceImpl}

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import io.blue.odi.OdiConnector
import io.blue.model._
import io.blue.actor.message._
import io.blue.AppInit


@Service
@Transactional
class AppService extends OdiConnector{

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var appInit: AppInit = _

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var scenarioService: ScenarioService = _

  @(Autowired @setter)
  private var loadPlanService: LoadPlanService = _

  def version = {
    case class Version(date: String, major: String, minor: String, versionCode: String, patch: String)
    val lines = Source.fromFile("version.properties").getLines.toList
    Version(lines(0),lines(1),lines(2),lines(3),lines(4))
  }

  def test = {
  }

  def importOdiObjectFromXml(connectionId: Long, mode: Int, objectType: String, f: MultipartFile) = {
    case class Result(status: String, connectionId: Long)
    var odiInstance: OdiInstance = null
    try {
      val filename = s"${objectType}_${UUID.randomUUID.toString}-${f.getOriginalFilename}".replaceAll("'","")
      val file = new File(s"/tmp/alchemist/${connectionId}", filename)
      FileUtils.touch(file)
      FileUtils.writeByteArrayToFile(file, f.getBytes)
      val connection = connectionService.findOne(connectionId)
      odiInstance = getOdiInstance(connection)
      val imp = new ImportServiceImpl(odiInstance)
      val (em, xm, xs) = getOdiTxm(odiInstance)
      imp.importObjectFromXml(mode, file.getAbsolutePath, false)
      commit(xm, xs)
      em.close
      odiInstance.close
      Result("success", connectionId)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        if (odiInstance != null) { odiInstance.close }
        Result("error", connectionId)
    }
  }

  def exportToXml(connectionId: Long, options: Map[String, String]) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val exp = new ExportServiceImpl(odiInstance)
      if (!options.contains("objectType")) {
        odiInstance.close
        throw new RuntimeException("Unknown object type in export")
      }
      if (!options.contains("internalId")) {
        odiInstance.close
        throw new RuntimeException("Missing object id")
      }

      if (!options.contains("name")) {
        odiInstance.close
        throw new RuntimeException("Name is mandotary")
      }

      val internalId = NumberFormat.getInstance().parse(options("internalId"))

      val exportable = (options("objectType") match {
        case "SCENARIO" => scenarioService.findById(odiInstance, internalId).odiScenario
        case "LOAD_PLAN" => loadPlanService.findById(odiInstance, internalId).odiLoadPlan
        case "SCEN_FOLDER" => scenarioService.findFolderById(odiInstance, internalId).odiScenarioFolder
        case _ => throw new RuntimeException("Object type is not supported")
      }).asInstanceOf[oracle.odi.domain.impexp.IExportable]

      val path = s"/tmp/alchemist/exp/${connectionId}/${UUID.randomUUID.toString}"

      val prefix = options("objectType") match {
        case "SCENARIO" => "SCEN"
        case "LOAD_PLAN" => "LP"
        case "SCEN_FOLDER" => "SFOL"
        case _ => throw new RuntimeException("Object type is not supported")
      }
      val (em, xm, xs) = getOdiTxm(odiInstance)
      exp.exportToXml(exportable, path, true, false, new EncodingOptions, null, true)
      commit(xm, xs)
      em.close
      odiInstance.close
      val name = s"""${prefix}_${options("name").replaceAll("\\s","_")}.xml"""
      val content = FileUtils.readFileToString(new File(s"${path}/${name}"))
      Map("content"->content, "name"->name)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }


}