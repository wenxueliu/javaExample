#!/bin/bash

# Set paths
FL_HOME=`dirname $0`
FL_JAR="${FL_HOME}/target/learnJava.jar"
FL_LOGBACK="${FL_HOME}/etc/logback.xml"

#DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,address=1200,server=y,suspend=n"
DEBUG_OPTS=""
# Set JVM options
JVM_OPTS=""
#JVM_OPTS="$JVM_OPTS -agentpath:/home/wenxueliu/Documents/package/lightweight-java-profiler/trunk/build-64/liblagent.so"
JVM_OPTS="$JVM_OPTS -server -d64"
JVM_OPTS="$JVM_OPTS -Xmx500m -Xms100m -Xmn800m"
JVM_OPTS="$JVM_OPTS -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"
JVM_OPTS="$JVM_OPTS -XX:MaxInlineSize=8192 -XX:FreqInlineSize=8192"
JVM_OPTS="$JVM_OPTS -XX:CompileThreshold=1500" # "-XX:PreBlockSpin=8"
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="$JVM_OPTS -Dpython.security.respectJavaAccessibility=false"
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote
    -Dcom.sun.management.jmxremote.port=1100
    -Dcom.sun.management.jmxremote.authenticate=false
    -Dcom.sun.management.jmxremote.ssl=false
    -Djava.rmi.server.hostname=127.0.0.1"
JVM_OPTS="$JVM_OPTS -XX:+UnlockCommercialFeatures"
JVM_OPTS="$JVM_OPTS -XX:+FlightRecorder"

# Create a logback file if required
[ -f ${FL_LOGBACK} ] || cat <<EOF_LOGBACK >${FL_LOGBACK}
<configuration scan="true">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%level [%logger:%thread] %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
    <logger name="org" level="ALL"/>
    <logger name="LogService" level=“INFO”/> <!-- Restlet access logging -->
    <logger name="net.floodlightcontroller" level="DEBUG"/>
    <logger name="net.floodlightcontroller.logging" level="INFO"/>
    <logger name="net.floodlightcontroller.cluster" level="TRACE"/>
</configuration>
EOF_LOGBACK

echo "Starting controller ..."
java ${DEBUG_OPTS} ${JVM_OPTS} -Dlogback.configurationFile=${FL_LOGBACK} -jar ${FL_JAR}
