package io.blue.service

import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scala.io.Source
import scala.annotation.meta.setter
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.core.OdiInstance
import oracle.odi.domain.topology.finder.IOdiContextFinder
import oracle.odi.domain.topology.OdiContext

import io.blue.odi.OdiConnector
import io.blue.model.odi.topology.Context


@Service
@Transactional
class ContextService extends OdiConnector {

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)


  def findAll(connectionId: Long): List[Context] = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance= getOdiInstance(connection)
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

  def findAll(odiInstance: OdiInstance): List[Context] = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiContext])
      .asInstanceOf[IOdiContextFinder]
      .findAll
      .asInstanceOf[java.util.Vector[OdiContext]]
      .map(new Context(_))
      .toList
  }

}