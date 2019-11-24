#!/bin/bash
service apache2 start
su nopermissions -c "exec java -jar /home/server.jar"