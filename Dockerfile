# --- Stage 1: Build using valid Maven/Java 21 image ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Create a proper entrypoint script that handles environment variables
RUN echo '#!/bin/bash\n\n\
if [ -f /app/.env ]; then\n\
  export $(cat /app/.env | grep -v '"'"'#'"'"' | sed '"'"'s/\\r$//'"'"' | xargs)\n\
fi\n\n\
exec java -jar /app/app.jar' > /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]