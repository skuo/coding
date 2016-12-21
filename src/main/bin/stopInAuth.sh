if [ -z "$JAVA_HOME" ]; then
  echo "Set JAVA_HOME to /data/java"
  JAVA_HOME=/data/java
  export JAVA_HOME
fi
echo "JAVA_HOME="$JAVA_HOME

echo "Stopping in-auth "
$JAVA_HOME/bin/java -jar lib/start-${jetty.version}.jar -DSTOP.PORT=8181 -DSTOP.KEY=inAuthWebStop --stop
