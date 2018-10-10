package io.blue.model

import javax.persistence._
import javax.validation.constraints.{NotNull}

import scala.beans.BeanProperty

object PulseType {
  val LOAD_PLAN_ERROR = "LOAD_PLAN_ERROR"
  val LOAD_PLAN_DURATION = "LOAD_PLAN_DURATION"
  val AGENT_DOWN = "AGENT_DOWN"
}


@Entity(name="pulse")
class Pulse {

  def this(id: Long) {
    this()
    this.id = id
  }

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var active: Boolean = true

  @BeanProperty
  @NotNull
  @Column(unique=true)
  var name: String = _

  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  var connection: Connection = _

  @NotNull
  @BeanProperty
  var pulseType: String = _

  @BeanProperty
  var mail: String = _

  @BeanProperty
  @NotNull
  @Column(columnDefinition = "varchar(max)")
  var content: String = _

}