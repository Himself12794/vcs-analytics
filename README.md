# INTRODUCTION
__seed-api-sb__ is a repo for Spring Boot "seed" RESTful API development with all the best practices implemented in.
<br/>
# USAGE
Fork this repository.
Follow these steps before you start building your APIs.

1. __Edit pom.xml__<br/>
Edit the following tags in your pom.xml: <br/>

	```xml
	<groupId>your-group-id</groupId>
	<artifactId>your-artifact-id</artifactId>
	<version>1.0-SNAPSHOT</version>
	<name>name-of-your-app</name>
	<description>description-of-your-app</description>
	```

2. __Edit src/main/resource/logback.xml__<br/>
Edit the value of following property in your logback.xml: <br/>
    
    ```xml
    <property name="APP_NAME" value="name-of-your-app_"/>
    ```
    
3. __Edit src/name/resource/application.xml and src/test/resource/application.xml__<br/>
Edit the values of different application properties and custom configurations (like server.port, DB details etc.) <br/>