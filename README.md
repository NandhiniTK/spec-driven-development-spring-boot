# Spec-Driven Development - Microservices POC

## Overview

A **Spec-Driven Development (SDD)** project demonstrating a fully functional CRUD REST API built with Java Spring Boot. The Application Management Service manages application metadata with full validation, error handling, Swagger documentation, and 93% test coverage.

## Tech Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 3.5.14 |
| Java | 17 |
| Gradle | 8.x (Groovy DSL) |
| PostgreSQL | 15+ |
| SpringDoc OpenAPI | 2.8.17 |
| MapStruct | 1.6.3 |
| JaCoCo | 0.8.12 |

## Project Structure

```
sdd-poc/
├── docs/api/
│   └── application-management-api.md   # API specification
├── services/application-management-service/
│   ├── src/main/java/.../applicationmanagement/
│   │   ├── config/                     # OpenAPI configuration
│   │   ├── controller/                 # REST controller
│   │   ├── dto/                        # Request/Response DTOs
│   │   ├── entity/                     # JPA entity + enums
│   │   ├── exception/                  # Exception handling
│   │   ├── mapper/                     # MapStruct mapper
│   │   ├── repository/                 # Spring Data repository
│   │   ├── service/                    # Business logic
│   │   └── validation/                 # Custom validators
│   ├── src/test/java/                  # Unit + integration tests
│   ├── postman/                        # Postman collection
│   └── build.gradle
├── composition.md                      # Architecture design
├── techstack.md                        # Technology stack
├── roadmap.md                          # Development timeline
├── IMPLEMENTATION_CHECKLIST.md         # Task tracking
└── README.md
```

## Getting Started

### Prerequisites

- Java JDK 17
- PostgreSQL 15+ (local installation)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Setup & Run

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd sdd-poc
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE appmanagementdb;
   ```
   Default credentials: `postgres` / `postgres` (configured in `application.yml`)

3. **Build**
   ```bash
   cd services/application-management-service
   ./gradlew build
   ```

4. **Run**
   ```bash
   ./gradlew bootRun
   ```

5. **Verify**
   - Health: http://localhost:8080/actuator/health
   - Swagger UI: http://localhost:8080/swagger-ui.html

## API Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v1/applications` | Create application | 201 |
| GET | `/api/v1/applications/{id}` | Get by ID | 200 |
| GET | `/api/v1/applications` | Get all | 200 |
| PUT | `/api/v1/applications/{id}` | Update application | 200 |
| DELETE | `/api/v1/applications/{id}` | Delete application | 204 |
| GET | `/actuator/health` | Health check | 200 |

See `docs/api/application-management-api.md` for full API specification including request/response schemas and validation rules.

## Testing

```bash
./gradlew test                    # Run all tests
./gradlew jacocoTestReport        # Generate coverage report
```

- **33 tests** across 3 test classes
- **93% instruction coverage** (JaCoCo, >80% enforced)
- Coverage report: `build/reports/jacoco/test/html/index.html`

| Test Class | Type | Tests |
|------------|------|-------|
| `ApplicationServiceImplTest` | Unit (Mockito) | 11 |
| `ApplicationRepositoryTest` | Unit (Mockito) | 9 |
| `ApplicationControllerTest` | Integration (MockMvc) | 13 |

### Postman

Import `services/application-management-service/postman/Application-Management-API.postman_collection.json` into Postman for manual API testing.

## Specifications

| Document | Purpose |
|----------|---------|
| `composition.md` | Architecture & service design |
| `roadmap.md` | 5-week development timeline |
| `techstack.md` | Technology stack & dependencies |
| `docs/api/application-management-api.md` | Complete API specification |
| `IMPLEMENTATION_CHECKLIST.md` | Development task tracking |

## Troubleshooting

**Port 8080 in use:**
```bash
netstat -ano | findstr :8080
taskkill /PID <process-id> /F
```

**Database connection failed:**
- Verify PostgreSQL is running
- Verify database exists: `psql -U postgres -l`
- Check credentials in `application.yml`

**Build failed:**
```bash
./gradlew clean build
```
