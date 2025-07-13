# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the parent POM and the module POM
COPY ../pom.xml .
COPY pom.xml full_stack_v5/

# Download dependencies for the parent and module (this will be cached if no changes)
RUN mvn -B dependency:go-offline -pl full_stack_v5 -am

# Copy source code
COPY src full_stack_v5/src

# Build the application
RUN mvn -B clean package -DskipTests -pl full_stack_v5

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/full_stack_v5/target/*.jar app.jar

# Environment variables
ENV SERVER_PORT=10006
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the application port
EXPOSE 10006

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]