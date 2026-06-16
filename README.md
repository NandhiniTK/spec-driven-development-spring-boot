# Spec-Driven Development - Microservices POC

## Overview

A **Spec-Driven Development (SDD)** project demonstrating a full-stack application with:
- **Frontend**: React UI for application management
- **Backend**: Microservices architecture with Spring Boot
- **Services**: Application Management Service & Payment Gateway Service

The Application Management Service provides CRUD operations with full validation, error handling, Swagger documentation, and 93% test coverage. A React UI provides a user-friendly interface for creating and viewing applications.

## Tech Stack

### Frontend
| Component | Version |
|-----------|---------|
| React | 18.x |
| Vite | 5.x |
| Tailwind CSS | 3.x |
| Axios | 1.x |
| Node.js | 18+ LTS |

### Backend
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
├── docs/
│   ├── api/
│   │   ├── application-management-api.md   # API specification
│   │   └── payment-gateway-api.md          # Payment API spec
│   └── ui/
│       └── application-management-ui.md    # UI specification
├── ui/
│   └── application-management-ui/          # React application
│       ├── src/
│       │   ├── components/                 # React components
│       │   ├── services/                   # API service layer
│       │   └── App.jsx                     # Main app component
│       ├── package.json
│       └── README.md
├── services/
│   ├── application-management-service/
│   │   ├── src/main/java/.../applicationmanagement/
│   │   │   ├── config/                     # OpenAPI, CORS config
│   │   │   ├── controller/                 # REST controller
│   │   │   ├── dto/                        # Request/Response DTOs
│   │   │   ├── entity/                     # JPA entity + enums
│   │   │   ├── exception/                  # Exception handling
│   │   │   ├── mapper/                     # MapStruct mapper
│   │   │   ├── repository/                 # Spring Data repository
│   │   │   ├── service/                    # Business logic
│   │   │   └── validation/                 # Custom validators
│   │   ├── src/test/java/                  # Unit + integration tests
│   │   ├── postman/                        # Postman collection
│   │   └── build.gradle
│   └── payment-gateway-service/            # Payment service
├── composition.md                          # Architecture design
├── techstack.md                            # Technology stack
├── roadmap.md                              # Development timeline
├── IMPLEMENTATION_CHECKLIST.md             # Task tracking
└── README.md
```

## Getting Started

### Prerequisites

- **Frontend**: Node.js 18+ LTS, npm or yarn
- **Backend**: Java JDK 17
- **Database**: PostgreSQL 15+ (local installation)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code

### Backend Setup & Run

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

3. **Build & Run Backend**
   ```bash
   cd services/application-management-service
   ./gradlew build
   ./gradlew bootRun
   ```

4. **Verify Backend**
   - Health: http://localhost:8080/actuator/health
   - Swagger UI: http://localhost:8080/swagger-ui.html

### Frontend Setup & Run

1. **Install dependencies**
   ```bash
   cd ui/application-management-ui
   npm install
   ```

2. **Run Development Server**
   ```bash
   npm run dev
   ```

3. **Access UI**
   - Application UI: http://localhost:5173
   - Create applications and view them in a table

### Full Stack Development

Run both backend and frontend simultaneously:

**Terminal 1 (Backend)**:
```bash
cd services/application-management-service
./gradlew bootRun
```

**Terminal 2 (Frontend)**:
```bash
cd ui/application-management-ui
npm run dev
```

Then access the UI at http://localhost:5173

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
| `composition.md` | Architecture & service design (frontend + backend) |
| `roadmap.md` | Development timeline (16 weeks) |
| `techstack.md` | Technology stack & dependencies (React + Spring Boot) |
| `docs/api/application-management-api.md` | Backend API specification |
| `docs/api/payment-gateway-api.md` | Payment Gateway API specification |
| `docs/ui/application-management-ui.md` | Frontend UI specification |
| `IMPLEMENTATION_CHECKLIST.md` | Development task tracking |

## AI-Assisted Development

This project was built using an AI coding assistant (Windsurf Cascade) following the spec-driven approach. The specifications (`composition.md`, `techstack.md`, `roadmap.md`, `IMPLEMENTATION_CHECKLIST.md`, `docs/api/application-management-api.md`) serve as the single source of truth, guiding the AI through each development phase.

### Sample Prompt

Use this prompt pattern when starting a new phase with an AI assistant:

> Let's start with phase **N** from roadmap. Ensure you read through all the md files (composition, techstack, readme, implementation_checklist, payment-gateway-api.md). Request for acknowledgement before applying changes. If you have any open questions, please ask.

This prompt ensures the AI:
1. **Reads all specs first** before writing any code
2. **Acknowledges the plan** so you can review and adjust before changes are applied
3. **Asks clarifying questions** to avoid assumptions

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
