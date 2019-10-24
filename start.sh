#!/bin/bash
php /var/www/html/files/dumbuth/private/setup.php
service apache2 start
exec java -jar /home/server.jar