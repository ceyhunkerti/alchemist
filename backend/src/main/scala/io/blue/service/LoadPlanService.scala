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


import io.blue.model.odi.runtime.loadplan._
import io.blue.model.odi.repository.WorkRepository
import io.blue.model.{Connection, User}
import io.blue.exception._
import io.blue.odi.OdiConnector
import io.blue.repository.LoadPlanRepository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.domain.runtime.loadplan.finder.IOdiLoadPlanFinder
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import oracle.odi.core.OdiInstance
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.{ Status }


@Service
@Transactional
class LoadPlanService @Autowired()(val repository: LoadPlanRepository) extends OdiConnector {

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var loadPlanRunService: LoadPlanRunService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  def findOne(id: Long): io.blue.model.LoadPlan = repository.findOne(id)

  def findById(odiInstance: OdiInstance, internalId: Number) = {
    val odiLoadPlan = odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiLoadPlan])
      .asInstanceOf[IOdiLoadPlanFinder]
      .findById(internalId)
      .asInstanceOf[OdiLoadPlan]
    new LoadPlan(odiLoadPlan)
  }

  def findOne(connectionId: Long, id: Number): LoadPlan = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val odiLoadPlan = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiLoadPlan])
        .asInstanceOf[IOdiLoadPlanFinder]
        .findById(id)
        .asInstanceOf[OdiLoadPlan]
      (new LoadPlan(odiLoadPlan)).withSteps
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }

  }


  def findAll(odiInstance: OdiInstance): List[LoadPlan] = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiLoadPlan])
      .asInstanceOf[IOdiLoadPlanFinder]
      .findAll
      .asInstanceOf[java.util.Vector[OdiLoadPlan]]
      .map(new LoadPlan(_)).toList
  }
  def findAll(connectionId: Long): List[LoadPlan] = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = findAll(odiInstance)
      odiInstance.close
      val runs = loadPlanRunService.findAll(connectionId)

      result.map{ r =>
        val list = runs
          .filter(
            _.loadPlanInstance.loadPlan.internalId.asInstanceOf[Number] ==
            r.internalId.asInstanceOf[Number]
          )

        r.errorCount = list.filter(_.status == Status.valueOf("ERROR")).length
        r.avgDuration = if (list.isEmpty) { 0 } else { list(0).avgDuration }
        r
      }
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findAllSync(connectionId: Long) =
    repository.findByConnectionId(connectionId)

  def sync(connectionId: Long) = {
    val plans = findAll(connectionId).map(new io.blue.model.LoadPlan(_, connectionId))
    val ids = repository.findByConnectionId(connectionId).map(_.internalId)
    repository.save(plans.filter{plan => ids.contains(plan.internalId) == false } )
  }

  def collectStats(connectionId: Long) = {
    // sync(connectionId)
    // loadPlanRunService.sync(connectionId)
    // var runs = loadPlanRunService.findDoneSync(connectionId)
    // var p = runs.groupBy(_.loadPlanInstance.loadPlan.id)
    // p
    // var plans = List[io.blue.model.LoadPlan]()
    // p.keys.map{ planId =>
    //   val (avg: Float, min: Float, max: Float) = p get planId match {
    //     case Some(runs) =>
    //       (
    //         runs.map(_.duration).sum / runs.length,
    //         runs.map(_.duration).reduceLeft(_ min _),
    //         runs.map(_.duration).reduceLeft(_ max _)
    //       )
    //     case _ =>
    //   }
    //   var plan = findOne(planId)
    //   plan.avgDuration = avg
    //   plan.minDuration = min
    //   plan.maxDuration = max
    //   plans ::= repository.save(plan)
    // }
    // plans
  }



}
