# Hospital Manager API

A secure RESTful API for a hospital management system, built for Assignment 02 of Full-Stack Software Development (COSC 32133 / BECS 32263).

**Student:** P.L.T. Chandunu  
**Student ID:** EC/2022/058

## Technology stack

- Java 21 and Maven
- Spring Boot 3.5.16
- Spring Web and Spring Data JPA
- Spring Security 6
- JSON Web Tokens using JJWT 0.11.5
- MySQL for the application database
- H2 for automated tests
- Postman for API testing

## Project structure

```text
src/main/java/com/example/hospitalmanager
|-- config          Application data initialization
|-- controller      REST controllers
|-- dto             Request and response objects
|-- exception       API error handling
|-- model           JPA entities and enums
|-- repository      Spring Data repositories
|-- security        JWT and Spring Security configuration
`-- service         Business logic
```

## Prerequisites

- JDK 21
- MySQL Server 8 or later
- Git
- Postman (optional, for manual API testing)

## Database setup

Create the database in MySQL:

```sql
CREATE DATABASE hospital_manager;
```

Set the local credentials in PowerShell before starting the application:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
$env:JWT_SECRET = "your_base64_encoded_secret_of_at_least_48_bytes"
```

The default JWT value in `application.properties` is for local development only. Set a private `JWT_SECRET` in every deployed environment.

## Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`.

## Run the tests

```powershell
.\mvnw.cmd test
```

Tests use an in-memory H2 database and do not require MySQL.

## Authentication endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register an account |
| POST | `/api/auth/login` | Authenticate and receive a bearer token |

Example registration request:

```json
{
  "name": "P.L.T. Chandunu",
  "username": "EC/2022/058",
  "password": "change-this-password",
  "role": "PATIENT"
}
```

Example login request:

```json
{
  "username": "EC/2022/058",
  "password": "change-this-password"
}
```

For protected endpoints, select **Bearer Token** in Postman and paste only the token value returned by the login endpoint.

## Main protected endpoints

| Method | Endpoint | Access |
|---|---|---|
| GET/POST | `/api/users` | ADMIN |
| GET/POST | `/api/patients` | ADMIN, DOCTOR, NURSE as configured |
| GET/POST | `/api/appointments` | Authenticated roles as configured |
| GET/POST | `/api/medical-records` | Authorized clinical roles |

## JWT tampering result

Changing the decoded JWT role from `PATIENT` to `ADMIN` does not grant administrator access. The token signature no longer matches the modified payload, so the API rejects it with HTTP `401 Unauthorized`. A genuine token belonging to a user whose role lacks permission is rejected with HTTP `403 Forbidden`.

## Security notes

- Passwords are stored using BCrypt hashing.
- Authentication is stateless; no server-side HTTP session is created.
- JWT signatures and expiration are validated before claims are trusted.
- Secrets and database passwords should be supplied through environment variables and must not be committed.
