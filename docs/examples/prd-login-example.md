# Example: Login Feature PRD (Filled Template)

**Feature Name:** User Authentication (Login)
**Status:** Approved
**Owner:** Product Manager

## 1. Problem Statement
Users need a secure way to access their accounts. Without authentication, all data is publicly accessible, creating privacy and security risks.

## 2. Goals & Success Metrics
| Metric Type | Metric Name | Target |
| :--- | :--- | :--- |
| **Product** | Login Success Rate | > 95% |
| **Technical** | P95 Login Latency | < 500ms |
| **Technical** | Failed Login → Account Lock | After 5 attempts |

## 3. User Stories
- As a **registered user**, I want to **log in with email and password** so that **I can access my account securely**.
- As a **user who forgot their password**, I want to **reset it via email** so that **I can regain access**.

## 4. Functional Requirements

### A. Frontend (UI/UX)
- [x] **Input State**: Email field (text) + Password field (masked) + "Log In" button.
- [x] **Loading State**: Button shows spinner, inputs disabled during request.
- [x] **Error State**: Inline error message below form ("Invalid email or password").
- [x] **Success State**: Redirect to `/dashboard`.

### B. Backend (API)
- [x] **Endpoint**: `POST /api/v1/auth/login`
- [x] **Input**: `{ "email": "string", "password": "string" }`
- [x] **Output**: `{ "access_token": "string", "refresh_token": "string" }`
- [x] **Error**: 401 Unauthorized with message "Invalid credentials"

### C. AI / Intelligence
- Not applicable for this feature.

## 5. Data & Privacy
- **Data Attributes**: Email (PII), Hashed Password, Login Timestamp, IP Address
- **Retention**: Login logs retained for 90 days.
- **Encryption**: Passwords hashed with bcrypt (cost factor 12).

## 6. Risk Mitigation (Fallbacks)
| Risk | Fallback Strategy |
| :--- | :--- |
| Brute Force Attack | Rate limit: 5 attempts per 15 min, then lock for 30 min |
| Token Theft | Short-lived access tokens (15 min), refresh via HttpOnly cookie |
| SQL Injection | Parameterized queries via ORM |

## 7. Golden Dataset (QA)
1. **Input**: `{"email": "user@example.com", "password": "correct"}` → **Output**: 200 + tokens
2. **Input**: `{"email": "user@example.com", "password": "wrong"}` → **Output**: 401
3. **Input**: `{"email": "", "password": ""}` → **Output**: 422 validation error
