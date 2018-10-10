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
import io.blue.model.security._

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.core.OdiInstance
import oracle.odi.domain.security.finder.{ IOdiUserFinder, IOdiRoleFinder }
import oracle.odi.domain.security.{ OdiUserCreationServiceImpl, OdiUser, OdiRole }


@Service
@Transactional
class SecurityService extends OdiConnector {

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  @(Autowired @setter)
  private var connectionService: ConnectionService = _

  def findUsers(odiInstance: OdiInstance): List[io.blue.model.security.User] = {
    odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiUser])
      .asInstanceOf[IOdiUserFinder]
      .findAll
      .asInstanceOf[java.util.Vector[OdiUser]]
      .map(new io.blue.model.security.User(_)).toList
  }

  def findUsers(connectionId: Long): List[io.blue.model.security.User] = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiUser])
        .asInstanceOf[IOdiUserFinder]
        .findAll
        .asInstanceOf[java.util.Vector[OdiUser]]
        .map(new io.blue.model.security.User(_)).toList
      odiInstance.close
      result
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def findOdiUser(id: Number, odiInstance: OdiInstance) = {
    val odiUser = odiInstance
      .getTransactionalEntityManager
      .getFinder(classOf[OdiUser])
      .asInstanceOf[IOdiUserFinder]
      .findById(id)
      .asInstanceOf[OdiUser]
    odiUser
  }

  def deleteUser(id: Number, connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val odiUser = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiUser])
        .asInstanceOf[IOdiUserFinder]
        .findById(id)
        .asInstanceOf[OdiUser]
      val user = new io.blue.model.security.User(odiUser)
      if(user.isSupervisor) {
        odiInstance.close
        throw new RuntimeException("Deleting SUPERVISOR users is not supported!")
      }
      val (em, xm, xs) = getOdiTxm(odiInstance)
      em.remove(em.merge(odiUser))
      commit(xm, xs)
      em.close
      odiInstance.close
      user
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def cloneUser(id: Number, connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val odiUser = findOdiUser(id, odiInstance)
      var user = new io.blue.model.security.User(odiUser)
      val names = findUsers(odiInstance).map(_.name)
      val name = if (names contains s"Copy of ${user.name}") {
        var i = 2
        while(names contains s"Copy (${i}) of ${user.name}") {
          i = i + 1
        }
        s"Copy (${i}) of ${user.name}"
      } else {
        s"Copy of ${user.name}"
      }
      val (em, xm, xs) = getOdiTxm(odiInstance)
      val odiUserCreationService = new OdiUserCreationServiceImpl(odiInstance)
      val newOdiUser = odiUserCreationService
        .createOdiUser(name, "welcome1".toCharArray, user.isSupervisor, null)
      commit(xm, xs)
      em.close
      odiInstance.close
      new io.blue.model.security.User(newOdiUser)
    } catch {
      case e: Exception =>
        logger.error(ExceptionUtils.getStackTrace(e))
        odiInstance.close
        throw e
    }
  }

  def renameUser(name: String, newName: String, connectionId: Long) = {
    // ! todo: implement
    // val connection = connectionService.findOne(connectionId)
    // val odiInstance = getOdiInstance(connection)
    // try {
    //   val odiRole = odiInstance
    //     .getTransactionalEntityManager
    //     .getFinder(classOf[OdiRole])
    //     .asInstanceOf[IOdiRoleFinder]
    //     .findByName(name)
    //     .asInstanceOf[OdiRole]
    //   val (em, xm, xs) = getOdiTxm(odiInstance)
    //   odiRole.setName(newName)
    //   em.persist(odiRole)
    //   commit(xm, xs)
    //   em.close
    //   odiInstance.close
    //   new io.blue.model.security.Role(odiRole)
    // } catch {
    //   case e: Exception =>
    //     logger.error(ExceptionUtils.getStackTrace(e))
    //     odiInstance.close
    //     throw e
    // }
  }

  def findRoles(connectionId: Long) = {
    val connection = connectionService.findOne(connectionId)
    val odiInstance = getOdiInstance(connection)
    try {
      val result = odiInstance
        .getTransactionalEntityManager
        .getFinder(classOf[OdiRole])
        .asInstanceOf[IOdiRoleFinder]
        .findAll
        .asInstanceOf[java.util.Vector[OdiRole]]
        .map(new io.blue.model.security.Role(_))
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
