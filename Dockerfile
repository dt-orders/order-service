FROM openjdk:11
COPY target/*.jar .
COPY target/MANIFEST .

# used to set the problem pattern
ARG APP_VERSION=1
ENV APP_VERSION=$APP_VERSION

# during development can set to true so that you dont
# need to have the customer and catalog service running
ARG DEV_MODE=false
ENV DEV_MODE=$DEV_MODE

EXPOSE 8080

CMD ["sh", "-c", "/usr/local/openjdk-11/bin/java -Xmx400m -Xms400m -jar *.jar"]
