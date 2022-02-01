FROM openjdk:8-jdk-alpine
COPY target/ovcharenko-0.0.1-SNAPSHOT.jar ovcharenko-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","ovcharenko-0.0.1-SNAPSHOT.jar"]