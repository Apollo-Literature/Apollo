# Project Setup Guide

## Prerequisites
Before setting up the project, ensure you have the following installed:
- [Git](https://git-scm.com/downloads)
- [Java JDK (Version 8 or later)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [PostgreSQL Database](https://www.postgresql.org/download/)
- [IntelliJ IDEA or Eclipse](https://www.jetbrains.com/idea/download/)

---

## Step 1: Clone the Repository
To get started, clone the project repository from GitHub:
```sh
git clone <repository-url>
cd <repository-folder>
```

---

## Step 2: Load Maven Dependencies
Navigate to the project directory and use Maven to download the required dependencies:
```sh
mvn clean install
```
If using an IDE such as IntelliJ IDEA or Eclipse, ensure Maven is enabled and dependencies are loaded automatically.

**For IntelliJ IDEA users:**
- Open the project.
- Navigate to the `Maven` tool window.
- Click `Load Maven Projects` to ensure all dependencies are properly downloaded.

---

## Step 3: Database Setup
Set up the database by following these steps:
1. Open your PostgreSQL database client.
2. Create a new database named `apollo`:
   ```sql
   CREATE DATABASE apollo;
   ```
3. Ensure that you have a user with the necessary permissions to access the database.

---

## Step 4: Environment Configuration
The application requires a `.env` file in the root directory to configure the database credentials. Follow these steps:

1. In the root directory of the project, create a new file named `.env`.
2. Add the following details with your database credentials:
   ```env
   DB_USERNAME=yourUsername
   DB_PASSWORD=yourPassword
   ```

**Note:** Ensure this file is not committed to version control by adding `.env` to `.gitignore`.

---

## Step 5: Run the Application
Once the setup is complete, start the application using:
```sh
mvn spring-boot:run
```
Or, if using an IDE, run the `main` method in the application’s entry point class.

---

## Step 6: Verify Setup
To confirm everything is working correctly:
- Check logs for successful database connections.
- Open `http://localhost:8080` in your browser (or the configured port) to see the application running.

---

## Troubleshooting
- **Maven dependencies not loading?** Run `mvn clean install -U`.
- **Database connection errors?** Double-check the `.env` file and ensure the database service is running.
- **Port conflicts?** Modify `application.properties` or `.env` to specify a different server port.

---

## Additional Notes
- For production deployments, configure security settings and environment variables properly.
- Regularly update dependencies by running `mvn versions:display-dependency-updates`.
- Follow best practices for storing sensitive information securely.

For further assistance, refer to the project documentation or reach out to the development team.

