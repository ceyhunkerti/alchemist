// package io.blue.model

// import scala.beans.BeanProperty
// import java.util.Date
// import javax.persistence._
// import org.hibernate.validator.constraints._
// import org.hibernate.annotations.Fetch
// import org.hibernate.annotations.FetchMode
// import javax.validation.constraints.{NotNull}
// import com.fasterxml.jackson.annotation._
// import com.fasterxml.jackson.databind.annotation.JsonSerialize
// import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.{ Status, OdiLoadPlanInstanceRunId }

// @Entity(name="load_plan_instance_runs")
// @Table(
//   uniqueConstraints =
//     Array(new UniqueConstraint(columnNames=Array("internalId", "load_plan_instance_id") ) )
// )
// class LoadPlanInstanceRun {

//   def this(id: Long) {
//     this()
//     this.id = id
//   }

//   def this(run: io.blue.model.odi.runtime.lpi.LoadPlanInstanceRun, connectionId: Long) {
//     this()
//     this.startTime = run.startTime
//     this.endTime = run.endTime
//     this.status = run.status
//     val i = run.internalId.asInstanceOf[OdiLoadPlanInstanceRunId]
//     this.internalId = s"${i.getLoadPlanInstance}-${i.getRunCount}"
//     this.loadPlanInstance = new io.blue.model.LoadPlanInstance(run.loadPlanInstance, connectionId)
//   }

//   @Id
//   @GeneratedValue
//   var id: Long = _

//   @BeanProperty
//   var internalId: String = _

//   @BeanProperty
//   var startTime: Date = _

//   @BeanProperty
//   var endTime: Date = _

//   @BeanProperty
//   var status: Status = _

//   @NotNull
//   @Fetch(value= FetchMode.JOIN)
//   @ManyToOne(optional = false, fetch = FetchType.EAGER)
//   @JoinColumn(name = "load_plan_instance_id")
//   var loadPlanInstance: io.blue.model.LoadPlanInstance = _

//   @JsonSerialize
//   def duration = endTime.getTime - startTime.getTime

// }

