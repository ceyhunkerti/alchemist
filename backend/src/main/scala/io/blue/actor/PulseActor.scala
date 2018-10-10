package io.blue.actor

import scala.annotation.meta.setter
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Actor
import akka.routing.Router
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._
import akka.actor.{Props, ActorRef}

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import io.blue.ext.SpringExtension
import io.blue.actor.message._
import io.blue.model._
import io.blue.service.PulseService

@Component(value="pulse")
@Scope("prototype")
class PulseActor extends Actor {

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @Autowired
  private var springExtension: SpringExtension = _

  @(Autowired @setter)
  private var pulseService: PulseService = _

  override def preStart: Unit = {
    hearthBeat
  }

  override def postStop: Unit = {
  }

  def receive = {
    case Tick => tick
    case _ => logger.warn("Opps ?")
  }

  def tick {
    try {
      pulseService.pulse
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
    }
  }

  def hearthBeat = {
    val ONE_MINUTE_IN_MILLIS = 6000
    val cancellable =
      context.system.scheduler.schedule(0 milliseconds, (2 * ONE_MINUTE_IN_MILLIS) milliseconds,self,Tick)
  }

}