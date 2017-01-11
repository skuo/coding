# coding

Build project with gradle
-----
```bash
./gradlew build
```

Run app as jar
-----
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
