#!/bin/sh
# Load local .env file if it exists
if [ -f .env ]; then
  echo "Loading environment variables from .env file"
  export $(grep -v '^#' .env | xargs)
fi

# Start the Spring Boot application.
exec java -jar app.jar