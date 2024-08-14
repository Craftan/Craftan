#!/bin/bash

mkdir -p /app/server/plugins
cp /app/tmp/app.jar /app/server/plugins/craftan.jar
cp -a /app/tmp/server-template/plugins/. /app/server/plugins
cd /app/server
exec java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 paper.jar