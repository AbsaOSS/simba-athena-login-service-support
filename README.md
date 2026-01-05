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

#### Add extra jar into Athena Driver
   1. build via `mvn package` or obtain prebuilt jar from above
   2. In DBeaver: Database -> Driver manager
   3. Select AWS/Athena -> Edit...
   4. Libraries -> Add File -> Select `simba-athena-driver-login-service-support-0.3.0-jar-with-dependencies.jar` file (packaged from previous step)

   5. Have your Login service URL ready (e.g. something like https://login-service-here.domain.com/token/generate) and use it in the next steps for `<LS_URL_GOES_HERE>`
   6. Have your Jwt2Token URL ready (.e.g something like https://my.domain.com/user-temporary-credentials-from-jwt-bearer) and use it in the next steps for `<JWT2TOKEN_URL_GOES_HERE>`

#### Configure connection to use Login Service
There are two classes available to choose from, depending on your use case:
 - `LoginServiceProfileCredentialsProvider` (older, works well for DBeaver 23.1 and older - see limitations below)
 - `PropertiesLoginServiceProfileCredentialsProvider` (newer, works better for DBeaver 23.2+ )

##### `LoginServiceProfileCredentialsProvider` option 
   This provider loads everything from the `AwsCredentialsProviderArguments`.
   This option works well for DBeaver versions up to 23.1. (this is the last minor version where `${password}` token
   is available to be used in `AwsCredentialsProviderArguments` value).

   In DriverSettings, check that the `Class Name` is: `com.simba.athena.jdbc.Driver` (default value).

   Set Driver properties as follows:
   1. `AwsCredentialsProviderClass=za.co.absa.loginsvc.athena.LoginServiceProfileCredentialsProvider`
   2. `AwsCredentialsProviderArguments=${user},${password},<LS_URL_GOES_HERE>,<JWT2TOKEN_URL_GOES_HERE>`

   With this provider, you can use DBeaver's standard username and password fields to provide your Login Service credentials.

##### `PropertiesLoginServiceProfileCredentialsProvider` option
   This provider loads configuration individual driver properties

   In DriverSettings, set the `Class Name` to: `za.co.absa.loginsvc.athena.driver.AthenaDelegatingDriver`

   Set Driver properties as follows:
   1. `AwsCredentialsProviderClass=za.co.absa.loginsvc.athena.PropertiesLoginServiceProfileCredentialsProvider`
   2. Define a (new) User property `ls_user` with your username
   3. Define a (new) User property `ls_password` with your password (this will get masked and secured by DBeaver)
   4. Define a (new) User property `ls_url` with value `<LS_URL_GOES_HERE>`
   5. Define a (new) User property `ls_jwt2token_url` with value `<JWT2TOKEN_URL_GOES_HERE>`
   6. Leave `AwsCredentialsProviderArguments` unset

   With this provider, DBeaver's standard username and password fields are ignored (User properties values are used instead).
   You may enter e.g. `notused`/`notused` for username/password value.
   
### How to release
 - Commit with final version in pom.xml (e.g. 1.2.3)
 - Tag this commit with the said version (1.2.3)
 - Create a release in GH linking the version (Release WF will build the jar and append it to the release)
 - (optionally, but nice) Commit new non-final snapshot version - e.g. 1.3.0-SNAPSHOP (next minor)

## Contribution
Inspired by from [https://github.com/neitomic/simba-athena-driver-sso-support](https://github.com/neitomic/simba-athena-driver-sso-support)