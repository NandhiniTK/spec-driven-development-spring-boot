# Application Management API Specification

## Base URL
```
http://localhost:8080/api/v1
```

## Overview
The Application Management Service provides CRUD operations for managing application metadata.

## Endpoints

### 1. Create Application

**POST** `/applications`

**Request Body:**
```json
{
  "name": "string",
  "description": "string",
  "version": "string",
  "status": "ACTIVE | INACTIVE | MAINTENANCE",
  "owner": "string",
  "technology": "string",
  "environment": "DEV | TEST | STAGING | PROD",
  "url": "string",
  "metadata": {
    "key1": "value1",
    "key2": "value2"
  }
}
```

**Response:** `201 Created`
```json
{
  "id": "long",
  "name": "string",
  "description": "string",
  "version": "string",
  "status": "string",
  "owner": "string",
  "technology": "string",
  "environment": "string",
  "url": "string",
  "metadata": {},
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input or application name already exists
- `500 Internal Server Error` - Server error

---

### 2. Get Application by ID

**GET** `/applications/{id}`

**Path Parameters:**
- `id` (long) - Application ID

**Response:** `200 OK` — Same structure as Create response

**Error Responses:**
- `404 Not Found` - Application not found

---

### 3. Get All Applications

**GET** `/applications`

**Response:** `200 OK`
```json
[
  {
    "id": "long",
    "name": "string",
    "description": "string",
    "version": "string",
    "status": "string",
    "owner": "string",
    "technology": "string",
    "environment": "string",
    "url": "string",
    "metadata": {},
    "createdAt": "timestamp",
    "updatedAt": "timestamp"
  }
]
```

---

### 4. Update Application

**PUT** `/applications/{id}`

Full update of an existing application.

**Path Parameters:**
- `id` (long) - Application ID

**Request Body:** Same structure as Create request

**Response:** `200 OK` — Same structure as Create response

**Error Responses:**
- `400 Bad Request` - Invalid input
- `404 Not Found` - Application not found

---

### 5. Delete Application

**DELETE** `/applications/{id}`

**Path Parameters:**
- `id` (long) - Application ID

**Response:** `204 No Content`

**Error Responses:**
- `404 Not Found` - Application not found

---

### 6. Health Check

**GET** `/actuator/health`

**Response:** `200 OK`
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## Data Model

### Application
```json
{
  "id": "long (auto-generated)",
  "name": "string (required, unique, 3-100 chars)",
  "description": "string (optional, max 500 chars)",
  "version": "string (required, pattern: x.y.z)",
  "status": "enum (ACTIVE, INACTIVE, MAINTENANCE)",
  "owner": "string (required, 1-100 chars)",
  "technology": "string (optional, 1-50 chars)",
  "environment": "enum (DEV, TEST, STAGING, PROD)",
  "url": "string (optional, valid URL)",
  "metadata": "JSON object (optional, key-value pairs)",
  "createdAt": "timestamp (auto-generated)",
  "updatedAt": "timestamp (auto-updated)"
}
```

### Enums

**Status:** ACTIVE, INACTIVE, MAINTENANCE

**Environment:** DEV, TEST, STAGING, PROD

---

## Validation Rules

| Field | Required | Constraints |
|-------|----------|-------------|
| name | Yes | Unique, 3-100 chars, alphanumeric/hyphens/underscores |
| version | Yes | Semantic versioning (x.y.z) |
| owner | Yes | 1-100 chars |
| description | No | Max 500 chars |
| url | No | Valid URL format |
| technology | No | 1-50 chars |
| metadata | No | JSON object, max 20 key-value pairs |

---

## Error Response Format

```json
{
  "timestamp": "2026-05-15T08:20:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'name': must not be blank",
  "path": "/api/v1/applications"
}
```

### HTTP Status Codes
- `200 OK` - Successful GET, PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Invalid input or validation error
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Example

**Create Application:**
```bash
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Content-Type: application/json" \
  -d '{
    "name": "payment-service",
    "description": "Payment processing microservice",
    "version": "1.0.0",
    "status": "ACTIVE",
    "owner": "john.doe",
    "technology": "Java Spring Boot",
    "environment": "PROD",
    "url": "https://payment.example.com",
    "metadata": {
      "team": "payments",
      "cost-center": "CC-1234"
    }
  }'
```

---

## Notes

1. All timestamps are in ISO 8601 format (UTC)
2. The `metadata` field allows flexible key-value storage for custom attributes
3. Application names must be unique
4. DELETE permanently removes the application
5. No authentication initially — to be added in a future phase
