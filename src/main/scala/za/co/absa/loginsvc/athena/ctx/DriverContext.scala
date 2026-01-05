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
package za.co.absa.loginsvc.athena.ctx

import java.util.Properties
import java.util.concurrent.atomic.AtomicReference

object DriverContext {

  private val propsRef = new AtomicReference[Properties]()

  def set(props: Properties): Unit = {
    propsRef.set(props)
  }

  def get: Properties = {
    propsRef.get()
  }

  // intentionally no clear method to keep the props for the lifetime of the JVM process
}
