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

@Entity(name="connections")
class Connection {

  def this(id: Long) {
    this()
    this.id = id
  }

  @Id
  @GeneratedValue
  var id: Long = _

  @Column(unique = true)
  @NotNull(message="Connection name can not be empty")
  var name: String = _

  @NotNull(message="JDBC URL can not be empty")
  var url: String = _

  @NotNull(message="Master repository username can not be empty")
  var masterUsername: String = _

  @NotNull(message="Master repository password can not be empty")
  var masterPassword: String = _

  var workRepo: String = _

  @NotNull(message="ODI username can not be empty")
  var odiUsername: String = _

  @NotNull(message="ODI password can not be empty")
  var odiPassword: String = _

  @BeanProperty
  var isDefault: Boolean = _

  @NotNull
  @Fetch(value= FetchMode.JOIN)
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  var user: User = _

  @Fetch(value= FetchMode.SELECT)
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "connection")
  @JsonIgnore
  var pulses: java.util.Set[Pulse] = new java.util.HashSet[Pulse]

}

