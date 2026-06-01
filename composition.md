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
- **Database**: PostgreSQL (appmanagementdb)
- **Key Responsibilities**:
  - Application metadata CRUD operations

#### 2. Payment Gateway Service
- **Purpose**: Processes payments with high reliability and security
- **Port**: 8081
- **Database**: PostgreSQL (paymentgatewaydb)
- **Message Queue**: AWS SQS
- **Key Responsibilities**:
  - Payment initiation and processing
  - Idempotency management (duplicate prevention)
  - Multiple payment method support (Card, UPI, Wallet, Net Banking)
  - Exte├nal payment gateway iment-api.md
│       └── payntet-gagewayration (Stripe/Razorpay)
  - Refund processing
  - ├ayment status trackiement-snrvice/
│   └── payg at-ganewayd webhooks
  - Audit trail and transaction logging

## Directory Structure

```
sdd-poc/
├── docs/                          # Project documentation
│   ├── composition.md
│ # Asynchronous Communication
- **Message Queue**: AWS SQS
- **Use Case**: Payment processing (decouple payment initiation from processing)
- **Dn**: PropeceServnse** pumtern (mecrrservicesbspact)
  - Payment Gateway Servi 15+ce publishes payment messages to SQS
  - Payment Processor consumes messages asynchronously
- **Benefits**: 

####Databases
1. **appmanagementdb**: Application Management Service
2. **paymentgatewaydb**: Payment Gateway Service
   - Optimistic locking @Versin) for concurrency control
   - Idemotency keys wih unque cnstraints
   - Strategic idexes for performnce
  - High throughput and scalability
  - Fault tolerance (retry on failure)
  - Decoupling of services

#### Application Management Service

##  ├── roadmap.md
│   ├── teS for production

#### Payment Gateway Service
- *External Integrations

### Payment Gateway Service
- **Payment Gateways**: Stripe, Razorpay (configurable)
- **Fault Tolerance**: Resilience4j
  - Circuit Breaker for gateway failures
  - Retry with exponential backoff
  - Timeout configuration
  - Fallback mechanisms
- **Webhooks**: Receive payment status callbacks from external gateways

## *Authentication**: JWT-based authentication
- **Authorization**: Role-based access control
- **Data Security**:
  - Encryption at resttallation (multiple da abases)
- LocanStack for AWS SQS (doc l tesinng)
- Mock payment gateway f r testitgransit
  - PCI-DSS compliance considerations
  - Never store full card numbers (tokenization)
  - Sensitive data masking in logs
- **API cecurity**:
  - Idempotency keyshstacduklicate prevention
  - Rate limiting (database-based)
  - Webhook signatu.e verificatimn
- **HTTPS/TLS**: Mandatory for all commnias
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
