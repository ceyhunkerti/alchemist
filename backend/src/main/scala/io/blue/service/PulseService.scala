package io.blue.service

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
import io.blue.model.odi.runtime.lpi._
import io.blue.repository.PulseRepository
import io.blue.model._
import io.blue.exception.SystemUserNotAllowedException
import io.blue.model.odi.runtime.session.finder._
import io.blue.actor.message._

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import org.apache.commons.lang3.exception.ExceptionUtils


@Service
@Transactional
class PulseService @Autowired()(val pulseRepository: PulseRepository){

  private val logger = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var userService: UserService = _

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  @(Autowired @setter)
  private var loadPlanService: LoadPlanService = _

  @(Autowired @setter)
  private var loadPlanRunService: LoadPlanRunService = _

  @(Autowired @setter)
  private var appInit: AppInit = _

  def findOne(id: Long) = pulseRepository.findOne(id)

  def findAll(connectionId: Long) = pulseRepository.findByConnectionId(connectionId)

  def findActive(connectionId: java.lang.Long) = {
    if(connectionId != null)
      pulseRepository.findByConnectionIdAndActive(connectionId, true)
    else
      pulseRepository.findByActive(true)
  }

  def create(pulse: Pulse, connectionId: Long) {
    pulse.connection = connectionService.findOne(connectionId)
    pulseRepository.save(pulse)
  }

  def update(id: Long, pulse: Pulse) = {
    var p = findOne(id)
    p.name= pulse.name
    p.active = pulse.active
    p.mail = pulse.mail
    p.content = pulse.content
    p.pulseType = pulse.pulseType
    pulseRepository.save(p)
  }

  def delete(id: Long) = {
    var pulse = findOne(id)
    pulseRepository.delete(pulse.id)
    pulse
  }

  def setActive(id: Long, active: Boolean) = {
    var pulse = findOne(id)
    pulse.active = active
    pulseRepository.save(pulse)
  }


  def pulse {
    val pulses = pulseRepository.findByActive(true)
    pulseLoadPlanErrors(pulses.filter(_.pulseType == PulseType.LOAD_PLAN_ERROR).toList)
  }

  //todo optimize connection
  def pulseLoadPlanErrors(pulses: List[Pulse]) {
    if(pulses == null || pulses.isEmpty) {
      return
    }
    val mapper = new ObjectMapper
    var criteria = new LoadPlanInstanceRunCriteria
    criteria.statuses = Array("ERROR")
    val ONE_MINUTE_IN_MILLIS = 60000
    val ONE_HOUR_IN_MILLIS = 60 * ONE_MINUTE_IN_MILLIS
    val ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS
    // ignore runs before that date.
    criteria.startTime = new Date(java.util.Calendar.getInstance.getTimeInMillis - (2 * ONE_DAY_IN_MILLIS))
    logger.info(s"${pulses.length} pulse found")

    pulses
      .groupBy(_.connection.id)
      .transform( (connectionId, pulses) => pulses.map{ pulse =>
          mapper
            .readValue(pulse.content, classOf[Array[Long]])
            .map((connectionId, pulse, _))
        }.flatten )
      .values
      .flatten
      .foreach{ case (connectionId, pulse, planId ) =>
        val plan = loadPlanService.findOne(connectionId, planId)
        criteria.loadPlanName = plan.name
        logger.info(s"Checking errors")
        loadPlanRunService
          .findByCriteria(connectionId, criteria)
          .foreach(pulseLoadPlanInstanceRunError(connectionId, pulse, _))
      }
  }

  def pulseLoadPlanInstanceRunError(connectionId: Long, pulse: Pulse, run: LoadPlanInstanceRun) {
    logger.info(s"Sending mail for ${pulse.name}")
    val connection = connectionService.findOne(connectionId)
    val mail: String = if(pulse.mail != null) pulse.mail else ""
    appInit.system.actorSelection("/user/mail") ! LoadPlanRunErrorMail(mail, connection.name, run)
  }



}