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
- [x] Create payment-gateway-service from Spring Initializr
  - Group: `com.nandhini.poc`, Artifact: `payment-gateway-service`
  - Dependencies: Web, JPA, PostgreSQL, Validation, Lombok, Actuator, Security
- [x] Create database: `paymentgatewaydb` (manual creation required)
- [x] Configure application.yml (port: 8081)
- [x] Add AWS SDK for SQS
- [x] Set up project structure and packages

### Week 7: Core Entities & Repositories
- [x] Create Payment entity
  - Fields: id (UUID), userId, amount, currency, paymentMethod, status, gatewayTransactionId, metadata (JSONB), version (@Version), timestamps
  - Enums: PaymentStatus (PENDING, PROCESSING, SUCCESS, FAILED, TIMEOUT, REFUNDED), PaymentMethod (CARD, UPI, WALLET, NET_BANKING), Currency (INR, USD)
- [x] Create PaymentTransaction entity (event sourcing/audit trail)
- [x] Create IdempotencyKey entity (unique constraint on key)
- [x] Create PaymentRepository, PaymentTransactionRepository, IdempotencyKeyRepository
- [x] Create DTOs (PaymentRequestDTO, PaymentResponseDTO, RefundRequestDTO)
- [x] Create MapStruct mapper

## Phase 6: Payment Initiation API (Week 8)

- [x] Create PaymentService interface and implementation
- [x] Implement idempotency check (PostgreSQL-based)
  - Check/insert idempotency key with unique constraint
  - Return cached response if key exists (JSON serialization with Jackson)
- [x] Implement payment initiation logic
  - Validate request (amount > 0, valid payment method)
  - Create Payment record with PENDING status
  - Store idempotency key with payment reference (24h TTL)
- [x] Create PaymentController with endpoints:
  - POST /api/v1/payments (with @RequestHeader("Idempotency-Key"))
  - GET /api/v1/payments/{id}
  - GET /api/v1/payments (paginated with status filter)
- [x] Add input validation (@Valid, custom validators)
- [x] Create exception classes (PaymentNotFoundException, DuplicatePaymentException, InvalidPaymentException)
- [x] Create GlobalExceptionHandler
- [x] Add Swagger/OpenAPI annotations (OpenApiConfig + @Schema on DTOs)

## Phase 7: SQS Integration & Async Processing (Week 9)

### SQS Producer
- [x] Add AWS SQS dependencies (spring-cloud-aws-messaging + spring-retry)
- [x] Configure SQS client (AWS SQS for local and prod)
- [x] Create SQSMessagePublisher service
- [x] Publish payment message to SQS after payment creation (paymentId only)
- [x] Add retry logic for SQS publish failures (@Retryable with exponential backoff)

### SQS Consumer
- [x] Create PaymentProcessorService with @SqsListener
- [x] Implement message consumption logic
  - Fetch payment from DB
  - Validate payment status (skip if already processed)
  - Pessimistic locking via @Transactional
- [x] Create PaymentMethodHandler interface
- [x] Implement mock handlers for each payment method:
  - CardPaymentHandler (80% success rate)
  - UpiPaymentHandler (85% success rate)
  - WalletPaymentHandler (90% success rate)
  - NetBankingPaymentHandler (75% success rate)
- [x] Update payment status based on processing result (SUCCESS/FAILED)
- [x] Handle SQS message acknowledgment/deletion (throw exception for DLQ on failure)
- [x] Log payment events to PaymentTransaction table (audit trail)

## Phase 8: External Gateway Integration (Week 10)

### Payment Gateway Clients
- [x] Create PaymentGatewayClient interface
- [x] Implement mock gateway client for testing (MockPaymentGatewayClient)
- [x] ~~Implement Stripe client~~ (Skipped - Mock sufficient for POC)
  - ~~Add Stripe SDK dependency~~
  - ~~Configure API keys (externalized)~~
  - ~~Implement charge/payment intent API calls~~
- [x] Add Resilience4j for fault tolerance (configured in application.yml)
  - Circuit Breaker (50% failure threshold, 60s wait, 10 calls window)
  - Retry policy (3 attempts, exponential backoff: 500ms, 1s, 2s)
  - Timeout configuration (10 seconds)
  - Fallback logic (returns TIMEOUT status)
- [x] Create WebhookController for payment gateway callbacks
  - POST /api/v1/webhooks/payment
  - Verify webhook signature (HMAC-SHA256)
  - Update payment status based on callback
- [x] Handle gateway response mapping (GatewayResponse DTO)
- [x] Store gateway transaction ID and metadata
- [x] Update payment handlers to use gateway client
- [x] Handle TIMEOUT status when circuit breaker opens

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
- [x] Configure structured logging (JSON format)
  - Logback with logstash-logback-encoder
  - Profile-based configuration (dev: human-readable, prod: JSON)
  - MDC support for correlation ID, userId, paymentId
- [x] Add correlation IDs for request tracing
  - CorrelationIdFilter for HTTP requests
  - X-Correlation-ID header support
  - Propagation through SQS messages
  - MDC cleanup in finally blocks
- [x] Implement custom metrics (Micrometer)
  - Payment success/failure/timeout counters
  - Payment processing time timer
  - Gateway API latency timer
  - Prometheus endpoint enabled
- [x] Add health checks (database, SQS, payment gateway)
  - Custom PaymentGatewayHealthIndicator
  - Custom SqsHealthIndicator with queue depth
  - Liveness and readiness probes
- [x] Configure alerts for critical failures (basic logging setup)

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

## Phase 14: React UI for Application Management (Week 16)

### Frontend Setup
- [ ] Initialize React app with Vite
- [ ] Set up project structure (components, services, utils)
- [ ] Configure Tailwind CSS for styling
- [ ] Add Axios for API calls
- [ ] Configure environment variables (.env for API base URL)

### Application UI Components
- [ ] Create Application Form Component
  - Application name input
  - Description textarea
  - Customer ID input
  - Submit button
  - Form validation (required fields)
  - Clear/Reset button
- [ ] Create Applications Table Component
  - Display all applications in table format
  - Columns: ID, Name, Description, Customer ID, Created Date, Updated Date
  - Responsive table design
  - Refresh button
  - Empty state message
- [ ] Create single-page layout
  - Header with title "Application Management"
  - Form section at top
  - Table section below
  - No authentication/login required

### API Integration
- [ ] Create API service layer (services/applicationService.js)
  - POST /api/applications (create application)
  - GET /api/applications (get all applications)
- [ ] Handle API errors and loading states
- [ ] Add toast notifications for success/error messages
- [ ] Implement auto-refresh after creating application

### UI/UX Features
- [ ] Responsive design (mobile-friendly)
- [ ] Loading spinner during API calls
- [ ] Success toast on application creation
- [ ] Error toast on API failures
- [ ] Disable submit button during submission
- [ ] Auto-clear form after successful submission
- [ ] Format dates in table (human-readable)

### Build & Deployment
- [ ] Create production build
- [ ] Configure CORS in Application Management Service backend
- [ ] Add README for frontend setup instructions
- [ ] Test with backend running on localhost:8080

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
