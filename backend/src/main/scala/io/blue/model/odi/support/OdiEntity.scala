package io.blue.model.odi.support

import java.util.Date
import scala.collection.JavaConversions._
import scala.collection.Iterable
import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

import oracle.odi.domain.support.AbstractOdiEntity

class OdiEntity {

  def this(o: AbstractOdiEntity) {
    this()
    init(o)
  }

  protected def init(o: AbstractOdiEntity) {
    internalId = o.getInternalId
    name = o.getName
    firstUser = o.getFirstUser
    firstDate = o.getFirstDate
    lastUser = o.getLastUser
    lastDate = o.getLastDate
    globalId = o.getGlobalId
    odiEntity= o
  }


  @JsonDeserialize(as=classOf[java.lang.Number])
  var internalId: java.io.Serializable = _

  var name: String = _

  var firstUser: String = _

  var firstDate: Date = _

  var lastUser: String = _

  var lastDate: Date = _

  var globalId: String = _

  @JsonIgnore
  var odiEntity: AbstractOdiEntity = _
}