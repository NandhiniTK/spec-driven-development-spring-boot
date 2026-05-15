# Project Composition

## Overview
This project follows a microservices architecture pattern with each service being independently deployable and maintainable.

## Architecture Pattern
- **Style**: Microservices Architecture
- **Communication**: REST APIs (synchronous)
- **API Gateway**: AWS API Gateway (future deployment)

## Microservices Structure

### Core Services

#### 1. Application Management Service
- **Purpose**: Manages application metadata and configuration
- **Port**: 8080
- **Database**: PostgreSQL
- **Key Responsibilities**:
  - Application metadata CRUD operations

## Directory Structure

```
sdd-poc/
├── docs/                          # Project documentation
│   ├── composition.md
│   ├── roadmap.md
│   ├── techstack.md
│   └── api/                       # API specifications
│       └── application-management-api.md
├── services/                      # Microservices
│   └── application-management-service/
└── README.md
```

## Service Communication

### Synchronous Communication
- REST APIs using Spring Web
- HTTP/HTTPS protocol
- JSON data format

## Data Management

### Database Strategy
- Single database for Application Management Service
- **Primary**: PostgreSQL
- **ORM**: JPA with Hibernate
- **Migration**: Flyway or Liquibase (optional)

## Cross-Cutting Concerns

### Logging
- SLF4J with Logback

### Monitoring
- Spring Boot Actuator

### Security
- No authentication initially (open APIs)
- Authentication/Authorization to be added in future phase
- HTTPS/TLS for production

### Configuration Management
- Environment-specific properties files (application.yml)

## Development Environment

### Local Development
- Local PostgreSQL installation
