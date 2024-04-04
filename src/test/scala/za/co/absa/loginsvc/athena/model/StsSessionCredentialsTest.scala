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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.time.Instant

class StsSessionCredentialsTest extends AnyFlatSpec with Matchers {
  behavior of "StsSessionToken.fromJson"

  it should "correctly parse StsSessionCredentials from expected json" in {
    val json: String =
      """{
        |"accessKeyId": "ACCESSKEY12345678901",
        |"secretAccessKey": "SecretHere123",
        |"sessionToken": "ABCdef123/",
        |"expiration": "2024-03-21T10:20:30Z"
        |}""".stripMargin

    val expectedSessionCreds = StsSessionCredentials(
      "ACCESSKEY12345678901", "SecretHere123", "ABCdef123/",
      Instant.parse("2024-03-21T10:20:30Z")
    )

    StsSessionCredentials.fromJson(json) shouldBe expectedSessionCreds
  }

  it should "fail to parse StsSessionCredentials from non-expected json" in {
    val json: String =
      """{
        |"not": "expected"
        |}
        |""".stripMargin

    an [IllegalStateException] should be thrownBy {
      StsSessionCredentials.fromJson(json)
    }
  }
}
