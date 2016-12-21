JVM_MEM=1700M

if [ -z "$JAVA_HOME" ]; then
  echo "Set JAVA_HOME to /data/java"
  JAVA_HOME=/data/java
  export JAVA_HOME
fi
echo "JAVA_HOME="$JAVA_HOME

echo "Starting in-auth (MEM = -server -Xms$JVM_MEM -Xmx$JVM_MEM)"
$JAVA_HOME/bin/java -server -Xms$JVM_MEM -Xmx$JVM_MEM -jar lib/jetty-runner-${jetty.version}.jar --config conf/in-auth-jetty.xml --stop-port 8181 --stop-key inAuthWebStop --path /in-auth in-auth-${project.version}.war > console.log 2>&1 &
