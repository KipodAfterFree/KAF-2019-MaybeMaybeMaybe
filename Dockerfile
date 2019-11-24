FROM php:7.0-apache
# Update package lists
RUN apt-get update
RUN mkdir -p /usr/share/man/man1
# Install java
RUN apt-get -y install openjdk-8-jre-headless ca-certificates-java --no-install-recommends --no-install-suggests
ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
# Copy flag to /flag.txt
COPY flag.txt /flag.txt
# Copy WebApp to /var/www/html
COPY web /var/www/html
# Permission groups (Hardening)
RUN useradd nopermissions
RUN useradd -g www-data permissions
RUN chown permissions:www-data /var/www/html -R
RUN chmod 755 /var/www/html -R
RUN chmod 777 /var/www/html/files/mmm/keys -R
RUN chmod 777 /var/www/html/upload -R
RUN chmod 777 /var/www/html/files/authenticate/ids
RUN chmod 777 /var/www/html/files/authenticate/users
RUN chmod 777 /var/www/html/files/authenticate/sessions.json
# Copy server.jar to /home/
COPY java/build/libs/MMMServer.jar /home/server.jar
# Expose server port
EXPOSE 8000
# Copy startup script
COPY start.sh /start.sh
RUN chmod 700 /start.sh
# Enable mods
RUN a2enmod headers
# Restart webserver
RUN service apache2 restart
# Startup command
CMD "/start.sh"