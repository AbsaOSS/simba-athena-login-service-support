/*
 * Copyright 2024 ABSA Group Limited
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


class LoginServiceProfileCredentialsProvider(user: String, pass: String, lsUrl: String, jwt2tokenSvcUrl: String) extends AWSCredentialsProvider {
  introLogging()

  private def introLogging(): Unit = {
    println()
    println("=== LoginServiceProfileCredentialsProvider ===")
    println(s"User: $user")
    println(s"Password: *** (${pass.size} chars)")
    println(s"LS URL: $lsUrl")
    println(s"JWT2Token URL: $jwt2tokenSvcUrl")
  }

  private val sessionManager: SessionManager = new SessionManager(user, pass, lsUrl, jwt2tokenSvcUrl)

  private val credentialsPrintLimiter = new PrintFrequencyLimiter()

  override def getCredentials: AWSCredentials = {
    val creds = sessionManager.getSessionCredentials // internally manages expiration and renewal
    credentialsPrintLimiter.printIfNotTooSoon(s"getCredentials: using creds: ${creds.getAWSAccessKeyId}, ...")
    creds
  }

  override def refresh(): Unit = {}

}
