FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG JAR_FILE=./target/*.jar

COPY ${JAR_FILE} app.jar

ENV SERVER_PORT=10006
ENV SPRING_PROFILES_ACTIVE=prod
ENV HOST_NAME=host.docker.internal
ENV SPRING_JPA_HIBERNATE_DDL-AUTO=update

EXPOSE 10006

ENTRYPOINT ["java", "-jar", "app.jar"]