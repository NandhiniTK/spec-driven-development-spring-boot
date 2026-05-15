# Project Roadmap

## Phase 1: Foundation Setup (Week 1)

- [x] Create project specification documents
  - [x] composition.md
  - [x] roadmap.md
  - [x] techstack.md
- [x] Initialize Application Management Service from Spring Initializr
- [x] Set up version control (Git)
- [ ] Push to GitHub
- [x] Configure Gradle build file
- [x] Install PostgreSQL locally and create database: appmanagementdb
- [x] Configure JPA/Hibernate and application properties

## Phase 2: Entity & CRUD API (Week 2-3)

### Week 2: Entity & Repository
- [x] Design and create Application entity with JPA annotations
- [x] Create ApplicationRepository interface
- [x] Create DTOs (Request/Response)
- [x] Implement entity-to-DTO mappers (MapStruct)

### Week 3: CRUD API
- [x] Create Application REST controller
- [x] Implement Create Application API
- [x] Implement Get Application by ID API
- [x] Implement Get All Applications API
- [x] Implement Update Application API
- [x] Implement Delete Application API
- [x] Add input validation and error handling
- [x] Add API documentation (Swagger/OpenAPI)

## Phase 3: Testing (Week 4)

- [x] Unit tests for service layer (JUnit 5 + Mockito)
- [x] Unit tests for repository layer
- [x] Integration tests for REST APIs
- [x] Test coverage reporting (JaCoCo) — target >80% (93% achieved)
- [x] API testing with Postman

## Phase 4: Polish (Week 5)

- [x] Code review and refactoring
- [x] Complete API documentation
- [x] Update README

## Future Enhancements

### Features
- [ ] Search and filtering
- [ ] Pagination and sorting
- [ ] Application status management
- [ ] Caching (Redis)
- [ ] Audit logging

### Security
- [ ] Authentication (JWT or Basic Auth)
- [ ] Authorization and role-based access control
- [ ] Rate limiting

### AWS Deployment
- [ ] Deploy to AWS ECS/EKS
- [ ] Configure AWS API Gateway
- [ ] Set up RDS for database
- [ ] Set up CI/CD with AWS CodePipeline

## Success Metrics

- API response time < 200ms (95th percentile)
- Test coverage > 80%
- Complete API documentation
- Clean, maintainable code
