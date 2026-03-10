# Example: Auth API Spec (Filled Template)

**Service Name:** Auth Service
**Version:** v1

## 1. Overview
Authentication service handling user login, token refresh, and logout. Uses JWT (Access + Refresh tokens) for session management.

## 2. Endpoints

### [POST] /api/v1/auth/login
**Description**: Authenticate user with email and password, return JWT tokens.

**Auth Required**: No

**Request Body (Schema)**:
```json
{
  "email": "string (required, format: email)",
  "password": "string (required, min: 8 chars)"
}
```

**Response (Success - 200)**:
```json
{
  "message": "Login successful",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
    "token_type": "Bearer",
    "expires_in": 900
  },
  "meta": {
    "uuid": "auth-service-001",
    "id": "req-abc123",
    "process_time_ns": 45000000
  }
}
```

**Response (Error - 401)**:
```json
{
  "message": "Invalid email or password",
  "data": null,
  "meta": {
    "uuid": "auth-service-001",
    "id": "req-abc123",
    "process_time_ns": 12000000
  }
}
```

---

### [POST] /api/v1/auth/refresh
**Description**: Refresh access token using a valid refresh token.

**Auth Required**: Yes (Refresh Token in HttpOnly Cookie)

**Request Body (Schema)**:
```json
{}
```
*Note: Refresh token is sent automatically via HttpOnly cookie.*

**Response (Success - 200)**:
```json
{
  "message": "Token refreshed",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "expires_in": 900
  },
  "meta": {
    "uuid": "auth-service-001",
    "id": "req-def456",
    "process_time_ns": 8000000
  }
}
```

---

### [POST] /api/v1/auth/logout
**Description**: Invalidate refresh token and clear session.

**Auth Required**: Yes (Bearer Token)

**Response (Success - 200)**:
```json
{
  "message": "Logout successful",
  "data": null,
  "meta": {
    "uuid": "auth-service-001",
    "id": "req-ghi789",
    "process_time_ns": 5000000
  }
}
```

## 3. AI Behavior (If applicable)
- **Streaming**: No
- **Timeout**: 10s
