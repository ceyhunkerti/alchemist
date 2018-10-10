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

import io.blue.repository.ConnectionRepository
import io.blue.model.odi.repository.WorkRepository
import io.blue.model.{Connection, User}
import io.blue.exception._
import io.blue.odi.OdiConnector

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import oracle.odi.core.OdiInstance
import oracle.odi.core.repository.Repository


@Service
@Transactional
class ConnectionService @Autowired()(val connectionRepository: ConnectionRepository) extends OdiConnector{

  @(Autowired @setter)
  private var userService: UserService = _

  private val logger: Logger  = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)

  def findAll = connectionRepository.findAll

  def findMyConnections =
    connectionRepository.findAllByUser(userService.findMe)

  def findOne(id: Long) = connectionRepository.findOne(id)

  def findOneByUser(id: Long, user: User = null) = {
    val u = if(user != null) user else userService.findMe
    val connection = connectionRepository.findByIdAndUser(id, u)
    if(connection == null) {
      throw new ConnectionNotExistException
    }
    connection
  }


  def test (connection: Connection): Boolean = {
    val odiInstance = getOdiInstance(connection)
    odiInstance.close
    true
  }
  def test(id: Long): Boolean = test(findOne(id))


  def findWorkRepos(connection: Connection) = {
    val ds = getDataSource(connection)
    val masterRepo = Repository.getMasterRepository(ds)
    val odiInstance= getOdiInstance(connection)

    val workRepos = odiInstance.getAllWorkRepositories(masterRepo).map{wr=>
      var w = new WorkRepository(wr.getShortId)
      w.name = wr.getName
      w
    }
    odiInstance.close
    workRepos
  }

  def create(connection: Connection): Connection = {
    connection.user = userService.findMe

    if (connection.isDefault) {
      resetDefaultConnection(connection.user)
    }

    if (connectionRepository.countByUser(connection.user) == 0){
      connection.isDefault = true
    }
    connectionRepository.save(connection)
  }

  def update(id: Long, connection: Connection) = {
    val me = userService.findMe

    var con = findOneByUser(id, me)
    con.name = if(connection.name != null) connection.name else con.name
    con.url = if(connection.url != null) connection.url else con.url
    con.masterUsername = if(connection.masterUsername != null) connection.masterUsername else con.masterUsername
    con.masterPassword = if(connection.masterPassword != null) connection.masterPassword else con.masterPassword
    con.workRepo = if(connection.workRepo != null) connection.workRepo else con.workRepo
    con.odiUsername = if(connection.odiUsername != null) connection.odiUsername else con.odiUsername
    con.odiPassword = if(connection.odiPassword != null) connection.odiPassword else con.odiPassword

    if (connection.isDefault) {
      resetDefaultConnection(me)
    }
    con.isDefault = connection.isDefault
    connectionRepository.save(con)
  }

  private def resetDefaultConnection(user: User) = {
    val connections = connectionRepository.findAllByUser(user)
    connections.foreach{ connection =>
      connection.isDefault = false
      connectionRepository.save(connection)
    }
  }

  def delete(id: Long): Connection = {
    val connection = findOneByUser(id);
    connectionRepository.delete(id)
    connection
  }

  def findByRepoConnection(url: String, masterUsername: String, masterPassword: String, workRepo: String) = {
    connectionRepository.findByUrlIgnoreCaseAndMasterUsernameIgnoreCaseAndMasterPasswordIgnoreCaseAndWorkRepoIgnoreCase(
      url, masterUsername, masterPassword, workRepo
    )
  }

  def findByRepoConnection(connection: Connection) = {
    connectionRepository.findByUrlIgnoreCaseAndMasterUsernameIgnoreCaseAndMasterPasswordIgnoreCaseAndWorkRepoIgnoreCase(
      connection.url, connection.masterUsername, connection.masterPassword, connection.workRepo
    )
  }

  def export(id: Long) = {
    val mapper = new ObjectMapper
    val connection = connectionRepository.findOne(id)
    (mapper.writeValueAsString(connection), connection)
  }
}
