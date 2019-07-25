# Description - UNDER CONSTRUCTION!!!
This sample uses the SAP application router as a web server and forwards requests to a Java Spring back-end application running on Cloud Foundry.
In a typcal UI5 application, the application router serves HTML files and REST data would be provided by a back-end application. To focus on the security part, UI5 has been omitted.

# Coding
This sample is using the spring-security project. As of version 5 of spring-security, this includes the OAuth resource-server functionality. The security configuration needs to configure JWT for authentication.
Please see the [`spring-xsuaa` descriptions](../spring-xsuaa/README.md) for details.

# Deployment To Cloud Foundry or SAP HANA XS Advanced
To deploy the application, the following steps are required:
- Configure the Application Router
- Compile the Java application
- Create an XSUAA service instance
- Configure manifest.yml
- Deploy the application
- Assign Role to your user
- Access the application

## Configure the Application Router

The [Application Router](./approuter/package.json) is used to provide a single entry point to a business application that consists of several different apps (microservices). It dispatches requests to backend microservices and acts as a reverse proxy. The rules that determine which request should be forwarded to which _destinations_ are called _routes_. The application router can be configured to authenticate the users and propagate the user information. Finally, the application router can serve static content.

## Compile the Java Application
Run maven to package the application
```shell
mvn clean package -DskipTests
```
> Note: As of now the JUnit tests may not run unless you've configured the Url of the Authorization Decision Controller (`ADC_URL`) as part of system environment variable or via the [application-uaamock.properties](src/main/resources/application-uaamock.properties). 
After successful deployment you can enter the ADC service url or alternatively you can refer to a ADC service that runs locally, e.g. in a docker container.

## Create the XSUAA Service Instance
Use the [xs-security.json](./xs-security.json) to define the authentication settings and create a service instance
```shell
cf create-service xsuaa application xsuaa-adc -c xs-security.json
```

## Configure the manifest
The [vars](../vars.yml) contains hosts and paths that you might need to adopt.

## Deploy the application
Deploy the application using cf push. It will expect 1 GB of free memory quota.

```shell
cf push --vars-file ../vars.yml
```

## Access Authorization Decision Controller (ADC) via Open Policy Agent endpoints
* `https://adc-service-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/policies`
* `https://adc-service-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/data`
* `https://adc-service-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/data/rbac/allow` POST request with Content-Type: application/json and payload:
```
{
	"input": {
		"token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImprdSI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzMxOTUvdG9rZW5fa2V5cyIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkifQ.eyJ6ZG4iOiIiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnsiY29uZmlkZW50aWFsaXR5X2xldmVsIjpbIlBVQkxJQyJdfSwidXNlcl9uYW1lIjoidmlld2VyIiwib3JpZ2luIjoidXNlcklkcCIsInhzLnN5c3RlbS5hdHRyaWJ1dGVzIjp7InhzLnNhbWwuZ3JvdXBzIjpbIkcxIl0sInhzLnJvbGVjb2xsZWN0aW9ucyI6WyJWaWV3ZXIiXX0sImV4dF9hdHRyIjp7InpkbiI6IiJ9LCJ6aWQiOiJ1YWEiLCJncmFudF90eXBlIjoidXJuOmlldGY6cGFyYW1zOm9hdXRoOmdyYW50LXR5cGU6c2FtbDItYmVhcmVyIiwic2NvcGUiOlsic3ByaW5nLXNlY3VyaXR5LWFkYy11c2FnZSF0MTQ4NjYuUmVhZCJdLCJleHAiOjY5NzQwMzE2MDAsImlhdCI6MTU2Mzk4MjgzMCwiZW1haWwiOiJ2aWV3ZXJAdGVzdC5vcmciLCJjaWQiOiJzYi1zcHJpbmctc2VjdXJpdHktYWRjLXVzYWdlIXQxNDg2NiJ9.utoeiGdzuo4B7hXmY1sZ--1cKyoVQaL27-mNbrCO1D52wGlXGluggZo8dfwEBAjP2n9R2xToTTeQeCR136_5vbPsoF64vbif_9AQOBChuT0wVYDAOONMelIyE9YCZAPchUIr15z59AjhL1dxiQhi5s-dLpcN6fM30vL03iTZg3WN3I9Ib6x3M21in1rKa-mb3fmxiZSM3wcCkkt4viH0fAsmGpzbZUzUcXl1TALxach2wC3dJzcJLceeilerQmNGQ9jRpJELamD2avC5BIQmPRdvuW962bf-fiKKBo2YErlEJ_yKdFHMT3RecRBsK3KyKg3Du7cqCna_6471MnlVdw",
		"role": "Viewer"
	}
}
```
should return true, whereas this payload with same `token` but different `role`:
```
{
	"input": {
		"token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImprdSI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzMxOTUvdG9rZW5fa2V5cyIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkifQ.eyJ6ZG4iOiIiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnsiY29uZmlkZW50aWFsaXR5X2xldmVsIjpbIlBVQkxJQyJdfSwidXNlcl9uYW1lIjoidmlld2VyIiwib3JpZ2luIjoidXNlcklkcCIsInhzLnN5c3RlbS5hdHRyaWJ1dGVzIjp7InhzLnNhbWwuZ3JvdXBzIjpbIkcxIl0sInhzLnJvbGVjb2xsZWN0aW9ucyI6WyJWaWV3ZXIiXX0sImV4dF9hdHRyIjp7InpkbiI6IiJ9LCJ6aWQiOiJ1YWEiLCJncmFudF90eXBlIjoidXJuOmlldGY6cGFyYW1zOm9hdXRoOmdyYW50LXR5cGU6c2FtbDItYmVhcmVyIiwic2NvcGUiOlsic3ByaW5nLXNlY3VyaXR5LWFkYy11c2FnZSF0MTQ4NjYuUmVhZCJdLCJleHAiOjY5NzQwMzE2MDAsImlhdCI6MTU2Mzk4MjgzMCwiZW1haWwiOiJ2aWV3ZXJAdGVzdC5vcmciLCJjaWQiOiJzYi1zcHJpbmctc2VjdXJpdHktYWRjLXVzYWdlIXQxNDg2NiJ9.utoeiGdzuo4B7hXmY1sZ--1cKyoVQaL27-mNbrCO1D52wGlXGluggZo8dfwEBAjP2n9R2xToTTeQeCR136_5vbPsoF64vbif_9AQOBChuT0wVYDAOONMelIyE9YCZAPchUIr15z59AjhL1dxiQhi5s-dLpcN6fM30vL03iTZg3WN3I9Ib6x3M21in1rKa-mb3fmxiZSM3wcCkkt4viH0fAsmGpzbZUzUcXl1TALxach2wC3dJzcJLceeilerQmNGQ9jRpJELamD2avC5BIQmPRdvuW962bf-fiKKBo2YErlEJ_yKdFHMT3RecRBsK3KyKg3Du7cqCna_6471MnlVdw",
		"role": "Administrator"
	}
}
```
should return false, as the token does not contain a `Administrator` role collection. 
Use this token 
```
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImprdSI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzMxOTUvdG9rZW5fa2V5cyIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkifQ.eyJ6ZG4iOiIiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnsiY29uZmlkZW50aWFsaXR5X2xldmVsIjpbIlBVQkxJQyJdfSwidXNlcl9uYW1lIjoidmlld2VyIiwib3JpZ2luIjoidXNlcklkcCIsInhzLnN5c3RlbS5hdHRyaWJ1dGVzIjp7InhzLnJvbGVjb2xsZWN0aW9ucyI6WyJBZG1pbmlzdHJhdG9yIl19LCJleHRfYXR0ciI6eyJ6ZG4iOiIifSwiemlkIjoidWFhIiwiZ3JhbnRfdHlwZSI6InVybjppZXRmOnBhcmFtczpvYXV0aDpncmFudC10eXBlOnNhbWwyLWJlYXJlciIsInNjb3BlIjpbInNwcmluZy1zZWN1cml0eS1hZGMtdXNhZ2UhdDE0ODY2LkFkbWluIl0sImV4cCI6Njk3NDAzMTYwMCwiaWF0IjoxNTY0MDUyNjAwLCJlbWFpbCI6InZpZXdlckB0ZXN0Lm9yZyIsImNpZCI6InNiLXNwcmluZy1zZWN1cml0eS1hZGMtdXNhZ2UhdDE0ODY2In0.ZpHLmmhoPQvqQ4blWlg9KkT2Ov5mO2Sq61AbMTLZ4KG7Srm-mI-d2BBu_D7hF1xflR3BVhmeAaDGslJAhFy-7Kdg9kUZfpG8nav0-8JGUyC7aC60ygpBD9c1OBjZ3iaTkCQIAYYlFXQ9TW3cRoc5B-037fFn9adHdrep8ZfEjZfyGt6Uu0E-G09nQdeenotIHB2lT8tlEi44_cCcnAWx9z095iSzVP3GAMzUAPjCeMAdateyiSQb_DmLyG2ajpwwxXcnDxhimlGgz6zRBaVaQCWyejgkeYPeOyA7L9IB3m9JTTWMdB9Qcbh21CeNh2FcgW7PZ_bmhsLYSLTkQuSQtg
```
to simulate an admin user.

Apply also a check on scope or attribute values:
```
{
	"input": {
		"token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImprdSI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzMxOTUvdG9rZW5fa2V5cyIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkifQ.eyJ6ZG4iOiIiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnsiY29uZmlkZW50aWFsaXR5X2xldmVsIjpbIlBVQkxJQyJdfSwidXNlcl9uYW1lIjoidmlld2VyIiwib3JpZ2luIjoidXNlcklkcCIsInhzLnN5c3RlbS5hdHRyaWJ1dGVzIjp7InhzLnJvbGVjb2xsZWN0aW9ucyI6WyJBZG1pbmlzdHJhdG9yIl19LCJleHRfYXR0ciI6eyJ6ZG4iOiIifSwiemlkIjoidWFhIiwiZ3JhbnRfdHlwZSI6InVybjppZXRmOnBhcmFtczpvYXV0aDpncmFudC10eXBlOnNhbWwyLWJlYXJlciIsInNjb3BlIjpbInNwcmluZy1zZWN1cml0eS1hZGMtdXNhZ2UhdDE0ODY2LkFkbWluIl0sImV4cCI6Njk3NDAzMTYwMCwiaWF0IjoxNTY0MDUyNjAwLCJlbWFpbCI6InZpZXdlckB0ZXN0Lm9yZyIsImNpZCI6InNiLXNwcmluZy1zZWN1cml0eS1hZGMtdXNhZ2UhdDE0ODY2In0.ZpHLmmhoPQvqQ4blWlg9KkT2Ov5mO2Sq61AbMTLZ4KG7Srm-mI-d2BBu_D7hF1xflR3BVhmeAaDGslJAhFy-7Kdg9kUZfpG8nav0-8JGUyC7aC60ygpBD9c1OBjZ3iaTkCQIAYYlFXQ9TW3cRoc5B-037fFn9adHdrep8ZfEjZfyGt6Uu0E-G09nQdeenotIHB2lT8tlEi44_cCcnAWx9z095iSzVP3GAMzUAPjCeMAdateyiSQb_DmLyG2ajpwwxXcnDxhimlGgz6zRBaVaQCWyejgkeYPeOyA7L9IB3m9JTTWMdB9Qcbh21CeNh2FcgW7PZ_bmhsLYSLTkQuSQtg",
		"scope": "spring-security-adc-usage!t14866.Admin",
		"role": "Administrator",
		"attributeName": "confidentiality_level",
		"attributeValue": "PUBLIC"
	}
}
```

Find the current API documentation of OPA (Open Policy Agent) [here](https://www.openpolicyagent.org/docs/latest/rest-api/).

## [OPTIONAL] Configure the local environment
You need to configure the Url of the Authorization Decision Controller (`ADC_URL`) as part of system environment variable or via the [application-uaamock.properties](src/main/resources/application-uaamock.properties). 
You can enter the url of the previous deployed ADC service or, alternatively you can refer to a ADC service that runs locally, e.g. in a docker container.

## [OPTIONAL] Test locally
```
mvn spring-boot:run -Dspring-boot.run.profiles=cloud,uaamock
```
    
When your application is successfully started (pls check the console logs) use a Rest client such as `Postman Chrome Extension`. Then you can perform a GET request to `http://localhost:8080/v1/method` and set an `Authorization` header with the value 
```
Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImprdSI6Imh0dHA6Ly9sb2NhbGhvc3Q6MzMxOTUvdG9rZW5fa2V5cyIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkifQ.eyJ6ZG4iOiIiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnsiY29uZmlkZW50aWFsaXR5X2xldmVsIjpbIlBVQkxJQyJdfSwidXNlcl9uYW1lIjoidmlld2VyIiwib3JpZ2luIjoidXNlcklkcCIsInhzLnN5c3RlbS5hdHRyaWJ1dGVzIjp7InhzLnNhbWwuZ3JvdXBzIjpbIkcxIl0sInhzLnJvbGVjb2xsZWN0aW9ucyI6WyJWaWV3ZXIiXX0sImV4dF9hdHRyIjp7InpkbiI6IiJ9LCJ6aWQiOiJ1YWEiLCJncmFudF90eXBlIjoidXJuOmlldGY6cGFyYW1zOm9hdXRoOmdyYW50LXR5cGU6c2FtbDItYmVhcmVyIiwic2NvcGUiOlsic3ByaW5nLXNlY3VyaXR5LWFkYy11c2FnZSF0MTQ4NjYuUmVhZCJdLCJleHAiOjY5NzQwMzE2MDAsImlhdCI6MTU2Mzk4MjgzMCwiZW1haWwiOiJ2aWV3ZXJAdGVzdC5vcmciLCJjaWQiOiJzYi1zcHJpbmctc2VjdXJpdHktYWRjLXVzYWdlIXQxNDg2NiJ9.utoeiGdzuo4B7hXmY1sZ--1cKyoVQaL27-mNbrCO1D52wGlXGluggZo8dfwEBAjP2n9R2xToTTeQeCR136_5vbPsoF64vbif_9AQOBChuT0wVYDAOONMelIyE9YCZAPchUIr15z59AjhL1dxiQhi5s-dLpcN6fM30vL03iTZg3WN3I9Ib6x3M21in1rKa-mb3fmxiZSM3wcCkkt4viH0fAsmGpzbZUzUcXl1TALxach2wC3dJzcJLceeilerQmNGQ9jRpJELamD2avC5BIQmPRdvuW962bf-fiKKBo2YErlEJ_yKdFHMT3RecRBsK3KyKg3Du7cqCna_6471MnlVdw
```

Alternatively you can also debug the [TestControllerTest](src/test/java/sample.spring.adc/TestControllerTest.java) JUnit Test.  

## Cockpit administration tasks: Assign Role to your User
When accessing your application endpoints on Cloud Foundry via the Application Router, you get redirected to a login-screen to authenticate yourself. But your application will respond with error status code `403` (`unauthorized`) in case you do not have any Roles / Role Collections assigned. 
That's why you need to assign as part of your Identity Provider, e.g. SAP ID Service the deployed Role Collection(s) such as `Viewer` or `Administrator` to your user as depicted in the screenshot below and as documented [here](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/9e1bf57130ef466e8017eab298b40e5e.html).

![](../images/SAP_CP_Cockpit_AssignRoleCollectionToUser.png)

Further up-to-date information you can get on sap.help.com:
- [Maintain Role Collections](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/d5f1612d8230448bb6c02a7d9c8ac0d1.html)
- [Maintain Roles for Applications](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/7596a0bdab4649ac8a6f6721dc72db19.html).

## Access the application
After deployment, the Application Router will trigger authentication automatically when you access one of the following URLs:

* `https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/method` - GET request to execute a method secured with Spring Global Method Security. This method requires `READ` scope.

> Note: https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN> points to the url of the Application Router. Get all app routes with `cf apps`.
