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

## 📋 Phase 2: Entity & CRUD API (Week 2-3)

### Week 2: Entity & Repository

- [ ] Create Application entity with JPA annotations
- [ ] Add fields: id, name, description, version, status, owner, technology, environment, url, metadata, createdAt, updatedAt
- [ ] Create Status enum (ACTIVE, INACTIVE, MAINTENANCE)
- [ ] Create Environment enum (DEV, TEST, STAGING, PROD)
- [ ] Create ApplicationRepository (extends JpaRepository)
- [ ] Create ApplicationRequestDTO and ApplicationResponseDTO
- [ ] Create ApplicationMapper

### Week 3: Service & Controller

- [ ] Create ApplicationService interface
- [ ] Create ApplicationServiceImpl with CRUD methods:
  - [ ] createApplication
  - [ ] getApplicationById
  - [ ] getAllApplications
  - [ ] updateApplication
  - [ ] deleteApplication
- [ ] Create exception classes (ResourceNotFoundException, DuplicateResourceException)
- [ ] Create GlobalExceptionHandler (@ControllerAdvice)
- [ ] Create ApplicationController with endpoints:
  - [ ] POST /api/v1/applications
  - [ ] GET /api/v1/applications/{id}
  - [ ] GET /api/v1/applications
  - [ ] PUT /api/v1/applications/{id}
  - [ ] DELETE /api/v1/applications/{id}
- [ ] Add input validation (@Valid)
- [ ] Add Swagger annotations

## 📋 Phase 3: Testing (Week 4)

- [ ] Unit tests for ApplicationServiceImpl (JUnit 5 + Mockito)
- [ ] Unit tests for ApplicationRepository (@DataJpaTest)
- [ ] Integration tests for ApplicationController (MockMvc)
- [ ] Add JaCoCo plugin — target >80% coverage
- [ ] API testing with Postman

## 📋 Phase 4: Polish (Week 5)

- [ ] Code review and refactoring
- [ ] Complete Swagger documentation
- [ ] Update README
- [ ] Create Postman collection
- [ ] Final checks: all tests passing, endpoints working, Swagger UI accessible

## Quick Commands

```bash
psql -U postgres -d appmanagementdb          # Access database
./gradlew build                               # Build
./gradlew bootRun                             # Run
./gradlew test                                # Test
./gradlew jacocoTestReport                    # Coverage report
./gradlew clean build                         # Clean build
```
