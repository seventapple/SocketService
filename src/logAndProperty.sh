#!/bin/sh 
#remeber encode set UNIX
EXEC_DIR=$(cd `dirname $0`;pwd)
JAVA_HOME=/home/wang/jre/

CLASS_PATH=$EXEC_DIR/lib/logAndProperty.jar
CLASS_PATH=$CLASS_PATH:$EXEC_DIR/lib/logback-class-1.1.11.jar
CLASS_PATH=$CLASS_PATH:$EXEC_DIR/lib/logback-core-1.1.11.jar
CLASS_PATH=$CLASS_PATH:$EXEC_DIR/lib/slf4j-api-1.7.25.jar
CLASS_PATH=$CLASS_PATH:$EXEC_DIR/lib/jackson-annotations-2.9.9.jar
CLASS_PATH=$CLASS_PATH:$EXEC_DIR/lib/jackson-core-2.9.9.jar
CLASS_PATH=$CLASS_PATH:$EXEC_DIR/lib/jackson-databind-2.9.9.jar

$JAVA_HOME/bin/java -classpath ${CLASS_PATH} com.wang.startup.StartUp "$@"