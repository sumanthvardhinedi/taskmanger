# Team Task Manager

A full-stack Team Task Management web application built with Spring Boot, Spring MVC, Spring Security, Spring Data JPA, MySQL, and a static HTML/CSS/JavaScript frontend served by the backend.

Users can sign up, log in, create projects, manage project members, assign tasks, update task status, and view dashboard metrics.

## Features

- User signup and login
- Session-based authentication with BCrypt password hashing
- Project creation
- Project roles: `ADMIN` and `MEMBER`
- Admin member management
- Task creation with title, description, due date, priority, assignee, and status
- Task statuses: `TODO`, `IN_PROGRESS`, `DONE`
- Dashboard metrics:
  - Total tasks
  - Tasks by status
  - Tasks per user
  - Overdue tasks
- Role-based access:
  - Admins can manage members and tasks
  - Members can view and update only assigned tasks

## Tech Stack

- Java 17
- Spring Boot 4
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- HTML, CSS, JavaScript
- Maven

## Project Structure

```text
src/main/java/dev/sumanth/taskmanager
+-- config        # Spring Security and app configuration
+-- controller    # REST controllers
+-- dao           # Spring Data JPA repositories
+-- dto           # Request and response DTOs
+-- entity        # JPA entities
+-- enums         # App enums
+-- exception     # API exception handling
+-- security      # Current user helper
+-- service       # Business logic

src/main/resources/static
+-- index.html
+-- styles.css
+-- app.js
```

## Prerequisites

- Java 17
- MySQL Server
- Maven is optional because the project includes Maven Wrapper

Check Java:

```bash
java -version
```

Check MySQL service on Windows:

```cmd
sc query MySQL80
```

The state should be:

```text
RUNNING
```

## Local Database Setup

Create a MySQL database:

```sql
CREATE DATABASE taskmanager;
```

The app can also create the database automatically if your JDBC URL includes `createDatabaseIfNotExist=true`.

Recommended local `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

server.port=8080
```

Replace `root123` with your MySQL password.

The current deployment-friendly config uses environment variables:

```properties
spring.datasource.url=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}
spring.datasource.username=${MYSQLUSER}
spring.datasource.password=${MYSQLPASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

server.port=${PORT:8080}
```

For local running with this config, set environment variables first.

PowerShell:

```powershell
$env:MYSQLHOST="localhost"
$env:MYSQLPORT="3306"
$env:MYSQLDATABASE="taskmanager"
$env:MYSQLUSER="root"
$env:MYSQLPASSWORD="root123"
```

Command Prompt:

```cmd
set MYSQLHOST=localhost
set MYSQLPORT=3306
set MYSQLDATABASE=taskmanager
set MYSQLUSER=root
set MYSQLPASSWORD=root123
```

## Run Locally

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows:

```cmd
mvnw.cmd spring-boot:run
```

Open the app:

```text
http://127.0.0.1:8080
```

## Verify Frontend and Backend

Frontend:

```text
http://127.0.0.1:8080
```

Backend health check before login:

```powershell
Invoke-WebRequest http://127.0.0.1:8080/api/auth/me
```

Expected result before login is `401 Unauthorized`, which means the backend is running and security is active.

Full verification:

1. Sign up from the browser.
2. Create a project.
3. Add a project member by email.
4. Create a task.
5. Assign it to a member.
6. Update task status.
7. Check dashboard counts.

## API Summary

Authentication:

```text
POST /api/auth/signup
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

Projects:

```text
GET    /api/projects
POST   /api/projects
GET    /api/projects/{projectId}/members
POST   /api/projects/{projectId}/members
DELETE /api/projects/{projectId}/members/{userId}
```

Tasks:

```text
GET    /api/projects/{projectId}/tasks
POST   /api/projects/{projectId}/tasks
PUT    /api/tasks/{taskId}
PUT    /api/tasks/{taskId}/status
DELETE /api/tasks/{taskId}
```

Dashboard:

```text
GET /api/projects/{projectId}/dashboard
```

## MySQL Workbench

Connect with:

```text
Hostname: 127.0.0.1
Port: 3306
Username: root
Password: your_mysql_password
```

Refresh schemas and open:

```text
taskmanager -> Tables
```

Expected tables:

```text
app_users
projects
project_members
tasks
```

Useful SQL:

```sql
USE taskmanager;
SHOW TABLES;
SELECT * FROM app_users;
SELECT * FROM projects;
SELECT * FROM project_members;
SELECT * FROM tasks;
```

## Testing

Run:

```bash
./mvnw test
```

On Windows:

```cmd
mvnw.cmd test
```

## Build

Create a deployable JAR:

```bash
./mvnw clean package
```

On Windows:

```cmd
mvnw.cmd clean package
```

The JAR will be generated under:

```text
target/
```

Run the JAR:

```bash
java -jar target/taskmanager-0.0.1-SNAPSHOT.jar
```

## Deployment

Set these environment variables on the hosting platform:

```text
MYSQLHOST=your-db-host
MYSQLPORT=3306
MYSQLDATABASE=taskmanager
MYSQLUSER=your-db-user
MYSQLPASSWORD=your-db-password
DDL_AUTO=update
PORT=8080
```

Build command:

```bash
./mvnw clean package -DskipTests
```

Start command:

```bash
java -jar target/taskmanager-0.0.1-SNAPSHOT.jar
```

For platforms that provide a dynamic port, keep:

```properties
server.port=${PORT:8080}
```

## Notes

- Do not commit real database passwords.
- Use environment variables for deployment secrets.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development. For production, use migrations such as Flyway or Liquibase.
- If port `8080` is already in use on Windows, stop the process:

```powershell
$conn = Get-NetTCPConnection -LocalPort 8080 -State Listen
Stop-Process -Id $conn.OwningProcess -Force
```
