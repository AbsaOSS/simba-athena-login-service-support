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

import java.time.{Duration, Instant}
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference

class PrintFrequencyLimiter(minStep: TemporalAmount = Duration.ofSeconds(5)) {
  private val lastPrint = new AtomicReference(Instant.now().minus(minStep).minus(minStep)) // 2x to be > older, not just >= on the first isAfter

  private[athena] def getNow(): Instant = Instant.now()

  def printIfNotTooSoon(line: String, printFn: (String => Unit) = println): Unit = {
    val now = getNow()
    lastPrint.updateAndGet { oldValue =>
      if (now.isAfter(oldValue.plus(minStep))) {
        printFn(line)
        now // update bc. enough time has passed
      } else
        oldValue // too soon
    }

  }
}
