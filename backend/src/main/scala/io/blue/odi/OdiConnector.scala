package io.blue.odi

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

import javax.sql.DataSource
import oracle.odi.core.OdiInstance
import oracle.odi.core.config.MasterRepositoryDbInfo
import oracle.odi.core.config.OdiInstanceConfig
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.core.config.WorkRepositoryDbInfo
import javax.sql.DataSource
import org.apache.commons.dbcp.BasicDataSource
import oracle.odi.core.repository.Repository
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition
import oracle.odi.core.persistence.transaction.{ITransactionManager, ITransactionStatus}

import io.blue.model.Connection


abstract class OdiConnector {

  protected def getDataSource(connection: Connection) : DataSource = {
    val jdbcClass = "oracle.jdbc.OracleDriver"
    val ds = new BasicDataSource
    ds.setRemoveAbandonedTimeout(10)
    ds.setDriverClassName(jdbcClass)
    ds.setUrl(connection.url)
    ds.setUsername(connection.masterUsername)
    ds.setPassword(connection.masterPassword)
    ds
  }

  protected def getOdiInstance(connection: Connection) = {
    val jdbcClass = "oracle.jdbc.OracleDriver"
    val ds = getDataSource(connection)
    val masterInfo= new MasterRepositoryDbInfo(
      connection.url,
      jdbcClass,
      connection.masterUsername,
      connection.masterPassword.toCharArray,
      new PoolingAttributes
    )

    val workInfo = if (connection.workRepo != null) {
      new WorkRepositoryDbInfo(connection.workRepo, new PoolingAttributes)
    } else { null }

    val masterRepo = Repository.getMasterRepository(ds)
    val odiInstance= OdiInstance.createInstance(new OdiInstanceConfig(masterInfo, workInfo, 0))

    val auth = odiInstance
      .getSecurityManager
      .createAuthentication(connection.odiUsername, connection.odiPassword.toCharArray)
    odiInstance.getSecurityManager.setCurrentThreadAuthentication(auth)
    odiInstance
  }

  protected def getOdiTxm(odiInstance: OdiInstance) = {
    val txDef = new DefaultTransactionDefinition
    val txMgr = odiInstance.getTransactionManager
    val txStatus = txMgr.getTransaction(txDef)
    val em = odiInstance.getTransactionalEntityManager
    (em, txMgr, txStatus)
  }

  protected def commit(xm: ITransactionManager, xs: ITransactionStatus): Unit = {
    xm.commit(xs)
  }

  protected def commit(odiInstance: OdiInstance): Unit = {
    val (_, xm, xs) = getOdiTxm(odiInstance)
    commit(xm, xs)
  }

}