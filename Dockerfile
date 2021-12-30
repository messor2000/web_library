FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/web_library.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]