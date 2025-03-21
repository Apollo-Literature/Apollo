# --- Stage 1: Build the Spring Boot application using Maven ---
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies.
COPY pom.xml .
# Copy the source code.
COPY src ./src

# Build the application; skipping tests.
RUN mvn clean package -DskipTests

# --- Stage 2: Create the runtime image ---
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from the previous stage.
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot listens on.
EXPOSE 8080

# Copy the entrypoint script.
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Use the entrypoint script to load local .env (for local development) if present.
ENTRYPOINT ["/app/entrypoint.sh"]
