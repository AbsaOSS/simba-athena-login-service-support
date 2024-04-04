# simba-athena-login-service-support
Credentials provider for Simba Athena driver for [Login Service](https://github.com/AbsaOSS/login-service)

## How to use?

### Build Jar package

```bash
mvn package
```

### Use pre-built jar package
Not yet available (TODO future: You can download the pre-built jar file from the latest release [here](https://github.com/AbsaOSS/simba-athena-login-service-support/releases))


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


## Contribution
Inspired by from [https://github.com/neitomic/simba-athena-driver-sso-support](https://github.com/neitomic/simba-athena-driver-sso-support)