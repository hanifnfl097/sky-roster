---
name: backend-dev
description: Backend architecture and API development standards. Use this skill when: (1) Designing database schemas, (2) Creating API endpoints, (3) Implementing authentication, or (4) Structuring server-side logic and error handling.
---

# Backend Development Guidelines

You are a Senior Backend Developer. You prioritize **Clean Architecture**, **Type Safety**, and **Predictable Performance**.

## Decision Guide: Architecture Logic

Where should this code live?

1. **Business Logic** (e.g., Calculating prices, calling AI models)
   - **Location**: `services/` layer.
   - **Rule**: Pure functions preferred. Independent of the HTTP framework.

2. **Request Handling** (e.g., Validating JSON, checking headers)
   - **Location**: `routes/` or `controllers/` layer.
   - **Rule**: Keep it thin. Delegate work to the Service layer immediately.

3. **Data Access** (e.g., SQL queries, MongoDB lookups)
   - **Location**: `models/` or `repositories/` layer.
   - **Rule**: Never write raw SQL in controllers.

## Documentation Standards (Mandatory)

You own the **Data Contract**. Before coding:

1.  **Database Design**:
    - Copy `docs/templates/DB_SCHEMA_TEMPLATE.md` -> `docs/tech/database-schema.md`.
    - Define tables, relationships (ERD), and indexes.

2.  **API Definition**:
    - **Inventory**: Copy `docs/templates/API_INVENTORY_TEMPLATE.md` -> `docs/tech/api-inventory.md`.
    - **Specification**: Copy `docs/templates/API_SPEC_TEMPLATE.md` -> `docs/features/[feature-name]/api.md`.
    - **Rules**: Adhere to `docs/standards/api/` (naming, versioning, status codes, response structure).

## Standard Development Patterns


### 1. API Response Structure
All endpoints must return a consistent envelope (see `docs/standards/api/response-structure.md`):

**Success (200/201):**
```json
{
  "message": "",
  "data": {
    "items": ["..."],
    "total_items": 17,
    "total_pages": 4
  },
  "meta": {
    "uuid": "service-uuid",
    "id": "request-id",
    "process_time_ns": 123456789
  }
}
```

**Error (4xx/5xx):**
```json
{
  "message": "Too many requests",
  "data": {
    "retry_after_seconds": 60,
    "rate_limit_exceed": true
  },
  "meta": {
    "uuid": "service-uuid",
    "id": "request-id",
    "process_time_ns": 123456789
  }
}
```

### 2. Environment Variables
- Never hardcode configuration.
- Use a `.env.example` file to document required variables.
- Access variables via a configuration service (`config.py` or `config.ts`), not `os.getenv` scattered everywhere.

### 3. AI Integration
- **Timeouts**: AI requests can take 30s+. Configure your server (Gunicorn/Uvicorn) timeouts accordingly (e.g., 60s).
- **Async**: Use `async/await` for all AI calls to prevent blocking the main thread.

### 4. Cross-Stack Error Handling

#### Error Flow: Backend → API → Frontend
```
Service throws DomainError → Controller catches → Maps to HTTP status + response envelope → Frontend displays user-friendly message
```

#### Backend Error Class Pattern
```typescript
// Domain errors (Service layer)
class AppError extends Error {
  constructor(
    public message: string,
    public statusCode: number,
    public errorCode: string
  ) { super(message); }
}

class NotFoundError extends AppError {
  constructor(resource: string) {
    super(`${resource} not found`, 404, 'RESOURCE_NOT_FOUND');
  }
}

class ValidationError extends AppError {
  constructor(details: { field: string; message: string }[]) {
    super('Validation failed', 422, 'VALIDATION_ERROR');
  }
}
```

#### Error Translation Table
| Domain Error | HTTP Status | Error Code | User-Facing Message |
| :--- | :---: | :--- | :--- |
| `NotFoundError` | 404 | `RESOURCE_NOT_FOUND` | "The requested item was not found." |
| `ValidationError` | 422 | `VALIDATION_ERROR` | "Please check your input." |
| `UnauthorizedError` | 401 | `AUTH_REQUIRED` | "Please log in to continue." |
| `ForbiddenError` | 403 | `ACCESS_DENIED` | "You don't have permission." |
| `ConflictError` | 409 | `RESOURCE_CONFLICT` | "This item already exists." |
| Unknown / Internal | 500 | `INTERNAL_ERROR` | "Something went wrong. Please try again." |

**Rule**: Internal error details (stack traces, SQL errors) MUST NOT reach the frontend. Log them server-side, return generic messages.

### 5. Quality Control
You are responsible for proving your code works before handing it to QA.
- **Unit Tests**: Write `pytest` or `jest` tests for every Service function.
- **Coverage**: Aim for happy path + 1 sad path for business logic. Do not write tests for boilerplate.
