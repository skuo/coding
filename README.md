#Build project with gradle
```bash
./gradlew build
```

##Automatic Reloading of Changed Classes 
```bash
# in a terminal
./gradlew build --continuous

# in a second terminal
./gradlew bootRun # not in debug mode
./gradlew bootRun --debug-jvm # debug port is 5005
```

##Debug app as jar
```bash
java -server -Xms1700M -Xmx1700M -Xdebug -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=y -jar build/libs/coding-0.1.0.jar --spring.profiles.active=dev > console.log 2>&1 &
```

##Run app as jar
```bash
java -jar ./build/libs/coding-{VERSION_NUMBER}.jar --spring.profiles.active=dev -Dspring.cloud.config.uri=http://localhost:8888
```
example:
```bash
java -jar ./build/libs/coding-0.0.1.jar --spring.profiles.active=dev 
or
SPRING_APPLICATION_JSON='{"spring":{"profiles":{"active":"dev"}}}' java -jar ./build/libs/coding-1.0.1-SNAPSHOT.jar

spring.profiles.active=secret and /data/application-secret.properties exist.
--- 
or # pass in env or property
ENV="int" java -DENV -Dprop.env=prop.int -jar ./build/libs/coding-1.0.1.jar --spring.config.location=/data/ 
```
Test running server:
curl  -H "Accept: application/json" -u user:CodingBreak -X GET localhost:8080/coding/version

Override individual properties at run time:
```bash
java -jar ./build/libs/coding-0.0.1.jar --spring.profiles.active=dev --spring.datasource.username= otherusername --spring.datasource.password= otherpassword --server.port=8888 --spring.config.location=location for override properties file
```

- - - -

#Build Project and Docker Image
```bash
./gradlew buildDocker \[-PdockerGroup={Optional Group}] [-PdockerTag={Optional tag}]
```

##Run app with docker
```bash
docker run -p:{LOCAL_HOST_PORT_TO_MAP}:8080 [-v {OPTIONAL_VOLUME_TO_MOUNT}:/data] -t {IMAGE_NAME} --spring.profiles.active=dev [--spring.config.location={PATH_TO_OVERRIDE_PROPERTIES_FILES}]
```
example:
```bash
# server.port property determine the port tomcat listening to
docker run -p:8080:8080 -v /data:/data -t coding --spring.profiles.active=dev
# if /data/application-secret.properties exist
# use -v /Users/skuo/.aws:/root/.aws for local docker run
docker run -p:8080:8080 -v /data:/data -v /Users/skuo/.aws:/root/.aws -t -e "S3_SECRETS_BUCKET=coding-private" -e "S3_SECRETS_KEY=coding/int/application-int.properties" --rm coding
```
Test running server:
curl  -H "Accept: application/json" -H "X-Auth-Token: {TOKEN}" -X GET localhost:8080/

curl -X GET -u user:CodingBreak localhost:8080/coding/version

```bash
// repository = "${project.group}/${applicationName}"
// tag = "${project.group}/${applicationName}:${tagVersion}"
docker images
REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
com.coding/coding            1.0.1               23b9f322f7ac        4 seconds ago       265.5 MB
coding                       latest              9c02aa4b910a        10 minutes ago      265.5 MB
```

#Shutdown
```bash
curl -X POST -u user:CodingBreak localhost:8080/coding/shutdown
```

#Fabric
```bash
fab build_and_debug # debug port at 4000, tomcat listens at 8080
```

#Swagger UI
```bash
http://localhost:8080/coding/swagger-ui.html
```

- - - -

#Built in Spring Boot Endpoints
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

- - - -

# Docker Installation and Useful Commands
Installation
```bash
* Click on "Get Docker for Mac [stable]" link on https://docs.docker.com/docker-for-mac/
* Install the Docker application.  The Docker whale icon will show up on menu bar.
* Open it up and start Docker.
```

Useful Commands
```bach
# docker images
$> docker images

# remove a docker image
$> docker rmi {IMAGE ID}

# docker processes (containers)
$> docker ps -a

# remove a docker container
$> docker rm {CONTAINER ID}

# tail a docker container's logfile
$> docker logs -f {CONTAINER ID}

# connect to a docker container and run sh
$> docker exec -it 377a08827a80 sh
```

- - - -

#Kubernetes

## Installation on Mac
Please reference https://deis.com/blog/2015/zero-to-kubernetes-dev-environment-on-os-x/
  * installed iterm2
  * installed corectl
  * installed kube-solo
  * brew install libev
    + sudo chown -R `whoami`:admin /usr/local/opt
    + brew link libev
  * To downgrade to the version when the docker client installed by Docker for Mac is newer than the Docker server on CoreOS VM, do the following:
    + DOCKER_API_VERSION=1.23 docker images
  * Another help page https://deis.com/blog/2016/run-kubernetes-on-a-mac-with-kube-solo/
  * DOCKER related environment variables iTerm2
    + # set by kube-solo during start up
    + DOCKER_HOST=tcp://192.168.64.2:2375 
    + DOCKER_TLS_VERIFY=
    + DOCKER_CERT_PATH=
    + # downgrade client version
    + export DOCKER_API_VERSION="1.23" 

##DOCKER related environment variables iTerm2
```bash
# set by kube-solo during start up
DOCKER_HOST=tcp://192.168.64.2:2375 
DOCKER_TLS_VERIFY=
DOCKER_CERT_PATH=
# downgrade client version
export DOCKER_API_VERSION="1.23" 
```

##build and use application-dev properties to CoreOS VM
```bash
./gradlew clean build buildDocker
```

### Start coding in a docker container
```bash
docker run -p:8080:8080 -t --rm coding
docker run -p:9898:8080 -t --rm coding # if 8080 is taken like it is the case for kubernetes dashboard
docker run -p:9898:8080 -t --rm coding:0.0.1 # Use image with tag=0.0.1 instead of latest

Test the running docker container on CoreOS VM (first ssh k8solo-01)
curl -H "Accept: application/json" -X GET -u user:CodingBreak 192.168.64.2:9898/coding/version
curl -H "Accept: application/json" -X GET -u user:CodingBreak localhost:9898/coding/version  # only work on CoreOS VM

##Swagger UI
http://192.168.64.2:9898/coding/swagger-ui.html
```

##Start coding in kubernetes
```bash
kubectl create -f coding-deploy.yaml # port 9090:8080

kubectl create -f coding-service.yaml

Test the k8s service from the internet
curl -H "Accept: application/json" -X GET -u user:CodingBreak 192.168.64.2:31625/coding/hola # NodePort

Test the k8s service on CoreOS VM (first ssh k8solo-01)
curl -H "Accept: application/json" -X GET -u user:CodingBreak 192.168.64.2:31625/coding/hola # NodePort
curl -H "Accept: application/json" -X GET -u user:CodingBreak 10.100.106.253:9999/coding/hola

##Swagger UI
http://192.168.64.2:31625/coding/swagger-ui.html
```

Useful commands
```bash
# get currently running pods
kubectl get po -o wide

# attach a pod
kubectl attach -it coding-app-2947216720-r9tw9

# log file for a pod
kubectl logs -f coding-app-2947216720-r9tw9

# delete deployment to delete its pods
kubectl get deployment -o wide
kubectl delete deployment coding-app

# delete service
kubectl delete service coding-app

# scale replicas
kubectl scale deployment coding-app --replicas=2

# deploy a new image
kubectl set image deployment/coding-app coding-app=coding:0.0.2

# edit
kubectl edit deployment coding-app
```
