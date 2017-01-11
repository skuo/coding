Coding
===

Build project with gradle
-----
```bash
./gradlew build
```

# debug app as jar
```bash
java -server -Xms1700M -Xmx1700M -Xdebug -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=y -jar build/libs/coding-0.1.0.jar --spring.profiles.active=dev > console.log 2>&1 &
```

#Run app as jar
```bash
java -jar ./build/libs/coding-{VERSION_NUMBER}.jar --spring.profiles.active=dev
```
example:
```bash
java -jar ./build/libs/coding-0.0.1.jar --spring.profiles.active=dev 
or
SPRING_APPLICATION_JSON='{"spring":{"profiles":{"active":"dev"}}}' java -jar ./build/libs/royalty-cargo-1.0.1-SNAPSHOT.jar

# spring.profiles.active=secret and /data/application-secret.properties exist. 
or # pass in env or property
ENV="int" java -DENV -Dprop.env=prop.int -jar ./build/libs/coding-1.0.1.jar --spring.config.location=/data/ 
```
Test running server:
curl  -H "Accept: application/json" -u user:CodingBreak -X GET localhost:8080/coding/version

Override individual properties at run time:
```bash
java -jar ./build/libs/coding-0.0.1.jar --spring.profiles.active=dev --spring.datasource.username= otherusername --spring.datasource.password= otherpassword --server.port=8888 --spring.config.location=location for override properties file
```

Shutdown
-----
curl -X POST -u user:CodingBreak localhost:8080/coding/shutdown

Fabric
-----------
fab build_and_debug # debug port at 4000, tomcat listens at 8080

Swagger-SpringMvc
-----------
http://localhost:8090/swagger-ui.html


Built in Spring Boot Endpoints
-------------------------------
```bash
http://localhost:8080/coding/health

http://localhost:8080/coding/actuator
http://localhost:8080/coding/autoconfig
http://localhost:8080/coding/beans
http://localhost:8080/coding/configprops
http://localhost:8080/coding/env
http://localhost:8080/coding/info     # display build info
http://localhost:8080/coding/metrics
http://localhost:8080/coding/mappings
http://localhost:8080/coding/shutdown # not enabled by default
http://localhost:8080/coding/trace
```
