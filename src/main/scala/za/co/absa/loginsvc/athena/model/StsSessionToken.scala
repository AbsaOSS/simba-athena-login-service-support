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
package za.co.absa.loginsvc.athena.model

import com.simba.athena.amazonaws.auth.BasicSessionCredentials
import io.circe.generic.auto._
import io.circe.parser._

import java.time.{Duration, Instant}

case class StsSessionCredentials(
  accessKeyId: String,
  secretAccessKey: String,
  sessionToken: String,

  /**
   * e.g. "2024-03-21T10:20:39Z"
   */
  expiration: Instant
) {

  def expiresIn(duration: Duration): Boolean = {
    // without any cushions, token expires if: Instant.now().isAfter(expiration)
    // with cushion we renew a little earlier:
    Instant.now().plus(duration).isAfter(expiration)
  }

  def toBasicSessionCredentials: BasicSessionCredentials = {
    new BasicSessionCredentials(accessKeyId, secretAccessKey, sessionToken)
  }
}

object StsSessionCredentials {

  def fromJson(json: String): StsSessionCredentials = {
    val decodedSessionsCredentials = decode[StsSessionCredentials](json)
    val successfullyDecodedSessionCredentials = decodedSessionsCredentials match {
      case Right(creds) => creds
      case Left(error) => throw new IllegalStateException(s"Could not decode session token from $json: $error")
    }

    successfullyDecodedSessionCredentials
  }
}

