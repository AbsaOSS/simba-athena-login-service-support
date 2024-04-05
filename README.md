# simba-athena-login-service-support
Credentials provider for Simba Athena driver for [Login Service](https://github.com/AbsaOSS/login-service)

## How to use?

### Build Jar package

```bash
mvn package
```

### Use pre-built jar package
You can download the pre-built jar file from the latest release [here](https://github.com/AbsaOSS/simba-athena-login-service-support/releases)


### Config athena driver

1. Add extra jar into Athena Driver
   1. build via `mvn package` or obtain prebuilt jar from above
   2. In DBeaver: Database -> Driver manager
   3. Select AWS/Athena -> Edit...
   4. Libraries -> Add File -> Select `simba-athena-driver-login-service-support-1.0-jar-with-dependencies.jar` file (packaged from previous step)

2. Configure connection driver properties
   1. `AwsCredentialsProviderClass=za.co.absa.loginsvc.athena.LoginServiceProfileCredentialsProvider`
   2. Have your Login service URL ready (e.g. something like https://login-service-here.domain.com/token/generate) 
   3. Have your Jwt2Token URL ready (.e.g something like https://my.domain.com/user-temporary-credentials-from-jwt-bearer)
   4. `AwsCredentialsProviderArguments=${user},${password},LS_URL_GOES_HERE,JWT2TOKEN_URL_GOES_HERE`

### How to release
 - Commit with final version in pom.xml (e.g. 1.2.3)
 - Tag this commit with the said version (1.2.3)
 - Create a release in GH linking the version (Release WF will build the jar and append it to the release)
 - (optionally, but nice) Commit new non-final snapshot version - e.g. 1.3.0-SNAPSHOP (next minor)

## Contribution
Inspired by from [https://github.com/neitomic/simba-athena-driver-sso-support](https://github.com/neitomic/simba-athena-driver-sso-support)