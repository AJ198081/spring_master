FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG JAR_FILE=./target/*.jar
ARG APP_NAME=sbfsv6

COPY ${JAR_FILE} ${APP_NAME}.jar

ENV SERVER_PORT=11000
ENV SPRING_PROFILES_ACTIVE=prod
ENV HOST_NAME=host.docker.internal
ENV SPRING_JPA_HIBERNATE_DDL-AUTO=verify

EXPOSE 11000

ENTRYPOINT ["java", "-jar", "app.jar"]