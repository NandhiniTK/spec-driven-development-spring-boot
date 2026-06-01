# Project Roadmap

## Phase 1: Foundation Setup (Week 1)

- [x] Create project specification documents
  - [x] composition.md
  - [x] roadmap.md
  - [x] techstack.md
- [x] Initialize Application Management Service from Spring Initializr
- [x] Set up version control (Git)
- [x] Push to GitHub
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

---

# Payment Gateway Service

## Phase 5: Foundation & Setup (Week 6-7)

### Week 6: Service Initialization
- [ ] Create payment-gateway-service from Spring Initializr
  - Group: `com.nandhini.poc`, Artifact: `payment-gateway-service`
  - Dependencies: Web, JPA, PostgreSQL, Validation, Lombok, Actuator, Security
- [ ] Create database: `paymentgatewaydb`
- [ ] Configure application.yml (port: 8081)
- [ ] Add AWS SDK for SQS
- [ ] Set up project structure and packages

### Week 7: Core Entities & Repositories
- [ ] Create Payment entity
  - Fields: id (UUID), idempotencyKey, userId, amount, currency, paymentMethod, status, gatewayTransactionId, metadata (JSONB), version (@Version), timestamps
  - Enums: PaymentStatus (PENDING, PROCESSING, SUCCESS, FAILED, TIMEOUT), PaymentMethod (CARD, UPI, WALLET, NET_BANKING)
- [ ] Create PaymentTransaction entity (event sourcing/audit trail)
- [ ] Create IdempotencyKey entity (unique constraint on key)
- [ ] Create PaymentRepository, PaymentTransactionRepository, IdempotencyKeyRepository
- [ ] Create DTOs (PaymentRequest, PaymentResponse, PaymentStatusResponse)
- [ ] Create MapStruct mapper

## Phase 6: Payment Initiation API (Week 8)

- [ ] Create PaymentService interface and implementation
- [ ] Implement idempotency check (PostgreSQL-based)
  - Check/insert idempotency key with unique constraint
  - Return cached response if key exists
- [ ] Implement payment initiation logic
  - Validate request (amount > 0, valid payment method)
  - Create Payment record with PENDING status
  - Store idempotency key with payment reference
- [ ] Create PaymentController with endpoints:
  - POST /api/v1/payments (with @RequestHeader("Idempotency-Key"))
  - GET /api/v1/payments/{id}
  - GET /api/v1/payments (paginated)
- [ ] Add input validation (@Valid, custom validators)
- [ ] Create exception classes (PaymentNotFoundException, DuplicatePaymentException, InvalidPaymentException)
- [ ] Create GlobalExceptionHandler
- [ ] Add Swagger/OpenAPI annotations

## Phase 7: SQS Integration & Async Processing (Week 9)

### SQS Producer
- [ ] Add AWS SQS dependencies (spring-cloud-aws-messaging)
- [ ] Configure SQS client (LocalStack for local dev, AWS SQS for prod)
- [ ] Create SQSMessagePublisher service
- [ ] Publish payment message to SQS after payment creation
- [ ] Add retry logic for SQS publish failures

### SQS Consumer
- [ ] Create PaymentProcessorService with @SqsListener
- [ ] Implement message consumption logic
  - Fetch payment from DB
  - Validate payment status (skip if already processed)
  - Acquire pessimistic lock (SELECT FOR UPDATE)
- [ ] Create PaymentMethodHandler interface
- [ ] Implement handlers for each payment method:
  - CardPaymentHandler
  - UpiPaymentHandler
  - WalletPaymentHandler
  - NetBankingPaymentHandler
- [ ] Update payment status based on processing result
- [ ] Handle SQS message acknowledgment/deletion

## Phase 8: External Gateway Integration (Week 10)

### Payment Gateway Clients
- [ ] Create PaymentGatewayClient interface
- [ ] Implement mock gateway client for testing
- [ ] Implement Stripe client (or Razorpay)
  - Add Stripe SDK dependency
  - Configure API keys (externalized)
  - Implement charge/payment intent API calls
- [ ] Add Resilience4j for fault tolerance
  - Circuit Breaker configuration
  - Retry policy (exponential backoff)
  - Timeout configuration
  - Fallback logic
- [ ] Create WebhookController for payment gateway callbacks
  - POST /api/v1/webhooks/payment
  - Verify webhook signature
  - Update payment status based on callback
- [ ] Handle gateway response mapping
- [ ] Store gateway transaction ID and metadata

## Phase 9: Concurrency & Consistency (Week 11)

### Optimistic Locking
- [ ] Implement @Version on Payment entity
- [ ] Add retry logic for OptimisticLockException
- [ ] Test concurrent payment updates

### Idempotency Enhancements
- [ ] Add TTL/expiry to idempotency keys (24 hours)
- [ ] Create scheduled job to cleanup expired keys
- [ ] Add idempotency key validation (format, length)

### Transaction Management
- [ ] Ensure @Transactional boundaries are correct
- [ ] Implement transactional outbox pattern (optional)
- [ ] Add database indexes:
  - idx_payment_idempotency_key (unique)
  - idx_payment_user_id_created_at
  - idx_payment_status
  - idx_idempotency_expires_at

### Rate Limiting
- [ ] Implement database-based rate limiting
- [ ] Add rate limit per user (e.g., 10 payments/minute)
- [ ] Return 429 Too Many Requests when limit exceeded

## Phase 10: Advanced Features (Week 12)

### Refunds
- [ ] Create Refund entity (linked to Payment)
- [ ] Implement refund API: POST /api/v1/payments/{id}/refund
- [ ] Add refund processing logic
- [ ] Update payment status to REFUNDED
- [ ] Integrate with payment gateway refund API

### Payment Status Tracking
- [ ] Implement payment status polling endpoint
- [ ] Add WebSocket support for real-time status updates (optional)
- [ ] Create payment history endpoint

### Receipts & Invoices
- [ ] Generate payment receipt (PDF)
- [ ] Store receipts in database or S3
- [ ] Add endpoint: GET /api/v1/payments/{id}/receipt

### Pagination & Filtering
- [ ] Add pagination to GET /api/v1/payments
- [ ] Add filters: status, paymentMethod, dateRange, userId
- [ ] Implement sorting (by createdAt, amount)

## Phase 11: Security & Authentication (Week 13)

### JWT Authentication
- [ ] Add Spring Security configuration
- [ ] Implement JWT token generation and validation
- [ ] Create User entity and UserRepository
- [ ] Implement login/register endpoints
- [ ] Secure payment endpoints with @PreAuthorize
- [ ] Add user context to payment records

### Data Security
- [ ] Encrypt sensitive data (card details) at rest
- [ ] Mask sensitive data in logs
- [ ] Add security headers (CORS, CSP, HSTS)
- [ ] Implement API key authentication for webhooks
- [ ] Add request/response logging (excluding sensitive fields)

### PCI-DSS Considerations
- [ ] Never store full card numbers (use tokenization)
- [ ] Use HTTPS/TLS for all communications
- [ ] Implement secure key management
- [ ] Add audit logging for all payment operations

## Phase 12: Testing (Week 14)

### Unit Tests
- [ ] PaymentService tests (JUnit 5 + Mockito)
- [ ] PaymentMethodHandler tests
- [ ] IdempotencyService tests
- [ ] PaymentGatewayClient tests (mock external calls)
- [ ] Target: >85% code coverage

### Integration Tests
- [ ] PaymentController tests (@WebMvcTest + MockMvc)
- [ ] SQS integration tests (LocalStack)
- [ ] Database integration tests
- [ ] Webhook handler tests

### End-to-End Tests
- [ ] Complete payment flow test (initiation → processing → success)
- [ ] Idempotency test (duplicate requests)
- [ ] Concurrent payment test (optimistic locking)
- [ ] Payment failure scenarios
- [ ] Refund flow test

### Performance Tests
- [ ] Load testing with JMeter/Gatling
- [ ] Target: 1000 requests/sec, p95 < 500ms
- [ ] Stress testing for concurrent payments
- [ ] SQS throughput testing

### Add JaCoCo
- [ ] Configure JaCoCo plugin in build.gradle
- [ ] Enforce >85% coverage
- [ ] Generate coverage reports

## Phase 13: Observability & Production Readiness (Week 15)

### Logging & Monitoring
- [ ] Configure structured logging (JSON format)
- [ ] Add correlation IDs for request tracing
- [ ] Implement custom metrics (Micrometer)
  - Payment success/failure rate
  - Payment processing time
  - SQS queue depth
  - Gateway API latency
- [ ] Add health checks (database, SQS, payment gateway)
- [ ] Configure alerts for critical failures

### Documentation
- [ ] Complete Swagger/OpenAPI documentation
- [ ] Create payment-gateway-api.md specification
- [ ] Update README with payment service details
- [ ] Document payment flow diagrams
- [ ] Create runbook for common issues

### Deployment Preparation
- [ ] Create Dockerfile
- [ ] Add environment-specific configurations (dev, staging, prod)
- [ ] Configure AWS SQS (production)
- [ ] Set up database migrations (Flyway)
- [ ] Create deployment scripts

### Final Checks
- [ ] All tests passing
- [ ] Code review and refactoring
- [ ] Security audit
- [ ] Performance benchmarking
- [ ] Update IMPLEMENTATION_CHECKLIST.md

---

## Application Management Service - Future Enhancements

### Features
- [ ] Search and filtering
- [ ] Pagination and sorting
- [ ] Application status management
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

### Application Management Service
- API response time < 200ms (95th percentile)
- Test coverage > 80% ✅ (93% achieved)
- Complete API documentation ✅
- Clean, maintainable code ✅

### Payment Gateway Service
- API response time < 500ms (95th percentile)
- Throughput: 1000+ payments/sec
- Test coverage > 85%
- Zero duplicate payments (idempotency)
- 99.9% uptime
- PCI-DSS compliance considerations implemented
