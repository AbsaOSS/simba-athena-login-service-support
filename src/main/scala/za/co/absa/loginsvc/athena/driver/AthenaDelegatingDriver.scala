/*
 * Copyright 2026 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
