package io.blue.repository

import org.springframework.data.jpa.repository._
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param

import io.blue.model.Pulse

@Repository
trait PulseRepository extends JpaRepository[Pulse, java.lang.Long] {
  def findByConnectionId(id: Long): java.util.List[Pulse]
  def findByActive(active: Boolean): java.util.List[Pulse]
  def findByConnectionIdAndActive(id: Long, active: Boolean): java.util.List[Pulse]
}