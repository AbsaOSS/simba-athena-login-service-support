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
