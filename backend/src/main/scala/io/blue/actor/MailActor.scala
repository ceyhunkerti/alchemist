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
import io.blue.service.MailService
import io.blue.model.odi.runtime.lpi.LoadPlanInstanceRun

@Component(value="mail")
@Scope("prototype")
class MailActor extends Actor {

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @Autowired
  private var springExtension: SpringExtension = _

  @(Autowired @setter)
  private var mailService: MailService = _

  def receive = {
    case SendUserPasswordMail(user: User, password: String) =>
      onSendUserPasswordMail(user,password)
    case SendNewPasswordMail(user: User, password: String) =>
      onSendNewPasswordMail(user,password)
    case LoadPlanRunErrorMail(mail: String, connection: String, run: LoadPlanInstanceRun) =>
      onLoadPlanRunErrorMail(mail, connection, run)
    case m => logger.error(s"Unknown message @MailActor $m")
  }

  def onSendUserPasswordMail(user: User, password: String) = {
    try {
      mailService.sendUserPasswordMail(user, password)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
    }
  }

  def onSendNewPasswordMail(user: User, password: String) = {
    try {
      mailService.sendNewPasswordMail(user, password)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
    }
  }

  def onLoadPlanRunErrorMail(mail: String, connection: String, run: LoadPlanInstanceRun) = {
    var m = new Mail
    val plan = run.loadPlanInstance.loadPlan.name
    m.subject = s"@${connection} - ODI Load Plan Error - #${plan}"
    m.to = mail
    m.body = s"${run.errorMessage}"
    try {
      mailService.sendMail(m)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
    }
  }

}