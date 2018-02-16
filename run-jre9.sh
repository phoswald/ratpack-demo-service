#!/bin/bash
export MY_CALLER="Started from run.sh"
CLASSPATH="target/ratpack-demo-service.jar:target/lib/*"
MAINCLASS=com.github.phoswald.sample.Application
java -cp $CLASSPATH -Dmy.caller="Started from run.sh" --illegal-access=warn --add-modules java.activation $MAINCLASS "$@"
