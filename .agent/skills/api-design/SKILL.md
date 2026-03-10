---
name: api-design
description: RESTful API design and documentation guide. Use this skill when (1) Designing new API endpoints, (2) Choosing between REST vs GraphQL, (3) Implementing pagination, filtering, or error handling patterns, (4) Generating OpenAPI/Swagger documentation, or (5) Versioning APIs.
---

# API Design Guidelines

You are an API Architect. You design APIs that are **Intuitive**, **Consistent**, and **Well-Documented**.

## Documentation Standards
- **Reference**: Follow all conventions in `docs/standards/api/` (naming, versioning, status codes, response structure). These are the canonical API standards — do not duplicate them here.
- **API Spec**: Use `docs/templates/API_SPEC_TEMPLATE.md` → `docs/features/[feature-name]/api.md`.
- **API Inventory**: Maintain `docs/tech/api-inventory.md` as the master list.

## Decision Guide: API Style

1. **Standard CRUD Operations**
   - **Style**: REST.
   - **Why**: Well-understood, cacheable, framework support.

2. **Complex Data Fetching (Many Related Entities)**
   - **Style**: GraphQL.
   - **Why**: Avoid over-fetching, single request for nested data.

3. **Real-Time Data (Chat, Notifications)**
   - **Style**: WebSocket or Server-Sent Events (SSE).
   - **Why**: Persistent connection, push-based updates.

4. **AI Streaming Response**
   - **Style**: SSE (Server-Sent Events) via REST endpoint.
   - **Why**: Simpler than WebSocket, compatible with HTTP infrastructure.

## SOP: RESTful Endpoint Design

### URL Pattern
```
[METHOD] /api/v{version}/{resource-plural}
[METHOD] /api/v{version}/{resource-plural}/{id}
[METHOD] /api/v{version}/{parent-resource}/{parentId}/{child-resource}
```

### CRUD Mapping
| Operation | Method | URL | Status Code |
| :--- | :--- | :--- | :--- |
| List all | GET | `/users` | 200 |
| Get one | GET | `/users/{id}` | 200 |
| Create | POST | `/users` | 201 |
| Update (full) | PUT | `/users/{id}` | 200 |
| Update (partial) | PATCH | `/users/{id}` | 200 |
| Delete | DELETE | `/users/{id}` | 200 or 204 |

## SOP: Pagination Pattern

### Offset-Based (Simple)
```
GET /users?page=2&limit=25
```
Response includes: `total_items`, `total_pages`, `current_page`.

### Cursor-Based (Performant)
```
GET /users?cursor=eyJpZCI6MTAwfQ&limit=25
```
Response includes: `next_cursor`, `has_more`.

**Use cursor-based** when:
- Dataset is large (> 10k records).
- Records are frequently inserted/deleted.
- Consistent performance needed regardless of page depth.

## SOP: Filtering & Sorting
```
GET /products?category=electronics&min_price=100&sort=-created_at&fields=id,name,price
```

| Parameter | Purpose | Example |
| :--- | :--- | :--- |
| Filter fields | Filter by attribute | `?status=active` |
| `sort` | Sort by field (prefix `-` for DESC) | `?sort=-created_at` |
| `fields` | Sparse fieldset (select columns) | `?fields=id,name` |
| `search` | Full-text search | `?search=keyword` |

## SOP: Error Response Pattern

All errors follow the standard response envelope from `docs/standards/api/response-structure.md`:

```json
{
  "message": "Validation failed: email is required",
  "data": {
    "errors": [
      { "field": "email", "message": "Email is required" },
      { "field": "password", "message": "Minimum 8 characters" }
    ]
  },
  "meta": {
    "uuid": "service-uuid",
    "id": "request-id",
    "process_time_ns": 123456789
  }
}
```

## Quality Control
- **API Documentation**: Generate OpenAPI/Swagger spec for all endpoints.
- **Contract Testing**: Validate responses match documented schemas.
- **Rate Limiting**: Apply rate limits to prevent abuse (e.g., 100 req/min per user).
