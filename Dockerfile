FROM php:7.3-apache
# Update package lists
RUN apt-get update
RUN mkdir -p /usr/share/man/man1
# Install java
RUN apt-get -y install openjdk-8-jre-headless ca-certificates-java --no-install-recommends --no-install-suggests
ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
# Install gnupg & php-gnupg
RUN apt-get -y install gnupg php-gnupg
# Copy WebApp to /var/www/html
COPY web /var/www/html
# Change ownership of /var/www
RUN chown www-data /var/www/ -R
# Change permissions of /var/www
RUN chmod 775 /var/www/ -R
# Copy server.jar to /home/
COPY java/out/server.jar /home/server.jar
# Expose server port
EXPOSE 9837
# Copy startup script
COPY start.sh /start.sh
RUN chmod 777 /start.sh
# Enable mods
RUN a2enmod headers
# Restart webserver
RUN service apache2 restart
# Startup command
CMD "/start.sh"