---
name: devops-deployment
description: DevOps, CI/CD, and deployment standards. Use this skill when (1) Setting up Docker/containers, (2) Configuring CI/CD pipelines (GitHub Actions, GitLab CI), (3) Managing environments (dev/staging/prod), (4) Creating deployment scripts, or (5) Setting up monitoring and observability.
---

# DevOps & Deployment Guidelines

You are a DevOps Engineer. You ensure applications are **Deployable**, **Reproducible**, and **Observable**.

## Documentation Standards
- **Deployment Guide**: Use `docs/templates/DEPLOYMENT_GUIDE_TEMPLATE.md` → `docs/tech/deployment-guide.md`.
- **Environment Config**: Use `docs/templates/ENV_CONFIG_TEMPLATE.md` → `docs/tech/env-config.md`.

## Decision Guide: Containerization

1. **Single Service App**
   - **Tool**: Single Dockerfile + Docker Compose for local dev.
   - **Why**: Simple, sufficient for most projects.

2. **Multi-Service App (Microservices)**
   - **Tool**: Docker Compose (dev) + Kubernetes or Cloud Run (prod).
   - **Why**: Service isolation and independent scaling.

3. **Static Frontend (SPA)**
   - **Tool**: Build locally → Deploy to CDN (Vercel, Netlify, Cloudflare Pages).
   - **Why**: No container needed, faster delivery.

## SOP: Dockerfile Best Practices

1. **Use Multi-Stage Builds**: Separate build and runtime stages.
2. **Pin Base Image Versions**: Use `node:20-alpine`, not `node:latest`.
3. **Non-Root User**: Always run as non-root in production.
4. **Layer Caching**: Copy dependency files first, then source code.
5. **.dockerignore**: Exclude `node_modules`, `.git`, `.env`, `dist/`.

### Example: Node.js Dockerfile
```dockerfile
# Build stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

# Runtime stage
FROM node:20-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
USER appuser
EXPOSE 3000
CMD ["node", "dist/main.js"]
```

## SOP: CI/CD Pipeline Structure

Every project should have a pipeline with these stages:

```
Install → Lint → Test → Build → Deploy
```

### GitHub Actions Template
```yaml
name: CI/CD Pipeline
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  ci:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      - run: npm ci
      - run: npm run lint
      - run: npm test
      - run: npm run build
```

## SOP: Environment Management

| Environment | Purpose | Branch | Auto-Deploy |
| :--- | :--- | :--- | :--- |
| **Development** | Local testing | `feature/*` | No |
| **Staging** | Integration testing | `develop` | Yes |
| **Production** | Live users | `main` | Manual approval |

### Rules
1. **Environment Parity**: Staging must mirror production config (same DB engine, same runtime).
2. **Feature Flags**: Use feature flags to deploy code without enabling features.
3. **Rollback Plan**: Every deployment must have a documented rollback procedure.

## SOP: Docker Compose for Local Dev

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "3000:3000"
    env_file: .env
    depends_on:
      - db
    volumes:
      - .:/app
      - /app/node_modules

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

## 12-Factor App Compliance

This skill follows [The Twelve-Factor App](https://12factor.net/) methodology:

| Factor | Principle | How We Apply It |
| :--- | :--- | :--- |
| I. Codebase | One repo, many deploys | Git workflow with `main`/`develop` branches |
| II. Dependencies | Explicitly declare | `package.json`, `requirements.txt`, lockfiles |
| III. Config | Store in environment | `.env` files, cloud secret managers |
| IV. Backing Services | Treat as attached resources | DB, Redis, S3 accessed via env URLs, swappable |
| V. Build, Release, Run | Strict separation | CI builds → versioned release → deploy |
| VI. Processes | Stateless | No local file storage in app process. Use external storage (S3, Redis) |
| VII. Port Binding | Export via port | `EXPOSE 3000`, self-contained HTTP server |
| VIII. Concurrency | Scale via process model | Horizontal scaling with load balancer, not vertical |
| IX. Disposability | Fast startup, graceful shutdown | Handle SIGTERM: finish requests, close DB connections, exit |
| X. Dev/Prod Parity | Keep environments similar | Same DB engine, same runtime in staging and prod |
| XI. Logs | Treat as event streams | Write to stdout/stderr, let log aggregator collect |
| XII. Admin Processes | Run as one-off tasks | Migrations, seeds, data fixes as separate scripts, not embedded |

## SOP: Logging Standards

### Log Levels
| Level | When to Use | Example |
| :--- | :--- | :--- |
| `ERROR` | Operation failed, requires attention | `Failed to connect to database` |
| `WARN` | Potential issue, but recoverable | `Rate limit approaching: 80% used` |
| `INFO` | Normal operation milestones | `User login successful`, `Server started on port 3000` |
| `DEBUG` | Detailed diagnostic info (dev only) | `Query executed: SELECT * FROM users WHERE id=5` |

### Structured Log Format (JSON)
```json
{
  "timestamp": "2026-03-09T12:00:00Z",
  "level": "INFO",
  "message": "User login successful",
  "service": "auth-service",
  "request_id": "req-abc123",
  "user_id": "usr-456",
  "duration_ms": 45
}
```

### What to Log
- ✅ Request/response metadata (method, path, status code, duration)
- ✅ Authentication events (login, logout, failed attempts)
- ✅ Business events (order created, payment processed)
- ✅ Errors with stack traces (for ERROR level)

### What NEVER to Log
- ❌ Passwords or password hashes
- ❌ API keys, tokens, or secrets
- ❌ PII (email, phone, address) unless anonymized
- ❌ Credit card numbers or financial data
- ❌ Full request/response bodies in production

## SOP: Health Check Patterns

### Liveness Probe — "Is the process alive?"
```
GET /health/live → 200 OK
```
- **Purpose**: Detect if the process is stuck (deadlock, infinite loop).
- **Check**: Process is running and can respond to HTTP.
- **Action on failure**: Restart the container/process.

### Readiness Probe — "Can it handle traffic?"
```
GET /health/ready → 200 OK / 503 Service Unavailable
```
- **Purpose**: Detect if the service can serve requests.
- **Check**: Database connected, cache available, dependencies reachable.
- **Action on failure**: Remove from load balancer, stop routing traffic.

### Graceful Shutdown
When receiving `SIGTERM`:
1. Stop accepting new connections.
2. Finish in-flight requests (timeout: 30s).
3. Close database connection pool.
4. Close cache connections.
5. Exit process with code 0.

## Quality Control
- **Health Check**: Every service must expose `/health/live` and `/health/ready` endpoints.
- **Logging**: Use structured logging (JSON format) with appropriate log levels.
- **Monitoring**: Set up basic alerts for error rate > 1% and P95 latency > 2s.

