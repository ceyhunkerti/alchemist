package io.blue.model.odi.topology

import java.util.Date
import scala.collection.JavaConversions._
import oracle.odi.domain.topology.OdiPhysicalSchema
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.annotation._


import io.blue.model.odi.support.OdiEntity


class PhysicalSchema extends OdiEntity {

  def this(o: OdiPhysicalSchema) {
    this()
    init(o)
    if(o.getTechnology.isSchemaSupported){
      schemaName = o.getSchemaName
      workSchemaName = o.getWorkSchemaName
    }
  }

  var dataServer: DataServer = _

  var schemaName: String = _

  var workSchemaName: String = _
}