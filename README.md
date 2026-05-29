# UndoSchool Backend

UndoSchool Backend is a production-style REST API built using Spring Boot for managing teachers, parents, sessions, and bookings in an online learning platform.

The project focuses on clean backend architecture, booking conflict validation, Dockerized deployment, and cloud-native development practices.

---

# Live Deployment

https://undoschool-backend.onrender.com/

## Swagger API Documentation

https://undoschool-backend.onrender.com/swagger-ui/index.html

---

# Tech Stack

| Technology | Purpose |
|------------|----------|
| Spring Boot 3 | Backend Framework |
| Java 21 | Programming Language |
| Spring Data JPA | ORM Framework |
| MySQL / TiDB | Relational Database |
| Docker | Containerization |
| Render | Cloud Deployment |
| Swagger / OpenAPI | API Documentation |
| Maven | Dependency Management |

---

# Features

## Teacher Management
- Create teacher
- Update teacher details
- Delete teacher
- Fetch teacher information

## Parent Management
- Register parent
- Fetch parent details
- Manage parent profiles

## Session Management
- Create sessions
- Manage session schedules
- Associate sessions with teachers

## Booking Management
- Create bookings
- Prevent overlapping bookings
- Validate booking schedules

## Exception Handling
- Global exception handling
- Structured API error responses

## Validation
- DTO validation
- Request body validation
- Input constraints

## API Documentation
- Swagger/OpenAPI integration
- Interactive API testing

---

# Project Architecture

```text
Client
   ↓
Controller Layer
   ↓
Service Layer
   ↓
Repository Layer
   ↓
MySQL / TiDB Database
```

---

# Project Structure

```text
src/main/java/com/undoschool/backend
│
├── booking
├── teacher
├── parent
├── session
├── config
├── exception
├── util
└── common
```

---

# Booking Conflict Logic

The system prevents overlapping bookings.

Conflict condition:

```text
(newStart < existingEnd)
AND
(newEnd > existingStart)
```

Example:

```text
Existing Booking:
1:00 PM -------- 2:00 PM

New Booking:
1:30 PM -------- 2:30 PM

→ Conflict Detected
```

This ensures teachers cannot have overlapping sessions.

---

# API Base URL

```text
https://undoschool-backend.onrender.com/api/v1
```

---

# Swagger Documentation

Interactive Swagger/OpenAPI documentation:

```text
https://undoschool-backend.onrender.com/swagger-ui/index.html
```

Features available in Swagger:
- API endpoint testing
- Request/Response schemas
- Validation details
- API examples
- Error response documentation

---

# API Endpoints

# Teacher APIs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | /api/v1/teachers | Create teacher |
| GET | /api/v1/teachers/{id} | Get teacher |
| PUT | /api/v1/teachers/{id} | Update teacher |
| DELETE | /api/v1/teachers/{id} | Delete teacher |

---

# Parent APIs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | /api/v1/parents | Create parent |
| GET | /api/v1/parents/{id} | Get parent |

---

# Session APIs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | /api/v1/sessions | Create session |
| GET | /api/v1/sessions | Get all sessions |

---

# Booking APIs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | /api/v1/bookings | Create booking |
| GET | /api/v1/bookings | Get all bookings |

---

# Example Request

## Create Booking

```http
POST /api/v1/bookings
Content-Type: application/json
```

```json
{
  "parentId": 1,
  "sessionId": 2,
  "startTime": "2026-05-29T10:00:00",
  "endTime": "2026-05-29T11:00:00"
}
```

---

# Example Success Response

```json
{
  "id": 10,
  "status": "CONFIRMED",
  "message": "Booking created successfully"
}
```

---

# Example Error Response

```json
{
  "timestamp": "2026-05-29T10:10:00",
  "status": 409,
  "error": "Conflict",
  "message": "Booking conflict detected"
}
```

---

# Docker Support

The application supports Dockerized deployment.

## Build Docker Image

```bash
docker build -t undoschool-backend .
```

## Run Docker Container

```bash
docker run -p 8080:8080 undoschool-backend
```

---

# Environment Variables

Configure these environment variables before running the application:

```env
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=
```

---

# Local Development Setup

## Clone Repository

```bash
git clone https://github.com/logeshramesh05/undoschool_backend.git
```

## Navigate to Project

```bash
cd undoschool_backend
```

## Build Project

```bash
mvn clean install
```

## Run Application

```bash
mvn spring-boot:run
```

---

# Deployment

The application is deployed using:

- Render for cloud hosting
- Docker containerization
- TiDB/MySQL cloud database

Deployment URL:

```text
https://undoschool-backend.onrender.com/
```

---

# Future Improvements

Planned future improvements:

- JWT Authentication
- Role-based Authorization
- Pagination
- Redis Caching
- Integration Testing
- CI/CD Pipelines
- Monitoring & Logging
- Rate Limiting

---

# Development Highlights

This project demonstrates:

- Layered backend architecture
- REST API development
- Booking conflict validation
- Docker deployment
- Cloud-native backend deployment
- OpenAPI/Swagger documentation
- Exception handling
- DTO validation

---

# Author

## Logesh Ramesh

GitHub:
https://github.com/logeshramesh05

Project Repository:
https://github.com/logeshramesh05/undoschool_backend

---
