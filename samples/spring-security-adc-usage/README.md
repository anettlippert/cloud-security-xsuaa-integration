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
mvn clean package
```

## Create the XSUAA Service Instance
Use the [xs-security.json](./xs-security.json) to define the authentication settings and create a service instance
```shell
cf create-service xsuaa application xsuaa-adc -c xs-security.json
```

## Configuration the manifest
The [vars](../vars.yml) contains hosts and paths that you might need to adopt.

## Deploy the application
Deploy the application using cf push. It will expect 1 GB of free memory quota.

```shell
cf push --vars-file ../vars.yml
```

## Access Authorization Decision Controller via OPA endpoints
* `https://adc-service-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/policies`
* `https://adc-service-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/data`
* `https://adc-service-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/data/rbac/allow` POST request with Content-Type: application/json and payload:
```
{
	"input": {
		"token": "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYWx0ZXN0LmF1dGhlbnRpY2F0aW9uLnNhcC5oYW5hLm9uZGVtYW5kLmNvbS90b2tlbl9rZXlzIiwia2lkIjoia2V5LWlkLTEiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIyNTMxZDdmZjc4OTM0YzkyOTI4Y2M1OWNmOTgzZjBkNSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJ6ZG4iOiJhbHRlc3QifSwieHMuc3lzdGVtLmF0dHJpYnV0ZXMiOnsieHMuc2FtbC5ncm91cHMiOlsiRzEiXSwieHMucm9sZWNvbGxlY3Rpb25zIjpbInRlc3RfY29sbGVjdGlvbiJdfSwiZ2l2ZW5fbmFtZSI6ImQwNDQyODgiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnt9LCJmYW1pbHlfbmFtZSI6InVua25vd24ub3JnIiwic3ViIjoiYzAyYjkzYzMtY2MyYi00YzBlLWE1MmEtMGFjMjM4NDM5MTViIiwic2NvcGUiOlsiYnVsbGV0aW5ib2FyZC1kMDQ0Mjg4IXQzMTc4LlVwZGF0ZSIsIm9wZW5pZCIsImJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OC5EaXNwbGF5Il0sImNsaWVudF9pZCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImNpZCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImF6cCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImdyYW50X3R5cGUiOiJ1cm46aWV0ZjpwYXJhbXM6b2F1dGg6Z3JhbnQtdHlwZTpzYW1sMi1iZWFyZXIiLCJ1c2VyX2lkIjoiYzAyYjkzYzMtY2MyYi00YzBlLWE1MmEtMGFjMjM4NDM5MTViIiwib3JpZ2luIjoieHN1YWEtbW9uaXRvcmluZy1pZHAiLCJ1c2VyX25hbWUiOiJkMDQ0Mjg4IiwiZW1haWwiOiJkMDQ0Mjg4QHVua25vd24ub3JnIiwicmV2X3NpZyI6ImQ1MWRhYmJiIiwiaWF0IjoxNTUwMTM1NTc5LCJleHAiOjE1NTAxNzg3NzksImlzcyI6Imh0dHA6Ly9hbHRlc3QubG9jYWxob3N0OjgwODAvdWFhL29hdXRoL3Rva2VuIiwiemlkIjoiNjUzMTNmNTEtN2FlNS00NWY5LTlmYmQtZGYyMjk2ODk1NWFkIiwiYXVkIjpbXX0.gY5I0dbD-xV9iOra0MVQkXH1qk1x9LwLWBZuWBeUbQOvgzs-iHFW3SOrbS-RNWCkKjpNrcJAMWFqpNxVcsKEnr5Ru6kKGbtJEAm_OMS_AVWLAIjsTmNnuyNjdn9axMxzbIq0Y_4Jn-ZYavdL1r6fzDt5FPksYgb8bghz6S04fOEyCt2X7uttLlJuEBs8GJXSC7aoamgc2iKbyTdyzToHlHFFeUgGXV3svkpuZ_-OtS2LdC9zxVjxQrXy8Ml0Gjf1dKREvumob9v8GGXYixLeAeU7kU-YjYrXZ1E49U2M1BuGy9axVXg3gH1m1CFVNjIFmE8hdD-Cdlo2z_ni8FKrdg",
		"role": "Viewer"
	}
}
```
should return true, whereas this payloads:
```
{
	"input": {
		"token": "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYWx0ZXN0LmF1dGhlbnRpY2F0aW9uLnNhcC5oYW5hLm9uZGVtYW5kLmNvbS90b2tlbl9rZXlzIiwia2lkIjoia2V5LWlkLTEiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIyNTMxZDdmZjc4OTM0YzkyOTI4Y2M1OWNmOTgzZjBkNSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJ6ZG4iOiJhbHRlc3QifSwieHMuc3lzdGVtLmF0dHJpYnV0ZXMiOnsieHMuc2FtbC5ncm91cHMiOlsiRzEiXSwieHMucm9sZWNvbGxlY3Rpb25zIjpbInRlc3RfY29sbGVjdGlvbiJdfSwiZ2l2ZW5fbmFtZSI6ImQwNDQyODgiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnt9LCJmYW1pbHlfbmFtZSI6InVua25vd24ub3JnIiwic3ViIjoiYzAyYjkzYzMtY2MyYi00YzBlLWE1MmEtMGFjMjM4NDM5MTViIiwic2NvcGUiOlsiYnVsbGV0aW5ib2FyZC1kMDQ0Mjg4IXQzMTc4LlVwZGF0ZSIsIm9wZW5pZCIsImJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OC5EaXNwbGF5Il0sImNsaWVudF9pZCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImNpZCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImF6cCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImdyYW50X3R5cGUiOiJ1cm46aWV0ZjpwYXJhbXM6b2F1dGg6Z3JhbnQtdHlwZTpzYW1sMi1iZWFyZXIiLCJ1c2VyX2lkIjoiYzAyYjkzYzMtY2MyYi00YzBlLWE1MmEtMGFjMjM4NDM5MTViIiwib3JpZ2luIjoieHN1YWEtbW9uaXRvcmluZy1pZHAiLCJ1c2VyX25hbWUiOiJkMDQ0Mjg4IiwiZW1haWwiOiJkMDQ0Mjg4QHVua25vd24ub3JnIiwicmV2X3NpZyI6ImQ1MWRhYmJiIiwiaWF0IjoxNTUwMTM1NTc5LCJleHAiOjE1NTAxNzg3NzksImlzcyI6Imh0dHA6Ly9hbHRlc3QubG9jYWxob3N0OjgwODAvdWFhL29hdXRoL3Rva2VuIiwiemlkIjoiNjUzMTNmNTEtN2FlNS00NWY5LTlmYmQtZGYyMjk2ODk1NWFkIiwiYXVkIjpbXX0.gY5I0dbD-xV9iOra0MVQkXH1qk1x9LwLWBZuWBeUbQOvgzs-iHFW3SOrbS-RNWCkKjpNrcJAMWFqpNxVcsKEnr5Ru6kKGbtJEAm_OMS_AVWLAIjsTmNnuyNjdn9axMxzbIq0Y_4Jn-ZYavdL1r6fzDt5FPksYgb8bghz6S04fOEyCt2X7uttLlJuEBs8GJXSC7aoamgc2iKbyTdyzToHlHFFeUgGXV3svkpuZ_-OtS2LdC9zxVjxQrXy8Ml0Gjf1dKREvumob9v8GGXYixLeAeU7kU-YjYrXZ1E49U2M1BuGy9axVXg3gH1m1CFVNjIFmE8hdD-Cdlo2z_ni8FKrdg",
		"role": "Advertiser"
	}
}
```
should return false.

Find the current API documentation of OPA (Open Policy Agent) [here](https://www.openpolicyagent.org/docs/latest/rest-api/).

## Test locally
```
source localhost.sh
mvn spring-boot:run
```

"xs.system.attributes": {
        "xs.saml.groups": [
            "G1"
        ],
        "xs.rolecollections": [
            "test_collection"
        ]
},
    
`http://localhost:8080/v1/sayHello?Authorization=Bearer eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vYWx0ZXN0LmF1dGhlbnRpY2F0aW9uLnNhcC5oYW5hLm9uZGVtYW5kLmNvbS90b2tlbl9rZXlzIiwia2lkIjoia2V5LWlkLTEiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIyNTMxZDdmZjc4OTM0YzkyOTI4Y2M1OWNmOTgzZjBkNSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJ6ZG4iOiJhbHRlc3QifSwieHMuc3lzdGVtLmF0dHJpYnV0ZXMiOnsieHMuc2FtbC5ncm91cHMiOlsiRzEiXSwieHMucm9sZWNvbGxlY3Rpb25zIjpbInRlc3RfY29sbGVjdGlvbiJdfSwiZ2l2ZW5fbmFtZSI6ImQwNDQyODgiLCJ4cy51c2VyLmF0dHJpYnV0ZXMiOnt9LCJmYW1pbHlfbmFtZSI6InVua25vd24ub3JnIiwic3ViIjoiYzAyYjkzYzMtY2MyYi00YzBlLWE1MmEtMGFjMjM4NDM5MTViIiwic2NvcGUiOlsiYnVsbGV0aW5ib2FyZC1kMDQ0Mjg4IXQzMTc4LlVwZGF0ZSIsIm9wZW5pZCIsImJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OC5EaXNwbGF5Il0sImNsaWVudF9pZCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImNpZCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImF6cCI6InNiLWJ1bGxldGluYm9hcmQtZDA0NDI4OCF0MzE3OCIsImdyYW50X3R5cGUiOiJ1cm46aWV0ZjpwYXJhbXM6b2F1dGg6Z3JhbnQtdHlwZTpzYW1sMi1iZWFyZXIiLCJ1c2VyX2lkIjoiYzAyYjkzYzMtY2MyYi00YzBlLWE1MmEtMGFjMjM4NDM5MTViIiwib3JpZ2luIjoieHN1YWEtbW9uaXRvcmluZy1pZHAiLCJ1c2VyX25hbWUiOiJkMDQ0Mjg4IiwiZW1haWwiOiJkMDQ0Mjg4QHVua25vd24ub3JnIiwicmV2X3NpZyI6ImQ1MWRhYmJiIiwiaWF0IjoxNTUwMTM1NTc5LCJleHAiOjE1NTAxNzg3NzksImlzcyI6Imh0dHA6Ly9hbHRlc3QubG9jYWxob3N0OjgwODAvdWFhL29hdXRoL3Rva2VuIiwiemlkIjoiNjUzMTNmNTEtN2FlNS00NWY5LTlmYmQtZGYyMjk2ODk1NWFkIiwiYXVkIjpbXX0.gY5I0dbD-xV9iOra0MVQkXH1qk1x9LwLWBZuWBeUbQOvgzs-iHFW3SOrbS-RNWCkKjpNrcJAMWFqpNxVcsKEnr5Ru6kKGbtJEAm_OMS_AVWLAIjsTmNnuyNjdn9axMxzbIq0Y_4Jn-ZYavdL1r6fzDt5FPksYgb8bghz6S04fOEyCt2X7uttLlJuEBs8GJXSC7aoamgc2iKbyTdyzToHlHFFeUgGXV3svkpuZ_-OtS2LdC9zxVjxQrXy8Ml0Gjf1dKREvumob9v8GGXYixLeAeU7kU-YjYrXZ1E49U2M1BuGy9axVXg3gH1m1CFVNjIFmE8hdD-Cdlo2z_ni8FKrdg` 

## Cockpit administration tasks: Assign Role to your User
Finally, as part of your Identity Provider, e.g. SAP ID Service, assign the deployed Role Collection(s) such as `Viewer` or `Administrator` to your user as depicted in the screenshot below and as documented [here](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/9e1bf57130ef466e8017eab298b40e5e.html).

![](../images/SAP_CP_Cockpit_AssignRoleCollectionToUser.png)

Further up-to-date information you can get on sap.help.com:
- [Maintain Role Collections](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/d5f1612d8230448bb6c02a7d9c8ac0d1.html)
- [Maintain Roles for Applications](https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/7596a0bdab4649ac8a6f6721dc72db19.html).

## Access the application
After deployment, the AppRouter will trigger authentication automatically when you access one of the following URLs:

* `https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/sayHello` - GET request that provides XSUAA user token details, but only if token matches.
* `https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/method` - GET request to executes a method secured with Spring Global Method Security.
* `https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN>/v1/readData` - GET request to read sensitive data via Global Method Security.
* `https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN>/v2/sayHello` - GET request that provides generic Jwt info, but only if token matches.

> Note: https://spring-security-adc-usage-web-<ID>.<LANDSCAPE_APPS_DOMAIN> points to the url of the AppRouter. Get all app routes with `cf apps`.
