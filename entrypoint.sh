#!/bin/bash

# Load environment variables from .env file if it exists
if [ -f /app/.env ]; then
  echo "Found .env file, loading variables..."
  export $(cat /app/.env | grep -v '#' | sed 's/\r$//' | xargs)

  # Print loaded variables to verify (mask sensitive values)
  echo "Loaded environment variables:"
  echo "DB_URL: $DB_URL"
  echo "DB_USERNAME: $DB_USERNAME"
  echo "DB_PASSWORD: ***masked***"
  echo "ALLOWED_ORIGINS: $ALLOWED_ORIGINS"
  echo "SUPABASE_URL: $SUPABASE_URL"
  echo "SUPABASE_KEY: ***masked***"
  echo "SUPABASE_JWT_SECRET: ***masked***"
else
  echo "No .env file found!"
  exit 1
fi

# Enable debug output for Spring Boot
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=DEBUG
export SPRING_PROFILES_ACTIVE=dev

# Start the application
exec java -jar /app/app.jar