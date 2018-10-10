package io.blue.repository


import org.springframework.data.jpa.repository._
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param

import io.blue.model.{Connection, User}

@Repository
trait ConnectionRepository extends JpaRepository[Connection, java.lang.Long] {
  def findByIdAndUser(id: Long, user: User): Connection
  def findAllByUser(user: User): java.util.List[Connection]
  def countByUser(user: User): Long
  def findByUrlIgnoreCaseAndMasterUsernameIgnoreCaseAndMasterPasswordIgnoreCaseAndWorkRepoIgnoreCase(
    url: String, masterUsername: String, masterPassword: String, workRepo: String
  ): java.util.List[Connection]
}
