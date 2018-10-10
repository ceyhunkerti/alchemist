package io.blue.model

import java.util.Date
import javax.persistence._
import javax.validation.constraints.{NotNull}

import scala.beans.BeanProperty


@Entity(name="pulse_log")
class PulseLog {

  def this(id: Long) {
    this()
    this.id = id
  }

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  var pulse: Pulse = _

  @BeanProperty
  var date: Date = _

  @BeanProperty
  @NotNull
  @Column(columnDefinition = "varchar(max)")
  var content: String = _

}