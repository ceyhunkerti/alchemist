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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.blue.model.odi.runtime.loadplan.LoadPlan

@Entity(name="load_plan")
@Table(
  uniqueConstraints =
    Array(new UniqueConstraint(columnNames=Array("internalId", "connection_id") ) )
)
class LoadPlan {

  def this(id: Long) {
    this()
    this.id = id
  }

  def this(lp: io.blue.model.odi.runtime.loadplan.LoadPlan) {
    this()
    this.internalId = lp.internalId.asInstanceOf[java.lang.Number].longValue
    this.name = lp.name
  }

  def this(lp: io.blue.model.odi.runtime.loadplan.LoadPlan, connectionId: Long) {
    this(lp)
    this.connection = new io.blue.model.Connection(connectionId)
  }


  @Id
  @GeneratedValue
  var id: Long = _

  @NotNull
  @Fetch(value= FetchMode.JOIN)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name="connection_id")
  var connection: Connection = _

  @BeanProperty
  var internalId: Long = _

  @BeanProperty
  var name: String = _

  @BeanProperty
  var avgDuration: java.lang.Float = _

  @BeanProperty
  var minDuration: java.lang.Float = _

  @BeanProperty
  var maxDuration: java.lang.Float = _
}

