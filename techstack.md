# Technology Stack

## Core Technologies

### Backend Framework
- **Spring Boot**: 3.5.14
- **Java Version**: 17 (LTS)
- **Build Tool**: Gradle 8.x with Groovy DSL
- **Project Type**: Spring Boot Application (from Spring Initializr)

### Spring Boot Dependencies

#### Essential Dependencies (All Services)
```groovy
dependencies {
    // Spring Web - REST API
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Spring Data JPA - ORM
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    
    // Database Driver (choose one)
    runtimeOnly 'org.postgresql:postgresql'
    
    // Validation
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Lombok - Reduce boilerplate
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Spring Boot DevTools - Development
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.mockito:mockito-core'
}
```

#### Service-Specific Dependencies

**User Service (Additional)**
```groovy
// Security & JWT
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```

**All Services (Recommended)**
```groovy
// Actuator - Health checks and metrics
implementation 'org.springframework.boot:spring-boot-starter-actuator'

// API Documentation
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.17'
```

## Database Layer

### ORM & Database
- **ORM Framework**: Hibernate (via Spring Data JPA)
- **Database**: PostgreSQL 15+
- **Connection Pooling**: HikariCP (default in Spring Boot)
- **Migration Tool**: Flyway or Liquibase (optional)

### JPA Configuration
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # use 'update' for dev, 'validate' for prod
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## API Layer

### REST API
- **Framework**: Spring Web MVC
- **Data Format**: JSON
- **Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Validation**: Jakarta Bean Validation (Hibernate Validator)

### API Documentation
- **Tool**: Swagger/OpenAPI 3.0
- **UI**: Swagger UI (auto-generated)
- **Access**: `http://localhost:808x/swagger-ui.html`

## Security

### Authentication & Authorization
- **Framework**: Spring Security 6.x
- **Token**: JWT (JSON Web Tokens)
- **Library**: JJWT (Java JWT)
- **Password Encoding**: BCrypt
- **HTTPS**: TLS 1.3

## Testing

### Testing Frameworks
- **Unit Testing**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **Integration Testing**: Spring Boot Test
- **API Testing**: RestAssured or MockMvc
- **Test Containers**: Testcontainers (for database integration tests)

### Code Quality
- **Code Coverage**: JaCoCo
- **Static Analysis**: SonarQube (optional)
- **Linting**: Checkstyle (optional)

## Logging & Monitoring

### Logging
- **Framework**: SLF4J with Logback (Spring Boot default)
- **Log Format**: JSON (for production)
- **Log Levels**: DEBUG (dev), INFO (prod)

### Monitoring (Future)
- **Metrics**: Spring Boot Actuator + Micrometer
- **APM**: Prometheus + Grafana
- **Distributed Tracing**: Spring Cloud Sleuth + Zipkin

## Development Tools

### IDE
- **Recommended**: IntelliJ IDEA, Eclipse, or VS Code
- **Plugins**: Lombok, Spring Boot, Gradle

### Version Control
- **VCS**: Git
- **Platform**: GitHub, GitLab, or Bitbucket
- **Branching**: GitFlow or Trunk-based development

### API Testing
- **Tools**: Postman, Insomnia, or cURL
- **Collections**: Postman collections for each service

## Deployment (Future - AWS)

- AWS ECS/EKS for container orchestration
- AWS API Gateway for routing
- AWS RDS for managed database

## Build & CI/CD

### Build
- **Build Tool**: Gradle 8.x
- **Wrapper**: Gradle Wrapper (included)
- **Multi-Module**: Gradle multi-project build

### CI/CD (Future)
- **Pipeline**: Jenkins, GitLab CI, or GitHub Actions
- **Stages**: Build → Test → Package → Deploy
- **Artifact Repository**: Nexus or Artifactory

## Configuration Management

### Application Configuration
- **Format**: YAML (application.yml)
- **Profiles**: dev, test, prod
- **External Config**: Spring Cloud Config (future)

### Environment Variables
- **Secrets**: Environment variables or Vault
- **Database Credentials**: Externalized configuration

## Additional Libraries

### Utilities
- **Lombok**: Reduce boilerplate code
- **MapStruct**: Bean mapping (optional)
- **Apache Commons**: Utility functions

### HTTP Client (for inter-service communication)
- **RestTemplate**: Spring's synchronous client
- **WebClient**: Spring WebFlux reactive client (recommended)
- **Feign Client**: Declarative REST client (Spring Cloud)

## Spring Initializr Configuration

### Project Metadata
```
Project: Gradle - Groovy
Language: Java
Spring Boot: 3.5.14
Packaging: Jar
Java: 17

Group: com.nandhini.poc
Artifact: application-management-service
Name: Application Management Service
Description: Application metadata and configuration management service
Package name: com.nandhini.poc.applicationmanagement
```

### Dependencies to Select
**For all services:**
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Validation
- Lombok
- Spring Boot DevTools
- Spring Boot Actuator

**For User Service (additional):**
- Spring Security

## Database Schema Management

### Schema Versioning
- **Tool**: Flyway (recommended) or Liquibase
- **Location**: `src/main/resources/db/migration`
- **Naming**: `V1__Initial_schema.sql`, `V2__Add_users_table.sql`

## Performance Optimization

### Caching (Future)
- **Framework**: Spring Cache
- **Provider**: Redis or Caffeine
- **Use Cases**: Frequently accessed data

### Database Optimization
- **Indexing**: Strategic database indexes
- **Query Optimization**: JPQL/HQL optimization
- **Connection Pooling**: HikariCP tuning

## Security Best Practices

### Dependencies
- **Vulnerability Scanning**: OWASP Dependency-Check
- **Updates**: Regular dependency updates
- **Security Headers**: Spring Security headers

### Data Protection
- **Encryption**: At rest and in transit
- **Sensitive Data**: Masked in logs
- **SQL Injection**: Parameterized queries (JPA)

## Documentation

### Code Documentation
- **JavaDoc**: For public APIs
- **README**: Per service
- **API Docs**: OpenAPI/Swagger

### Architecture Documentation
- **Diagrams**: C4 Model or UML
- **ADRs**: Architecture Decision Records

## Development Environment

### Minimum Requirements
- **Java**: JDK 17
- **Gradle**: 8.x (via wrapper)
- **Database**: PostgreSQL 15+
- **RAM**: 8GB minimum, 16GB recommended
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code

## Version Matrix

| Component | Version |
|-----------|---------|
| Spring Boot | 3.5.14 |
| Java | 17 |
| Gradle | 8.x |
| PostgreSQL | 15+ |
| Hibernate | 6.x (via Spring Boot) |

## Notes
- All versions are subject to change based on compatibility and security updates
- Use Spring Boot's dependency management for consistent versions
- Prefer Spring Boot starters over individual dependencies
- Keep dependencies minimal to reduce attack surface and complexity
