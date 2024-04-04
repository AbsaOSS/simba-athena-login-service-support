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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory

import java.time.Instant
import java.time.Duration

class PrintFrequencyLimiterTest extends AnyFlatSpec with Matchers with MockFactory {

  behavior of "PrintFrequencyLimiter.printIfNotTooSoon"

  it should "print for the first time always" in {
    val mockPrint = mockFunction[String, Unit]
    mockPrint.expects("random").returns(())

    (new PrintFrequencyLimiter()).printIfNotTooSoon("random", mockPrint)
  }

  it should "print for the first time and not print just after that until time limit is reached" in {
    var now = Instant.now()
    val limiter = new PrintFrequencyLimiter(Duration.ofMillis(100)) {
      override private[athena] def getNow() = now
    }

    val mockPrint = mockFunction[String, Unit]
    mockPrint.expects("random").returns(())
    limiter.printIfNotTooSoon("random", mockPrint)

    // same time again, but should not print twice, this can happen!
    mockPrint.expects("randomB").returns(()).never()
    limiter.printIfNotTooSoon("randomB", mockPrint)

    now = now.plusMillis(20)
    mockPrint.expects("random2").returns(()).never()
    limiter.printIfNotTooSoon("random2", mockPrint)

    now = now.plusMillis(79) // total +99
    mockPrint.expects("random3").returns(()).never()
    limiter.printIfNotTooSoon("random3", mockPrint)

    now = now.plusMillis(2) // total +101
    mockPrint.expects("random4").returns(())
    limiter.printIfNotTooSoon("random4", mockPrint)

  }

}
