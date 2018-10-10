package io.blue.repository


import org.springframework.data.jpa.repository._
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param

import io.blue.model._

@Repository
trait LoadPlanRepository extends JpaRepository[io.blue.model.LoadPlan, java.lang.Long] {
  def findByConnectionId(connectionId: Long): java.util.List[io.blue.model.LoadPlan]
}
