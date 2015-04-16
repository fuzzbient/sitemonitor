#!/bin/sh
#
# Control Script

JARFile="sitemonitor.jar"
PIDFile="sitemonitor.pid"
LOG="/dev/null"
ERR="./sitemonitor.err"

nohup java -Xmx1024m -Dspring.mail.host=localhost -Dsitemonitor.mail.from=sitemonitor@foo.bar -jar $JARFile --server.port=8011 >$LOG 2> $ERR & echo $! > $PIDFile

