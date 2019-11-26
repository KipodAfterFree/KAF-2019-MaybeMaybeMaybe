#!/bin/bash
service apache2 start
exec su nopermissions -c "exec java -jar /home/server.jar"