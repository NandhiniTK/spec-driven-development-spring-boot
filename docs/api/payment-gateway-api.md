# Payment Gateway API Specification

## Base URL
```
http://localhost:8081/api/v1
```

## Overview
The Payment Gateway Service provides secure, scalable payment processing with support for multiple payment methods. It ensures idempotency, consistency, and PCI-DSS compliance considerations.

## Authentication
All payment endpoints require JWT authentication (except webhooks which use API key verification).

**Header:**
```
Authorization: Bearer <jwt_token>
```

---

## Endpoints

### 1. Initiate Payment

**POST** `/payments`

Creates a new payment request. Idempotency is enforced via the `Idempotency-Key` header.

**Headers:**
```
Authorization: Bearer <jwt_token>
Idempotency-Key: <unique-key>  (required, max 255 chars)
```

**Request Body:**
```json
{
  "amount": 10000,
  "currency": "INR",
  "paymentMethod": "CARD | UPI | WALLET | NET_BANKING",
  "metadata": {
    "orderId": "ORD-12345",
    "customerId": "CUST-67890",
    "description": "Order payment"
  }
}
```

**Response:** `202 Accepted`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-123",
  "amount": 10000,
  "currency": "INR",
  "paymentMethod": "CARD",
  "status": "PENDING",
  "metadata": {
    "orderId": "ORD-12345",
    "customerId": "CUST-67890",
    "description": "Order payment"
  },
  "createdAt": "2026-06-01T14:30:00.000Z"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input or duplicate idempotency key with different payload
- `401 Unauthorized` - Missing or invalid JWT token
- `429 Too Many Requests` - Rate limit exceeded

---

### 2. Get Payment by ID

**GET** `/payments/{id}`

Retrieves payment details by payment ID.

**Path Parameters:**
- `id` (UUID) - Payment ID

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-123",
  "amount": 10000,
  "currency": "INR",
  "paymentMethod": "CARD",
  "status": "SUCCESS",
  "gatewayTransactionId": "ch_3NqZ8KLkdIwHu7ix0B3n0W8Z",
  "metadata": {
    "orderId": "ORD-12345",
    "customerId": "CUST-67890",
    "description": "Order payment"
  },
  "createdAt": "2026-06-01T14:30:00.000Z",
  "updatedAt": "2026-06-01T14:30:15.000Z"
}
```

**Error Responses:**
- `404 Not Found` - Payment not found
- `401 Unauthorized` - Missing or invalid JWT token

---

### 3. List Payments

**GET** `/payments`

Retrieves a paginated list of payments for the authenticated user.

**Query Parameters:**
- `page` (int, default: 0) - Page number
- `size` (int, default: 20, max: 100) - Page size
- `status` (string, optional) - Filter by status (PENDING, PROCESSING, SUCCESS, FAILED, TIMEOUT, REFUNDED)
- `paymentMethod` (string, optional) - Filter by payment method
- `fromDate` (ISO 8601, optional) - Filter payments from date
- `toDate` (ISO 8601, optional) - Filter payments to date
- `sort` (string, default: createdAt,desc) - Sort field and direction

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "amount": 10000,
      "currency": "INR",
      "paymentMethod": "CARD",
      "status": "SUCCESS",
      "createdAt": "2026-06-01T14:30:00.000Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalPages": 5,
  "totalElements": 100,
  "last": false,
  "first": true
}
```

---

### 4. Refund Payment

**POST** `/payments/{id}/refund`

Initiates a refund for a successful payment.

**Path Parameters:**
- `id` (UUID) - Payment ID

**Request Body:**
```json
{
  "amount": 10000,
  "reason": "Customer requested refund"
}
```

**Response:** `200 OK`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 10000,
  "status": "PROCESSING",
  "reason": "Customer requested refund",
  "createdAt": "2026-06-01T15:00:00.000Z"
}
```

**Error Responses:**
- `400 Bad Request` - Payment not eligible for refund (not SUCCESS status)
- `404 Not Found` - Payment not found
- `422 Unprocessable Entity` - Refund amount exceeds payment amount

---

### 5. Get Payment Receipt

**GET** `/payments/{id}/receipt`

Downloads payment receipt as PDF.

**Path Parameters:**
- `id` (UUID) - Payment ID

**Response:** `200 OK`
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="receipt-{paymentId}.pdf"`

**Error Responses:**
- `404 Not Found` - Payment not found or receipt not available
- `400 Bad Request` - Receipt only available for successful payments

---

### 6. Payment Webhook (External Gateway Callback)

**POST** `/webhooks/payment`

Receives payment status updates from external payment gateways (Stripe, Razorpay).

**Headers:**
```
X-Webhook-Signature: <hmac-signature>
```

**Request Body:** (Gateway-specific format)
```json
{
  "event": "payment.success",
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "gatewayTransactionId": "ch_3NqZ8KLkdIwHu7ix0B3n0W8Z",
  "status": "SUCCESS",
  "timestamp": "2026-06-01T14:30:15.000Z"
}
```

**Response:** `200 OK`
```json
{
  "received": true
}
```

**Error Responses:**
- `400 Bad Request` - Invalid signature or payload
- `404 Not Found` - Payment not found

---

### 7. Health Check

**GET** `/actuator/health`

**Response:** `200 OK`
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "sqs": {
      "status": "UP"
    },
    "paymentGateway": {
      "status": "UP"
    }
  }
}
```

---

## Data Models

### Payment
```json
{
  "id": "UUID (auto-generated)",
  "userId": "string (from JWT token)",
  "amount": "number (required, > 0, in smallest currency unit)",
  "currency": "string (required, ISO 4217 code, e.g., INR, USD)",
  "paymentMethod": "enum (CARD, UPI, WALLET, NET_BANKING)",
  "status": "enum (PENDING, PROCESSING, SUCCESS, FAILED, TIMEOUT, REFUNDED)",
  "gatewayTransactionId": "string (from payment gateway)",
  "metadata": "object (optional, key-value pairs)",
  "createdAt": "timestamp (auto-generated)",
  "updatedAt": "timestamp (auto-updated)"
}
```

### Enums

**PaymentStatus:**
- `PENDING` - Payment initiated, awaiting processing
- `PROCESSING` - Payment being processed by gateway
- `SUCCESS` - Payment completed successfully
- `FAILED` - Payment failed
- `TIMEOUT` - Payment timed out
- `REFUNDED` - Payment refunded

**PaymentMethod:**
- `CARD` - Credit/Debit card
- `UPI` - Unified Payments Interface
- `WALLET` - Digital wallet (PayTM, PhonePe, etc.)
- `NET_BANKING` - Net banking

**Currency:**
- `INR` - Indian Rupee
- `USD` - US Dollar
- (Extensible for other currencies)

---

## Validation Rules

| Field | Required | Constraints |
|-------|----------|-------------|
| amount | Yes | > 0, max 10 digits |
| currency | Yes | Valid ISO 4217 code (INR, USD) |
| paymentMethod | Yes | One of: CARD, UPI, WALLET, NET_BANKING |
| metadata | No | Max 20 key-value pairs, each key/value max 255 chars |
| Idempotency-Key | Yes | Unique, 1-255 chars, alphanumeric + hyphens |

---

## Error Response Format

```json
{
  "timestamp": "2026-06-01T14:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: amount must be greater than 0",
  "path": "/api/v1/payments"
}
```

### HTTP Status Codes
- `200 OK` - Successful GET, PUT
- `202 Accepted` - Payment initiated (async processing)
- `400 Bad Request` - Invalid input or validation error
- `401 Unauthorized` - Missing or invalid authentication
- `404 Not Found` - Resource not found
- `422 Unprocessable Entity` - Business logic error
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error

---

## Idempotency

All payment creation requests must include an `Idempotency-Key` header. If the same key is used within 24 hours:
- **Same payload**: Returns the cached response (200 OK with original payment)
- **Different payload**: Returns 400 Bad Request

**Example:**
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Authorization: Bearer <token>" \
  -H "Idempotency-Key: payment-20260601-001" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "currency": "INR",
    "paymentMethod": "CARD",
    "metadata": {
      "orderId": "ORD-12345"
    }
  }'
```

---

## Rate Limiting

- **Per User**: 10 payment requests per minute
- **Response**: `429 Too Many Requests` with `Retry-After` header

---

## Security

### Authentication
- JWT tokens required for all endpoints (except webhooks)
- Token expiry: 1 hour
- Refresh token: 7 days

### Data Security
- **Never store full card numbers** - Use payment gateway tokenization
- **Encryption**: AES-256 for sensitive data at rest
- **TLS 1.3**: All communications encrypted
- **Webhook Verification**: HMAC-SHA256 signature validation

### PCI-DSS Compliance
- Card data handled by PCI-compliant payment gateways (Stripe, Razorpay)
- No card data stored in application database
- Secure logging (sensitive data masked)

---

## Async Processing Flow

1. **Client** → POST /payments → **Payment Service**
2. **Payment Service** → Create PENDING payment → **Database**
3. **Payment Service** → Publish message → **SQS Queue**
4. **Payment Service** → Return 202 Accepted → **Client**
5. **SQS Consumer** → Fetch payment → **Database**
6. **SQS Consumer** → Process payment → **Payment Gateway (Stripe/Razorpay)**
7. **Payment Gateway** → Return result → **SQS Consumer**
8. **SQS Consumer** → Update status (SUCCESS/FAILED) → **Database**
9. **Payment Gateway** → Webhook callback → **Payment Service**
10. **Payment Service** → Verify & update → **Database**

---

## Example Usage

### Create Payment
```bash
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Idempotency-Key: payment-$(date +%s)" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "currency": "INR",
    "paymentMethod": "UPI",
    "metadata": {
      "orderId": "ORD-98765",
      "customerId": "CUST-12345",
      "description": "Premium subscription"
    }
  }'
```

### Get Payment Status
```bash
curl -X GET http://localhost:8081/api/v1/payments/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Refund Payment
```bash
curl -X POST http://localhost:8081/api/v1/payments/550e8400-e29b-41d4-a716-446655440000/refund \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000,
    "reason": "Customer cancellation"
  }'
```

---

## Notes

1. All timestamps are in ISO 8601 format (UTC)
2. Amounts are in smallest currency unit (paise for INR, cents for USD)
3. Payment processing is asynchronous - use webhooks or polling for status updates
4. Idempotency keys expire after 24 hours
5. Rate limits are per user, enforced at database level
6. All payment operations are logged for audit trail
7. Circuit breaker protects against payment gateway failures
