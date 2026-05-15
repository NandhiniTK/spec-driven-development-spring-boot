# Implementation Checklist

## ✅ Phase 0: Specifications (COMPLETED)

- [x] Create composition.md
- [x] Create roadmap.md
- [x] Create techstack.md
- [x] Create API specification (application-management-api.md)
- [x] Create .gitignore

## 📋 Phase 1: Foundation Setup (Week 1)

### Service Initialization
- [x] Visit https://start.spring.io
- [x] Configure: Group `com.nandhini.poc`, Artifact `application-management-service`, Java 17
- [x] Add dependencies: Spring Web, Spring Data JPA, PostgreSQL, Validation, Lombok, DevTools, Actuator
- [x] Generate, download, and extract to `services/application-management-service/`
- [x] Add SpringDoc OpenAPI dependency to build.gradle
- [x] Create application.yml with database configuration

### Database Setup
- [x] Install PostgreSQL locally (if needed)
- [x] Create database: `CREATE DATABASE appmanagementdb;`
- [x] Update application.yml with credentials

### Build & Run
- [x] Run `./gradlew build`
- [x] Run `./gradlew bootRun`
- [x] Verify health: http://localhost:8080/actuator/health
- [x] Verify Swagger UI: http://localhost:8080/swagger-ui.html

### Version Control
- [x] Initialize Git repository
- [ ] Create initial commit
- [ ] Push to GitHub

## 📋 Phase 2: Entity & CRUD API (Week 2-3)

### Week 2: Entity & Repository

- [x] Create Application entity with JPA annotations
- [x] Add fields: id, name, description, version, status, owner, technology, environment, url, metadata, createdAt, updatedAt
- [x] Create Status enum (ACTIVE, INACTIVE, MAINTENANCE)
- [x] Create Environment enum (DEV, TEST, STAGING, PROD)
- [x] Create ApplicationRepository (extends JpaRepository)
- [x] Create ApplicationRequestDTO and ApplicationResponseDTO
- [x] Create ApplicationMapper (MapStruct)

### Week 3: Service & Controller

- [x] Create ApplicationService interface
- [x] Create ApplicationServiceImpl with CRUD methods:
  - [x] createApplication
  - [x] getApplicationById
  - [x] getAllApplications
  - [x] updateApplication
  - [x] deleteApplication
- [x] Create exception classes (ResourceNotFoundException, DuplicateResourceException)
- [x] Create GlobalExceptionHandler (@ControllerAdvice)
- [x] Create ApplicationController with endpoints:
  - [x] POST /api/v1/applications
  - [x] GET /api/v1/applications/{id}
  - [x] GET /api/v1/applications
  - [x] PUT /api/v1/applications/{id}
  - [x] DELETE /api/v1/applications/{id}
- [x] Add input validation (@Valid)
- [x] Add Swagger annotations

## 📋 Phase 3: Testing (Week 4)

- [x] Unit tests for ApplicationServiceImpl (JUnit 5 + Mockito)
- [x] Unit tests for ApplicationRepository (Mockito)
- [x] Integration tests for ApplicationController (MockMvc)
- [x] Add JaCoCo plugin — target >80% coverage (93% achieved)
- [x] API testing with Postman (collection created)

## 📋 Phase 4: Polish (Week 5)

- [x] Code review and refactoring
- [x] Complete Swagger documentation (OpenApiConfig + @Schema on DTOs + error schemas)
- [x] Update README (completed project format)
- [x] Final checks: all tests passing, endpoints working, Swagger UI accessible

## Quick Commands

```bash
psql -U postgres -d appmanagementdb          # Access database
./gradlew build                               # Build
./gradlew bootRun                             # Run
./gradlew test                                # Test
./gradlew jacocoTestReport                    # Coverage report
./gradlew clean build                         # Clean build
```
