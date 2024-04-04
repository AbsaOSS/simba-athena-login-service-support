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

import java.time.{Duration, Instant}

class LsJwtTokensTest extends AnyFlatSpec with Matchers {

  behavior of "LsJwtTokens.expiresIn"

  val now = Instant.now()
  val oneHourAgo = now.minus(Duration.ofHours(1))
  val fiveMinutesLater = now.plus(Duration.ofMinutes(5))
  val oneHourLater = now.plus(Duration.ofHours(1))

  val creds1 = StsSessionCredentials("accessKeyId1", "secretAccessKey1", "sessionToken1", now)

  it should "correctly return (not) expired with 0-cushion" in {
    val cushion = Duration.ofMinutes(0)

    creds1.copy(expiration = oneHourAgo).expiresIn(cushion) shouldBe true
    creds1.expiresIn(cushion) shouldBe true // internal Instant.now will be just after
    creds1.copy(expiration = oneHourLater).expiresIn(cushion) shouldBe false
  }

  it should "correctly return (not) expired with Xmin-cushion" in {
    val cushion = Duration.ofMinutes(10)

    creds1.copy(expiration = oneHourAgo).expiresIn(cushion) shouldBe true // long expired
    creds1.copy(expiration = now).expiresIn(cushion) shouldBe true // just expired
    creds1.copy(expiration = fiveMinutesLater).expiresIn(cushion) shouldBe true // not expired yet, but sooner than cushion

    creds1.copy(expiration = oneHourLater).expiresIn(cushion) shouldBe false // not expired and will expire later than cushion
  }

  behavior of "LsJwtTokens.fromJson"

  it should "correctly parse JWT from valid Tokens json" in {
    val json: String =
      """{
        |"token": "abc.def.ghi",
        |"refresh": "123.456.789"
        |}
        |""".stripMargin

    LsJwtTokens.fromJson(json) shouldBe "abc.def.ghi"
  }

  it should "fail to parse JWT from invalid Tokens json " in {
    val json: String =
      """{
        |"not": "expected"
        |}
        |""".stripMargin

    an [IllegalStateException] should be thrownBy {
      LsJwtTokens.fromJson(json)
    }
  }

}
