package za.co.absa.loginsvc.athena

import com.simba.athena.amazonaws.auth.{BasicAWSCredentials, BasicSessionCredentials}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import za.co.absa.loginsvc.athena.model.LoginServiceProperties

import java.util.Properties

class PropertiesLoginServiceProfileCredentialsProviderTest extends AnyFlatSpec with Matchers with MockFactory {

  behavior of "PropertiesLoginServiceProfileCredentialsProvider.getCredentials"

  it should "call getCredentials with expected props - with overrides and debug" in {
    val props = new java.util.Properties()
    props.setProperty("ls_url", "http://example.com/ls")
    props.setProperty("ls_jwt2token_url", "http://example.com/jwt2token")
    props.setProperty("user", "defaultUser")
    props.setProperty("password", "defaultPass")
    props.setProperty("ls_user", "") // empty override should be ignored

    val mockSessionManager = mock[SessionManager]
    val provider = createProviderWithProps(props, Some(mockSessionManager))
    val testCreds = new BasicSessionCredentials("one", "two", "three")

    (mockSessionManager.getSessionCredentials _)
      .expects(Some(LoginServiceProperties("defaultUser", "defaultPass", "http://example.com/ls", "http://example.com/jwt2token")))
      .returns(testCreds)

    provider.getCredentials shouldBe testCreds
  }

  it should "call getCredentials with expected props - simple happy case (no valid overrides, no debug)" in {
    val props = new java.util.Properties()
    props.setProperty("ls_url", "http://example.com/ls")
    props.setProperty("ls_jwt2token_url", "http://example.com/jwt2token")

    props.setProperty("user", "defaultUser")
    props.setProperty("password", "defaultPass")
    props.setProperty("ls_user", "overrideUser")
    props.setProperty("ls_password", "overridePass")

    props.setProperty("ls_debug", "true")

    val mockSessionManager = mock[SessionManager]
    val provider = createProviderWithProps(props, Some(mockSessionManager))
    val testCreds = new BasicSessionCredentials("one", "two", "three")

    (mockSessionManager.getSessionCredentials _)
      .expects(Some(LoginServiceProperties("overrideUser", "overridePass", "http://example.com/ls", "http://example.com/jwt2token", debug = true)))
      .returns(testCreds)

    provider.getCredentials shouldBe testCreds
  }

  // these tests cover the internal state when loading, but not strictly necessary
  behavior of "PropertiesLoginServiceProfileCredentialsProvider.loadLsProperties"

  it should "load properties from DriverContext - simple happy case (no valid overrides, no debug)" in {
    val props = new java.util.Properties()
    props.setProperty("ls_url", "http://example.com/ls")
    props.setProperty("ls_jwt2token_url", "http://example.com/jwt2token")
    props.setProperty("user", "defaultUser")
    props.setProperty("password", "defaultPass")
    props.setProperty("ls_user", "") // empty override should be ignored
    val provider = createProviderWithProps(props)

    val lsProps = provider.loadLsProperties
    lsProps.user shouldBe "defaultUser"
    lsProps.pass shouldBe "defaultPass"
    lsProps.lsUrl shouldBe "http://example.com/ls"
    lsProps.jwt2tokenSvcUrl shouldBe "http://example.com/jwt2token"
    lsProps.debug shouldBe false
  }

  it should "load properties from DriverContext - with overrides and debug" in {
    val props = new java.util.Properties()
    props.setProperty("ls_url", "http://example.com/ls")
    props.setProperty("ls_jwt2token_url", "http://example.com/jwt2token")

    props.setProperty("user", "defaultUser")
    props.setProperty("password", "defaultPass")
    props.setProperty("ls_user", "overrideUser")
    props.setProperty("ls_password", "overridePass")

    props.setProperty("ls_debug", "true")
    val provider = createProviderWithProps(props)

    val lsProps = provider.loadLsProperties

    lsProps.user shouldBe "overrideUser"
    lsProps.pass shouldBe "overridePass"
    lsProps.lsUrl shouldBe "http://example.com/ls"
    lsProps.jwt2tokenSvcUrl shouldBe "http://example.com/jwt2token"
    lsProps.debug shouldBe true
  }

  it should "fail when there are required properties missing" in {
    val props = new java.util.Properties()
    val provider = createProviderWithProps(props)

    (the[RuntimeException] thrownBy {
      provider.loadLsProperties
    }).getMessage should include(
      "Config field 'ls_url' is missing!"
    )

    props.setProperty("ls_url", "http://example.com/ls")
    val provider2 = createProviderWithProps(props)

    (the[RuntimeException] thrownBy {
      provider2.loadLsProperties
    }).getMessage should include(
      "Config field 'ls_jwt2token_url' is missing!"
    )

  }

  private def createProviderWithProps(props: java.util.Properties, optSessionManager: Option[SessionManager] = None): PropertiesLoginServiceProfileCredentialsProvider = {
    optSessionManager.fold {
      new PropertiesLoginServiceProfileCredentialsProvider {
        override def initializeDriverContextWithProperties(): Properties = props
      }
    }{ customSessionManager =>
      new PropertiesLoginServiceProfileCredentialsProvider {
        override def initializeDriverContextWithProperties(): Properties = props
        override def initializeSessionManager(): SessionManager = customSessionManager
      }
    }

  }

}
