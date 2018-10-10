// package io.blue.repository

// import scala.collection.JavaConversions._
// import org.springframework.data.jpa.repository._
// import org.springframework.stereotype.Repository
// import org.springframework.data.repository.query.Param

// import io.blue.model._

// @Repository
// trait LoadPlanInstanceRunRepository extends JpaRepository[io.blue.model.LoadPlanInstanceRun, java.lang.Long] {
//   def findByLoadPlanInstanceLoadPlanIdIn(plans: java.util.Collection[Long]): java.util.List[io.blue.model.LoadPlanInstanceRun]
//   def findByLoadPlanInstanceLoadPlanIn(plans: java.util.Collection[io.blue.model.LoadPlan]): java.util.List[io.blue.model.LoadPlanInstanceRun]
//   def findByInternalIdAndLoadPlanInstanceLoadPlan(internalId: String, loadPlan: LoadPlan): io.blue.model.LoadPlanInstanceRun
// }
