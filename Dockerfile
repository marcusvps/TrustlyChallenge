FROM openjdk:8-jdk-alpine as build
ARG JAR_FILE=build/challenge-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]


