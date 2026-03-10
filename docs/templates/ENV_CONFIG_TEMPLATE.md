# Environment Configuration

**Service Name:** [Service Name]
**Last Updated:** YYYY-MM-DD

## 1. Required Environment Variables

### Application
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `NODE_ENV` | string | Yes | `development` | Runtime environment (`development` / `staging` / `production`) |
| `PORT` | number | Yes | `3000` | Application server port |
| `LOG_LEVEL` | string | No | `info` | Logging verbosity (`debug` / `info` / `warn` / `error`) |

### Database
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `DB_HOST` | string | Yes | `localhost` | Database server hostname |
| `DB_PORT` | number | Yes | `5432` | Database server port |
| `DB_NAME` | string | Yes | — | Database name |
| `DB_USER` | string | Yes | — | Database username |
| `DB_PASSWORD` | string | Yes | — | Database password (🔒 Secret) |

### Authentication
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `JWT_SECRET` | string | Yes | — | JWT signing secret (🔒 Secret, min 32 chars) |
| `JWT_EXPIRY` | string | No | `15m` | Access token expiry duration |
| `REFRESH_TOKEN_EXPIRY` | string | No | `7d` | Refresh token expiry duration |

### External Services
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `REDIS_URL` | string | No | — | Redis connection string (for caching/sessions) |
| `SMTP_HOST` | string | No | — | Email server hostname |
| `SENTRY_DSN` | string | No | — | Error tracking DSN (🔒 Secret) |

## 2. Environment-Specific Overrides

| Variable | Development | Staging | Production |
| :--- | :--- | :--- | :--- |
| `NODE_ENV` | `development` | `staging` | `production` |
| `LOG_LEVEL` | `debug` | `info` | `warn` |
| `DB_HOST` | `localhost` | `staging-db.internal` | `prod-db.internal` |

## 3. `.env.example`

```bash
# Application
NODE_ENV=development
PORT=3000
LOG_LEVEL=info

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=myapp_dev
DB_USER=
DB_PASSWORD=

# Authentication
JWT_SECRET=
JWT_EXPIRY=15m
REFRESH_TOKEN_EXPIRY=7d

# External Services (optional)
REDIS_URL=
SMTP_HOST=
SENTRY_DSN=
```

## 4. Secret Management Notes
- 🔒 Variables marked with Secret MUST NOT be committed to version control.
- Use cloud secret managers (AWS Secrets Manager, GCP Secret Manager) for production.
- Rotate secrets periodically (recommended: every 90 days).
