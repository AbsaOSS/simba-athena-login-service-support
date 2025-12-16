package za.co.absa.loginsvc.athena.driver

import java.sql.{Connection, Driver, DriverPropertyInfo}
import java.util.Properties
import za.co.absa.loginsvc.athena.ctx.DriverContext

import java.util.logging.Logger

class AthenaDelegatingDriver extends Driver {
  println("/// AthenaDelegatingDriver initialized ///")

  private val delegate =
    new com.simba.athena.jdbc42.Driver

  override def connect(
                        url: String,
                        info: Properties
                      ): Connection = {

    // Capture once and keep
    DriverContext.set(info)
    delegate.connect(url, info)

  }

  override def acceptsURL(url: String): Boolean = {
    delegate.acceptsURL(url)
  }

  override def getPropertyInfo(url: String,info: Properties): Array[DriverPropertyInfo] = {
    delegate.getPropertyInfo(url, info)
  }

  override def getMajorVersion: Int =
    delegate.getMajorVersion

  override def getMinorVersion: Int =
    delegate.getMinorVersion

  override def jdbcCompliant(): Boolean =
    delegate.jdbcCompliant()

  override def getParentLogger: Logger =
    delegate.getParentLogger
}
