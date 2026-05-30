# UndoSchool Booking System

Production-ready backend for a **global live-learning platform** where teachers
conduct online classes for students across different countries and timezones.

---

## Table of Contents
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Database Design](#database-design)
- [API Reference](#api-reference)
- [Timezone Handling](#timezone-handling)
- [Booking Rules & Concurrency](#booking-rules--concurrency)
- [Running Locally](#running-locally)
- [Running with Docker](#running-with-docker)
- [Environment Variables](#environment-variables)
- [Testing](#testing)

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                  REST API Layer                      │
│  TeacherController  │  ParentController  │  Courses  │
├─────────────────────────────────────────────────────┤
│                 Service Layer                        │
│  OfferingService  │  BookingService  │  TimezoneUtil │
├─────────────────────────────────────────────────────┤
│               Repository Layer                       │
│  TeacherRepo  │  SessionRepo  │  BookingRepo         │
├─────────────────────────────────────────────────────┤
│                   MySQL (UTC)                        │
└─────────────────────────────────────────────────────┘
```

**Modular Monolith** — code is organized into independent modules:
```
com.undoschool/
├── booking/        # Booking entity, service, repository, DTOs
├── offering/       # Course + Offering entity, service, repository, DTOs
├── session/        # Session entity, repository, DTOs
├── teacher/        # Teacher entity, service, controller
├── parent/         # Parent entity, service, controller
├── util/           # TimezoneUtil, OverlapUtil
├── exception/      # GlobalExceptionHandler, custom exceptions
└── config/         # SwaggerConfig
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.0 |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Containerization | Docker + Docker Compose |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Validation | Jakarta Validation |

---

## Database Design

### Entity Relationship

```
Course (1) ──────< Offering (1) ──────< Session
                       │
                       └─────< Booking >───── Parent
Teacher (1) ─────< Offering
```

### Tables

#### `courses`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | Auto increment |
| title | VARCHAR | NOT NULL |
| description | VARCHAR | Nullable |

#### `teachers`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | Auto increment |
| name | VARCHAR | NOT NULL |
| email | VARCHAR | UNIQUE, NOT NULL |
| timezone | VARCHAR | IANA string e.g. `Asia/Kolkata` |

#### `parents`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | Auto increment |
| name | VARCHAR | NOT NULL |
| email | VARCHAR | UNIQUE, NOT NULL |
| timezone | VARCHAR | IANA string e.g. `America/New_York` |

#### `offerings`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | Auto increment |
| course_id | BIGINT FK | → courses.id |
| teacher_id | BIGINT FK | → teachers.id |
| title | VARCHAR | NOT NULL |
| description | VARCHAR | Nullable |
| status | ENUM | DRAFT, ACTIVE, CANCELLED |

#### `sessions`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | Auto increment |
| offering_id | BIGINT FK | → offerings.id |
| start_time_utc | DATETIME(6) | Always stored as UTC |
| end_time_utc | DATETIME(6) | Always stored as UTC |
| sequence_no | INT | Optional ordering |

**Indexes:** `(offering_id)`, `(start_time_utc, end_time_utc)`

#### `bookings`
| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT PK | Auto increment |
| parent_id | BIGINT FK | → parents.id |
| offering_id | BIGINT FK | → offerings.id |
| status | ENUM | CONFIRMED, CANCELLED |
| booked_at | DATETIME(6) | Auto-set on creation (UTC) |

**Indexes:** `(parent_id)`, `(offering_id)`

---

## API Reference

Base URL: `http://localhost:10000`
Swagger UI: `http://localhost:10000/swagger-ui.html`
OpenAPI JSON: `http://localhost:10000/api-docs`

### Courses
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/courses` | Create a course |
| GET | `/api/v1/courses` | List all courses |

### Teacher APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/teachers` | Register a teacher |
| POST | `/api/v1/teachers/offerings` | Create an offering |
| POST | `/api/v1/teachers/offerings/{offeringId}/sessions` | Add sessions |
| GET | `/api/v1/teachers/{teacherId}/offerings` | Get teacher's offerings |

### Parent APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/parents` | Register a parent |
| GET | `/api/v1/parents/offerings?timezone=` | Browse available offerings |
| POST | `/api/v1/parents/bookings` | Book an offering |
| GET | `/api/v1/parents/{parentId}/bookings` | Get parent's bookings |

### HTTP Status Codes
| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Validation failed |
| 404 | Resource not found |
| 409 | Booking conflict (overlap or duplicate) |
| 500 | Internal server error |

### Error Response Format
```json
{
  "status": 409,
  "message": "Schedule conflict: session on 2025-06-07T08:30:00-04:00 overlaps with your existing booking on 2025-06-07T08:00:00-04:00",
  "details": null,
  "timestamp": "2025-06-07T12:30:00Z"
}
```

For validation errors, `details` contains field-level messages:
```json
{
  "status": 400,
  "message": "Validation failed",
  "details": {
    "parentId": "parentId is required",
    "offeringId": "offeringId is required"
  },
  "timestamp": "2025-06-07T12:30:00Z"
}
```

---

## Timezone Handling

### Rule: Store UTC, Convert at the Edges

```
Teacher Input          Backend                  DB Storage
──────────────         ─────────────────────    ──────────────────────
"2025-06-07T18:00"  →  LocalDateTime.parse()  →  2025-06-07T12:30:00Z
+ "Asia/Kolkata"    →  .atZone(IST)           →  (UTC Instant)
                    →  .toInstant()
```

```
DB Storage             Backend                  Parent Response
──────────────         ─────────────────────    ──────────────────────
2025-06-07T12:30:00Z → .atZone(parentTimezone) → "2025-06-07T08:30:00-04:00"
                     → ISO_OFFSET_DATE_TIME      (America/New_York)
```

### Why `Instant` for entity fields?
- `Instant` has no timezone — always unambiguous UTC
- `LocalDateTime` silently uses JVM timezone — causes bugs in production
- `DATETIME(6)` with `hibernate.jdbc.time_zone=UTC` ensures correct storage

### IANA Timezone Examples
| Region | IANA String |
|--------|-------------|
| India | `Asia/Kolkata` |
| USA East | `America/New_York` |
| USA West | `America/Los_Angeles` |
| UK | `Europe/London` |
| Singapore | `Asia/Singapore` |
| UAE | `Asia/Dubai` |
| Japan | `Asia/Tokyo` |
| Australia | `Australia/Sydney` |

---

## Booking Rules & Concurrency

### Rule 1 — Offering-level Booking
Booking one offering books **all its sessions** as a single atomic unit.

### Rule 2 — Overlap Detection
Uses Allen's interval overlap algorithm:
```
overlap = (start1 < end2) AND (end1 > start2)
```
Implemented as a JPQL query in `SessionRepository`:
```java
SELECT s FROM Session s
JOIN Booking b ON b.offering.id = s.offering.id
WHERE b.parent.id = :parentId
  AND b.status = 'CONFIRMED'
  AND s.startTimeUtc < :endTime
  AND s.endTimeUtc   > :startTime
```

### Rule 3 — Concurrent Booking Safety
```
Thread 1 (Parent A books Offering 1)    Thread 2 (Parent A books Offering 2)
────────────────────────────────────    ────────────────────────────────────
BEGIN TRANSACTION
SELECT * FROM parents                   BEGIN TRANSACTION
  WHERE id = 1                          SELECT * FROM parents
  FOR UPDATE  ← acquires lock             WHERE id = 1
                                           FOR UPDATE  ← BLOCKED, waits
Check overlap → none
INSERT INTO bookings ...
COMMIT  ← releases lock
                                        ← unblocked, proceeds
                                        Check overlap → CONFLICT FOUND
                                        THROW BookingConflictException
                                        ROLLBACK
```

---

## Running Locally

### Prerequisites
- Java 21+
- MySQL 8.0 running locally
- Maven 3.9+

```bash
# 1. Create database
mysql -u root -p -e "CREATE DATABASE undoschool;"

# 2. Set environment variables
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=undoschool
export DB_USER=root
export DB_PASSWORD=yourpassword

# 3. Run
mvn spring-boot:run
```

---

## Running with Docker

```bash
# Start everything (app + MySQL)
docker compose up --build

# Stop
docker compose down

# Stop and remove volumes (fresh DB)
docker compose down -v
```

App: `http://localhost:10000`
Swagger: `http://localhost:10000/swagger-ui.html`

---

## Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | MySQL host | `db` (Docker) / `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `undoschool` |
| `DB_USER` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `strongpassword` |

---

## Testing

### Quick Test Flow (Swagger UI)

```
1. POST /api/v1/courses          → { "title": "Minecraft Coding" }
2. POST /api/v1/teachers         → { "name": "Ravi", "email": "ravi@test.com", "timezone": "Asia/Kolkata" }
3. POST /api/v1/parents          → { "name": "John", "email": "john@test.com", "timezone": "America/New_York" }
4. POST /api/v1/teachers/offerings
5. POST /api/v1/teachers/offerings/1/sessions  ← teacher submits IST times
6. GET  /api/v1/parents/offerings?timezone=America/New_York  ← parent sees EST times ✅
7. POST /api/v1/parents/bookings
8. POST /api/v1/parents/bookings  ← same offering = 409 Conflict ✅
```

### Timezone Verification
Teacher creates session at `18:00 IST`:
- DB stores: `12:30:00Z`
- Parent (EST) sees: `08:30:00-04:00` ✅
- Parent (SGT) sees: `20:30:00+08:00` ✅

### Conflict Verification
```
Book Offering A: June 7, 6:00 PM - 7:00 PM IST
Book Offering B: June 7, 6:30 PM - 7:30 PM IST  ← 409 Conflict ✅
Book Offering C: June 7, 8:00 PM - 9:00 PM IST  ← 201 Created  ✅
```
