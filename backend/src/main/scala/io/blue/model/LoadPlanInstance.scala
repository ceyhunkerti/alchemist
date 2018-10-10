package io.blue.model

import scala.beans.BeanProperty
import java.util.Date
import javax.persistence._
import org.hibernate.validator.constraints._
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.validation.constraints.{NotNull}
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import oracle.odi.domain.runtime.lpi.OdiLoadPlanInstanceRun.{ Status, OdiLoadPlanInstanceRunId }

@Entity(name="load_plan_instances")
@Table(
  uniqueConstraints =
    Array(new UniqueConstraint(columnNames=Array("internalId", "load_plan_id") ) )
)
class LoadPlanInstance {

  def this(id: Long) {
    this()
    this.id = id
  }

  def this(instance: io.blue.model.odi.runtime.lpi.LoadPlanInstance, connectionId: Long) {
    this()
    this.internalId = instance.internalId.asInstanceOf[Number].longValue
    this.loadPlan = new io.blue.model.LoadPlan(instance.loadPlan, connectionId)
  }

  @Id
  @GeneratedValue
  var id: Long = _

  @BeanProperty
  var internalId: Long = _

  @NotNull
  @Fetch(value= FetchMode.JOIN)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "load_plan_id")
  var loadPlan: io.blue.model.LoadPlan = _

}

