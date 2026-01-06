/*
 * Copyright 2025 ABSA Group Limited
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

package za.co.absa.loginsvc.athena

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import za.co.absa.loginsvc.athena.ctx.DriverContext
import za.co.absa.loginsvc.athena.model.LoginServiceProperties

import java.util.Properties

class PropertiesLoginServiceProfileCredentialsProvider() extends AWSCredentialsProvider {
  println("=== PropertiesLoginServiceProfileCredentialsProvider ===")

  private var arguments: String = _

  // Simba calls this to pass the arguments string
  def setArguments(args: String): Unit =
    arguments = args

  private val sessionManager: SessionManager = new SessionManager(None)
  private val credentialsPrintLimiter = new PrintFrequencyLimiter()

  private[athena] def initializeDriverContextWithProperties(): Properties = {
    val props = DriverContext.get
    if (props == null)
      throw new IllegalStateException(
        "DriverContext not initialized. Make sure that your Athena driver class name is: za.co.absa.loginsvc.athena.driver.AthenaDelegatingDriver"
      )

    props
  }

  private def loadLsProperties: LoginServiceProperties = {
    val props = initializeDriverContextWithProperties()

    val lsDebug = props.getProperty("ls_debug", "false").toBoolean

    val lsUrl = props.getProperty("ls_url")
    val jwt2tokenUrl = props.getProperty("ls_jwt2token_url")

    if (lsUrl == null || lsUrl.isEmpty)
      throw new RuntimeException("Config field 'ls_url' is missing!")
    if (jwt2tokenUrl == null || jwt2tokenUrl.isEmpty)
      throw new RuntimeException("Config field 'ls_jwt2token_url' is missing!")

    val user = props.getProperty("user") // default UI field value
    val lsUsername = props.getProperty("ls_user") // optional custom override

    val password = props.getProperty("password") //
    val lsPassword = props.getProperty("ls_password") // optional custom override


    val usernameUsed = if (lsUsername == null || lsUsername.isEmpty) {
      user
    } else {
      if (lsDebug) {
        println(s"username override from ls_username is used: $lsUsername")
      }
      lsUsername
    }

    val passwordUsed = if (lsPassword == null || lsPassword.isEmpty) {
      password
    } else {
      if (lsDebug) {
        println(s"password override from ls_password is used: *** (${lsPassword.length} chars)")
      }
      lsPassword
    }


    if (lsDebug) {
      for (key <- props.keySet().toArray()) {
        val value = props.getProperty(key.toString)

        if (key.toString.contains("password"))
          println(s"Prop: $key = *** (${value.length} chars)")
        else
          println(s"Prop: $key = $value")
      }
    }

    LoginServiceProperties(usernameUsed, passwordUsed, lsUrl, jwt2tokenUrl, lsDebug)
  }

  override def getCredentials: AWSCredentials = {
    val lsProps = loadLsProperties

    val creds = sessionManager.getSessionCredentials(Some(lsProps)) // internally manages expiration and renewal
    credentialsPrintLimiter.printIfNotTooSoon(s"getCredentials: ${lsProps.user}/*** (${lsProps.pass.length} chars) -> using creds: ${creds.getAWSAccessKeyId}, ...")
    creds
  }

  override def refresh(): Unit = {}

}
