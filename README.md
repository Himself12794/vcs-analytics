# INTRODUCTION
__seed-api-sb__ is a repo for Spring Boot "seed" RESTful API development with all the best practices implemented in.
<br/>
<br/>
# USAGE
Fork this repository.
Follow these steps before you start building your APIs.

1. Edit __pom.xml__<br/>
Edit the following tags in your pom.xml: <br/>

	```xml
	<groupId>your-group-id</groupId>
	<artifactId>your-artifact-id</artifactId>
	<version>1.0-SNAPSHOT</version>
	<name>name-of-your-app</name>
	<description>description-of-your-app</description>
	```
<br/>
2. Edit __src/main/resource/logback.xml__<br/>
Edit the value of following property in your logback.xml: <br/>
    
    ```xml
    <property name="APP_NAME" value="name-of-your-app"/>
    ```
<br/>
3. Edit __src/main/resource/application.yml__ and __src/test/resource/application.yml__<br/>
Edit the values of different application properties and custom configurations (like server.port, DB details etc.) <br/>
<br/>
4. View various metrics generated for your APIs on a graphite server by entering following graphite server configuration in __src/main/resource/application.yml__<br/>

    ```xml
	graphite:
      graphiteReportingEnabled: true
      consoleReportingEnabled: false
      reportRate: 100
      prefix: <prefix to be appended before APIs>
      host: <graphite server host>
      port: <graphite server port>
	```
	<br/>
	In order to generate metrics for any API in the app, use the following annotation (refer sample APIs in seed app):<br/>
	```
	@Timed(name = "<API name>", absolute = true)
	```