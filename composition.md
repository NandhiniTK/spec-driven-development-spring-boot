# Project Composition

## Overview
This project follows a microservices architecture pattern with a React frontend. Each service is independently deployable and maintainable.

## Architecture Pattern
- **Style**: Microservices Architecture with Frontend
- **Communication**: REST APIs (synchronous), Message Queue (asynchronous)
- **Frontend**: React SPA
- **API Gateway**: AWS API Gateway (future deployment)

## System Components

### 1. Frontend Layer

#### Application Management UI
- **Technology**: React 18+ with Vite
- **Port**: 5173 (development)
- **Purpose**: User interface for managing applications
- **Key Features**:
  - Create new applications
  - View all applications in table format
  - No authentication (experimental)
  - Responsive design
- **API Integration**:
  - Consumes Application Management Service REST APIs
  - CORS enabled for local development

---

### 2. Backend Services

#### Application Management Service
- **Purpose**: Manages application metadata and configuration
- **Port**: 8080
- **Database**: PostgreSQL (appmanagementdb)
- **Technology**: Spring Boot 3.x, Java 17
- **Key Responsibilities**:
  - Application metadata CRUD operations
  - RESTful API endpoints
  - Data validation and persistence
  - CORS configuration for frontend

#### Payment Gateway Service
- **Purpose**: Processes payments with high reliability and security
- **Port**: 8081
- **Database**: PostgreSQL (paymentgatewaydb)
- **Message Queue**: AWS SQS
- **Technology**: Spring Boot 3.x, Java 17
- **Key Responsibilities**:
  - Payment initiation and processing
  - Idempotency management (duplicate prevention)
  - Multiple payment method support (Card, UPI, Wallet, Net Banking)
  - External payment gateway integration (Mock for POC)
  - Refund processing
  - Payment status tracking
  - Gateway webhooks
  - Audit trail and transaction logging

---

## Directory Structure

```
sdd-poc/
├── docs/                          # Project documentation
│   ├── api/                       # API specifications
│   │   ├── application-management-api.md
│   │   └── payment-gateway-api.md
│   └── ui/                        # UI specifications
│       └── application-management-ui.md
├── services/                      # Microservices
│   ├── application-management-service/
│   │   ├── src/
│   │   ├── build.gradle
│   │   └── README.md
│   └── payment-gateway-service/
│       ├── src/
│       ├── build.gradle
│       └── README.md
├── ui/                            # Frontend applications
│   └── application-management-ui/ # React application (to be created)
│       ├── src/
│       ├── package.json
│       └── README.md
├── composition.md
├── roadmap.md
├── techstack.md
└── README.md
```

---

## Service Communication

### Synchronous Communication
- **Protocol**: REST APIs using Spring Web
- **Format**: JSON
- **Transport**: HTTP/HTTPS
- **Frontend to Backend**: Axios HTTP client

### Asynchronous Communication
- **Message Queue**: AWS SQS
- **Use Case**: Payment processing (decouple payment initiation from processing)
- **Pattern**: Producer-Consumer (microservices decoupling)
  - Payment Gateway Service publishes payment messages to SQS
  - Payment Processor consumes messages asynchronously
- **Benefits**: 
  - High throughput and scalability
  - Fault tolerance (retry on failure, DLQ)
  - Decoupling of services

---

## Data Management

### Databases
1. **appmanagementdb**: Application Management Service
   - Tables: applications
   - Simple CRUD operations
   
2. **paymentgatewaydb**: Payment Gateway Service
   - Tables: payments, payment_transactions, idempotency_keys
   - Optimistic locking (@Version) for concurrency control
   - Pessimistic locking (SELECT FOR UPDATE) for critical sections
   - Idempotency keys with unique constraints
   - Strategic indexes for performance

### Database Strategy
- **Primary**: PostgreSQL
- **ORM**: JPA with Hibernate
- **Migration**: Flyway (optional for production)
- **Isolation**: Each service has its own database

---

## External Integrations

### Payment Gateway Service
- **Payment Gateways**: Mock Gateway (POC), Stripe/Razorpay (future)
- **Fault Tolerance**: Resilience4j
  - Circuit Breaker for gateway failures
  - Retry with exponential backoff
  - Timeout configuration (10s)
  - Bulkhead for concurrency control
  - Fallback mechanisms (TIMEOUT status)
- **Webhooks**: Receive payment status callbacks from external gateways
  - HMAC-SHA256 signature verification
  - Idempotent webhook processing

---

## Cross-Cutting Concerns

### Logging & Monitoring
- **Logging Framework**: SLF4J with Logback
- **Structured Logging**: JSON format (production) via logstash-logback-encoder
- **Correlation ID**: Request tracing across services and async operations
- **Monitoring**: Spring Boot Actuator
- **Metrics**: Micrometer with Prometheus
  - Payment success/failure/timeout counters
  - Processing time timers
  - Gateway API latency
- **Health Checks**: 
  - Database connectivity
  - SQS connectivity (when enabled)
  - Payment gateway availability

### Security
- **Current State**: No authentication (experimental/POC)
- **Future Enhancements**:
  - JWT-based authentication
  - Role-based access control
- **Data Security**:
  - Encryption at rest and in transit
  - PCI-DSS compliance considerations
  - Never store full card numbers (tokenization)
  - Sensitive data masking in logs
- **API Security**:
  - Idempotency keys for duplicate prevention
  - Rate limiting (database-based)
  - Webhook signature verification
  - CORS configuration for frontend
- **HTTPS/TLS**: Mandatory for production

### Configuration Management
- Environment-specific properties files (application.yml)
- Profile-based configuration (dev, prod)
- Environment variables for sensitive data

---

## Development Environment

### Local Development Setup
- **Frontend**: 
  - Node.js 18+
  - npm/yarn
  - Vite dev server (port 5173)
  
- **Backend**:
  - Java 17
  - Gradle 8.x
  - Local PostgreSQL installation (multiple databases)
  - LocalStack for AWS SQS (optional for local testing)
  - Mock payment gateway for testing

### CORS Configuration
- Application Management Service allows requests from `http://localhost:5173`
- Payment Gateway Service (future UI integration)

---

## Deployment Architecture (Future)

### AWS Deployment
- **Compute**: ECS/EKS for containerized services
- **Database**: AWS RDS PostgreSQL
- **Message Queue**: AWS SQS
- **API Gateway**: AWS API Gateway
- **Frontend**: S3 + CloudFront for static hosting
- **Load Balancer**: Application Load Balancer
- **CI/CD**: AWS CodePipeline / GitHub Actions

### Container Strategy
- Docker containers for each service
- Docker Compose for local multi-service setup
- Kubernetes manifests for production (optional)

---

## Technology Stack Summary

| Component | Technology |
|-----------|-----------|
| **Frontend** | React 18+, Vite, Tailwind CSS, Axios |
| **Backend** | Spring Boot 3.x, Java 17 |
| **Database** | PostgreSQL 15+ |
| **Message Queue** | AWS SQS |
| **Logging** | SLF4J, Logback, Logstash Encoder |
| **Monitoring** | Actuator, Micrometer, Prometheus |
| **Fault Tolerance** | Resilience4j |
| **Build Tools** | Gradle (Backend), Vite (Frontend) |
| **Testing** | JUnit 5, Mockito, Spring Test |
| **Documentation** | Swagger/OpenAPI |

---

## Service Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Management UI                 │
│                    (React + Vite + Tailwind)                │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API (HTTP)
                         ▼
┌─────────────────────────────────────────────────────────────┐
│           Application Management Service (Port 8080)         │
│                    (Spring Boot + PostgreSQL)                │
└─────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────┐
│            Payment Gateway Service (Port 8081)               │
│                    (Spring Boot + PostgreSQL)                │
└────────┬──────────────────────┬─────────────────────────────┘
         │                      │
         │ SQS Messages         │ API Calls
         ▼                      ▼
┌─────────────────┐    ┌──────────────────────┐
│    AWS SQS      │    │  Mock Payment Gateway│
│  (Async Queue)  │    │   (Resilience4j)     │
└─────────────────┘    └──────────────────────┘
```

---

## Key Design Decisions

1. **Microservices over Monolith**: Better scalability and independent deployment
2. **PostgreSQL for both services**: Proven reliability, ACID compliance
3. **SQS for async processing**: Decouples payment initiation from processing
4. **React with Vite**: Fast development experience, modern tooling
5. **Tailwind CSS**: Utility-first, rapid UI development
6. **No authentication initially**: Simplifies POC, can be added later
7. **Mock payment gateway**: Avoids external dependencies during development
8. **Resilience4j**: Production-grade fault tolerance
9. **Structured logging**: Better observability in production
10. **Correlation IDs**: End-to-end request tracing
