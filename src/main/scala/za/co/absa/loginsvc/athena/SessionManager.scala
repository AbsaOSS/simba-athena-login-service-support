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

import com.simba.athena.amazonaws.auth.BasicSessionCredentials
import requests.{RequestAuth, Response}
import za.co.absa.loginsvc.athena.model.{LsJwtTokens, StsSessionCredentials, LoginServiceProperties}

import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

class SessionManager(
  val lsProperties: Option[LoginServiceProperties],
  expirationAdvance: Duration = Duration.ofMinutes(10)
) {

  private val savedCreds: AtomicReference[Option[StsSessionCredentials]] = new AtomicReference[Option[StsSessionCredentials]](None)

  def getSessionCredentials(externalLsProperties: Option[LoginServiceProperties] = None): BasicSessionCredentials = {
    if (externalLsProperties.exists(_.debug)) {
      println(s"getSessionCredentials: external LS Properties are used")
    }

    val useProps = externalLsProperties.orElse(lsProperties).getOrElse {
      throw new IllegalArgumentException("No LS Properties provided (neither internal nor external)")
    }

    val sessionCreds = savedCreds.updateAndGet {
      case Some(creds) if !creds.expiresIn(expirationAdvance) =>
        // no need to renew
        Some(creds)
      case _ =>
        println("renewing creds")
        val newCreds = getNewSessionCredentials(useProps)
        Some(newCreds)
    }

    sessionCreds.get.toBasicSessionCredentials
  }

  private[athena] def getNewSessionCredentials(props: LoginServiceProperties): StsSessionCredentials = {
    val jwt = getJwtFromLs(props)
    getTokenFromJwt(jwt, props.jwt2tokenSvcUrl)
  }

  private[athena] def getJwtFromLs(props: LoginServiceProperties): String = {
    val r: Response = requests.post(props.lsUrl, auth = new RequestAuth.Basic(props.user, props.pass))
    if (r.statusCode == 200 && r.contentType.exists(_.contains("application/json"))) {
      val jsonPayload = r.text()
      val jwt = LsJwtTokens.fromJson(jsonPayload)
      if (props.debug) {
        println(s"SessionManager.getJwtFromLs: jwt = $jwt")
      }
      jwt
    } else {
      throw new IllegalStateException(s"Login via LS @ ${props.lsUrl} failed: $r")
    }

  }

  private[athena] def getTokenFromJwt(jwt: String, jwt2tokenServiceUrl: String): StsSessionCredentials = {

    val r: Response = requests.post(jwt2tokenServiceUrl, RequestAuth.Bearer(jwt))
    if (r.statusCode == 200 && r.contentType.exists(_.contains("application/json"))) {
      val jsonPayload = r.text()
      val sessionCredentials = StsSessionCredentials.fromJson(jsonPayload)
      println(s"SessionManager.getTokenFromJwt: sessionCredentials = $sessionCredentials")
      sessionCredentials
    } else {
      throw new IllegalStateException(s"Obtaining the session token @ $jwt2tokenServiceUrl from jwt $jwt failed: $r")
    }
  }

}
