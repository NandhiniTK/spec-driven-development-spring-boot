# Spec-Driven Development - Microservices POC

## Overview
This project demonstrates a **Spec-Driven Development (SDD)** approach for building a microservices architecture using Java Spring Boot.

## Architecture
- **Service**: Application Management Service (Port: 8080)
- **Framework**: Spring Boot 3.5.14, Java 17
- **Build**: Gradle (Groovy DSL)
- **Database**: PostgreSQL (local installation)
- **APIs**: REST with Swagger/OpenAPI

## Key Specifications

| Document | Purpose |
|----------|---------|
| `composition.md` | Architecture & service design |
| `roadmap.md` | 5-week development timeline |
| `techstack.md` | Technology stack & dependencies |
| `docs/api/application-management-api.md` | Complete API specification |
| `IMPLEMENTATION_CHECKLIST.md` | Development task tracking |

## Getting Started

### Prerequisites
- Java JDK 17
- Gradle 8.x (wrapper included)
- PostgreSQL 15+ (local installation)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd sdd-poc
   ```

2. **Review Specifications**
   - Read `composition.md` for architecture overview
   - Check `techstack.md` for technology details
   - Follow `roadmap.md` for development phases

3. **Initialize Service from Spring Initializr**
   - Visit https://start.spring.io
   - Use configurations from `techstack.md`
   - Download and extract to `services/application-management-service/`

4. **Set up Database**
   - Install PostgreSQL locally
   - Create database: `CREATE DATABASE appmanagementdb;`
   - Update connection details in application.yml

5. **Build Service**
   ```bash
   cd services/application-management-service
   ./gradlew build
   ```

6. **Run Service**
   ```bash
   ./gradlew bootRun
   ```

## Development

Follow the roadmap in `roadmap.md`:
1. **Week 1**: Setup and initialization
2. **Week 2-3**: Entity, repository, service, and CRUD APIs
3. **Week 4**: Testing (>80% coverage)
4. **Week 5**: Polish and documentation

Use `IMPLEMENTATION_CHECKLIST.md` to track progress.

## Testing
```bash
./gradlew test                    # Run tests
./gradlew jacocoTestReport        # Coverage report
```

## Troubleshooting

**Port 8080 in use:**
```bash
netstat -ano | findstr :8080
taskkill /PID <process-id> /F
```

**Database connection failed:**
- Check PostgreSQL is running
- Verify database exists: `psql -U postgres -l`
- Check credentials in application.yml

**Build failed:**
```bash
./gradlew clean build
```

## Resources
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
