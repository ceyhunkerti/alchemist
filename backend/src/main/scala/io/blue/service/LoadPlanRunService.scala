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

import io.blue.model.odi.runtime.lpi._
import io.blue.model.odi.runtime.loadplan._
import io.blue.model.odi.repository.WorkRepository
import io.blue.model.{Connection, User}
import io.blue.exception._
import io.blue.odi.OdiConnector
// import io.blue.repository.LoadPlanInstanceRunRepository
import io.blue.model.odi.runtime.session.finder.LoadPlanInstanceRunCriteria

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.domain.runtime.lpi.finder.{
  IOdiLoadPlanInstanceRunFinder,
  OdiLoadPlanInstanceRunCriteria
}
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun
import oracle.odi.core.OdiInstance
import oracle.odi.core.repository.Repository
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.{ Status, OdiLoadPlanInstanceRunId }
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceStepLog



@Service
@Transactional
class LoadPlanRunService extends OdiConnector {

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var loadPlanService: LoadPlanService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  // def findOne(id: Long) = repository.findOne(id)

  // def findByInternalIdAndLoadPlan(internalId: String, loadPlan: io.blue.model.LoadPlan) =
  //   repository.findByInternalIdAndLoadPlanInstanceLoadPlan(internalId, loadPlan)

  def clearLpiRunStepLog(connectionId: Long, id: OdiLoadPlanInstanceRunId, globalId: String) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val run = findOne(id, odiInstance, true)
      def findLog(node: LoadPlanInstanceStep): Option[OdiLoadPlanInstanceStepLog] =  {
        val log = node.odiLoadPlanInstanceStep.getLog(run.odiLoadPlanInstanceRun)
        if (log.getGlobalId == globalId) {
          return Some(log)
        }
        node match {
          case container: LoadPlanInstanceStepContainer =>
            if (!container.childrenSteps.isEmpty) {
              container.childrenSteps.foreach { step =>
                return findLog(step)
              }
            }
          case _ =>
        }
        return None
      }
      findLog(run.loadPlanInstance.rootStep) match {
        case Some(log: OdiLoadPlanInstanceStepLog) =>
          val (em, xm, xs) = getOdiTxm(odiInstance)
          em.remove(em.merge(log.asInstanceOf[oracle.odi.domain.IOdiEntity]))
          commit(xm, xs)
          em.close
          logger.debug("Removed log!")
        case None =>
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

  def findOneWithLogs(connectionId: Long, id: OdiLoadPlanInstanceRunId) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val run = findOne(id, odiInstance, true)
      def findLogs(node: LoadPlanInstanceStep) {
        node.setLog(run)
        node match {
          case container: LoadPlanInstanceStepContainer =>
            if (!container.childrenSteps.isEmpty) {
              container.childrenSteps.map { step =>
                findLogs(step)
              }
            }
          case _ =>
        }
      }
      var logs = findLogs(run.loadPlanInstance.rootStep)
      odiInstance.close
      run
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  // todo find by criteria
  def findAllByLoadPlan(connectionId: Long, loadPlanId: Long) = {
    findAll(connectionId)
      .filter(_.loadPlanInstance.loadPlan.internalId.asInstanceOf[Number].longValue == loadPlanId)
      // .filter(_.status == Status.valueOf("DONE"))
      .toList
  }


  def findOne(id: OdiLoadPlanInstanceRunId, odiInstance: OdiInstance, withSteps: Boolean = false): LoadPlanInstanceRun = {
    val result = odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiLoadPlanInstanceRun])
      .asInstanceOf[IOdiLoadPlanInstanceRunFinder]
      .findById(id)
      .asInstanceOf[OdiLoadPlanInstanceRun]
    new LoadPlanInstanceRun(result, withSteps)
  }

  def findAll(connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiLoadPlanInstanceRun])
        .asInstanceOf[IOdiLoadPlanInstanceRunFinder]
        .findAll
        .asInstanceOf[java.util.Vector[OdiLoadPlanInstanceRun]]
        .filter(_.getLoadPlanInstance.getLoadPlan != null)
        .map(new LoadPlanInstanceRun(_)).toList
      odiInstance.close
      val avgDurations = findAverageDurations(result)
      result.map{r =>
        val d = avgDurations.filter(_._1 == r.loadPlanInstance.loadPlan.internalId.asInstanceOf[Number])
        r.avgDuration = if (d.isEmpty) { 0 } else { d(0)._2 }
        r
      }
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findLast(connectionId: Long) = {
    findAll(connectionId)
      .groupBy(_.loadPlanInstance.loadPlan.internalId)
      .mapValues{x: List[LoadPlanInstanceRun] =>
        var max = x.maxBy{r: LoadPlanInstanceRun => r.startTime}
        val min = x.filter(_.internalId == max.internalId).minBy{r: LoadPlanInstanceRun => r.startTime}
        max.startTime = min.startTime
        max
      }.values
  }
  private def avg(list: List[Long], len: Long) = list.sum / len
  def findAverageDurations(runs: List[LoadPlanInstanceRun]): List[(Number, Long)] = {
    runs
      // .filter(_.status == Status.valueOf("DONE"))
      .groupBy(_.loadPlanInstance.loadPlan.internalId).map{kv =>
      (
        kv._1.asInstanceOf[Number],
        avg(kv._2.map(_.duration), kv._2.map(_.loadPlanInstance.internalId.asInstanceOf[Number].longValue).distinct.length)
      )
    }.toList
  }

  def sync(connectionId: Long) = {
    // val plans = loadPlanService.findAllSync(connectionId)
    // val runs = findAll(connectionId).map{ lpr =>
    //   val lps = plans.filter(_.internalId.asInstanceOf[java.lang.Number].longValue ==
    //     lpr.loadPlanInstance.loadPlan.internalId.asInstanceOf[java.lang.Number].longValue)
    //   if(lps.isEmpty) { null } else {
    //     val res = new io.blue.model.LoadPlanInstanceRun(lpr, connectionId)
    //     res.loadPlanInstance.loadPlan = lps(0)
    //     res
    //   }
    // }.filter(_ != null)

    // val planIds = plans.map(_.id).toList
    // val ids = repository.findByLoadPlanInstanceLoadPlanIdIn(asJavaCollection(planIds)).map(_.internalId)
    // runs.filter{run => ids.contains(run.internalId) == false }
    // runs.foreach { run =>
    //   var r: io.blue.model.LoadPlanInstanceRun = findByInternalIdAndLoadPlan(run.internalId, run.loadPlanInstance.loadPlan)
    //   if(r != null) {
    //     r.startTime = run.startTime
    //     r.endTime = run.endTime
    //     r.status = run.status
    //   } else {
    //     r = run
    //   }
    //   repository.save(r)
    // }
 }

  // def findAllSync(connectionId: Long) = {
  //   val plans = loadPlanService.findAllSync(connectionId)
  //   repository.findByLoadPlanInstanceLoadPlanIn(asJavaCollection(plans))
  // }

  // def findDoneSync(connectionId: Long) =
  //   findAllSync(connectionId).filter(_.status.equals(OdiLoadPlanInstanceRun.Status.DONE))

  def findByCriteria(connectionId: Long, criteria: LoadPlanInstanceRunCriteria) = {
    var odiCriteria = new OdiLoadPlanInstanceRunCriteria
    odiCriteria.setStartTime(criteria.startTime)
    odiCriteria.setStatuses(criteria.statuses.map(
      oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.Status.valueOf(_)
    ).toArray)
    odiCriteria.setLoadPlanName(criteria.loadPlanName)

    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiLoadPlanInstanceRun])
        .asInstanceOf[IOdiLoadPlanInstanceRunFinder]
        .findByCriteria(odiCriteria, 50)
        .asInstanceOf[java.util.Vector[OdiLoadPlanInstanceRun]]
        .map(new LoadPlanInstanceRun(_)).toList
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
