# Environment Configuration — SkyRoster

**Service Name:** SkyRoster (All Platforms)
**Last Updated:** 2026-03-09

---

## 1. Required Environment Variables

### Application (Spring Boot Backend)
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | string | Yes | `dev` | Runtime profile (`dev` / `staging` / `prod`) |
| `SERVER_PORT` | number | Yes | `8080` | API server port |
| `LOG_LEVEL` | string | No | `INFO` | Logging verbosity (`DEBUG` / `INFO` / `WARN` / `ERROR`) |

### Database (PostgreSQL)
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `DB_HOST` | string | Yes | `localhost` | PostgreSQL server hostname |
| `DB_PORT` | number | Yes | `5432` | PostgreSQL server port |
| `DB_NAME` | string | Yes | — | Database name |
| `DB_USERNAME` | string | Yes | — | Database username |
| `DB_PASSWORD` | string | Yes | — | Database password (🔒 Secret) |
| `DB_POOL_SIZE` | number | No | `10` | HikariCP connection pool size |

### Authentication (JWT)
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `JWT_SECRET` | string | Yes | — | JWT signing secret (🔒 Secret, min 64 chars, HS512) |
| `JWT_ACCESS_EXPIRY_MINUTES` | number | No | `15` | Access token expiry in minutes |
| `JWT_REFRESH_EXPIRY_DAYS` | number | No | `7` | Refresh token expiry in days |

### Geofencing
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `GEOFENCE_RADIUS_METERS` | number | No | `500` | Crew center geofence radius in meters |
| `SIGN_ON_WINDOW_MINUTES` | number | No | `120` | Minutes before ETD sign-on opens |

### Cron Jobs (Document Expiry Scanner)
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `CRON_DOC_EXPIRY_SCHEDULE` | string | No | `0 0 6 * * *` | Cron expression for document expiry checker (daily at 06:00) |
| `DOC_EXPIRY_WARNING_DAYS` | string | No | `30,14,7` | Comma-separated warning thresholds in days |

### FTL Configuration
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `FTL_MAX_7_DAY_HOURS` | number | No | `30` | Maximum flight hours in 7 consecutive days |
| `FTL_MAX_MONTHLY_HOURS` | number | No | `100` | Maximum flight hours in 1 calendar month |
| `FTL_MAX_YEARLY_HOURS` | number | No | `1050` | Maximum flight hours in 1 year |
| `FTL_MIN_REST_HOURS` | number | No | `12` | Minimum rest hours between Block-On and next Sign-On |

### External Services
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `REDIS_URL` | string | No | — | Redis connection string (for caching/sessions) |
| `SMTP_HOST` | string | No | — | Email server hostname |
| `SMTP_PORT` | number | No | `587` | Email server port |
| `SMTP_USERNAME` | string | No | — | Email username (🔒 Secret) |
| `SMTP_PASSWORD` | string | No | — | Email password (🔒 Secret) |
| `FCM_SERVER_KEY` | string | No | — | Firebase Cloud Messaging key for push notifications (🔒 Secret) |
| `SENTRY_DSN` | string | No | — | Error tracking DSN (🔒 Secret) |

### Next.js Frontend
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `NEXT_PUBLIC_API_URL` | string | Yes | `http://localhost:8080/api/v1` | Backend API base URL |
| `NEXT_PUBLIC_WS_URL` | string | No | `ws://localhost:8080/ws` | WebSocket URL for real-time updates |
| `NEXTAUTH_SECRET` | string | Yes | — | NextAuth session secret (🔒 Secret) |
| `PORT` | number | No | `3000` | Next.js dev server port |

### Flutter Mobile App
| Variable | Type | Required | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `API_BASE_URL` | string | Yes | `http://10.0.2.2:8080/api/v1` | Backend API URL (emulator: 10.0.2.2) |
| `GOOGLE_MAPS_API_KEY` | string | No | — | Google Maps API key for geofence visualization (🔒 Secret) |

---

## 2. Environment-Specific Overrides

| Variable | Development | Staging | Production |
| :--- | :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `dev` | `staging` | `prod` |
| `LOG_LEVEL` | `DEBUG` | `INFO` | `WARN` |
| `DB_HOST` | `localhost` | `staging-db.internal` | `prod-db.internal` |
| `DB_POOL_SIZE` | `5` | `15` | `30` |
| `GEOFENCE_RADIUS_METERS` | `50000` (relaxed for testing) | `500` | `500` |
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080/api/v1` | `https://staging-api.skyroster.com/api/v1` | `https://api.skyroster.com/api/v1` |

---

## 3. `.env.example` (Backend)

```bash
# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
LOG_LEVEL=INFO

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=skyroster_dev
DB_USERNAME=
DB_PASSWORD=

# Authentication
JWT_SECRET=
JWT_ACCESS_EXPIRY_MINUTES=15
JWT_REFRESH_EXPIRY_DAYS=7

# Geofencing
GEOFENCE_RADIUS_METERS=500
SIGN_ON_WINDOW_MINUTES=120

# FTL (CASR 121 / ICAO Defaults)
FTL_MAX_7_DAY_HOURS=30
FTL_MAX_MONTHLY_HOURS=100
FTL_MAX_YEARLY_HOURS=1050
FTL_MIN_REST_HOURS=12

# Cron
CRON_DOC_EXPIRY_SCHEDULE=0 0 6 * * *
DOC_EXPIRY_WARNING_DAYS=30,14,7

# External Services (optional)
REDIS_URL=
SMTP_HOST=
SMTP_PORT=587
SMTP_USERNAME=
SMTP_PASSWORD=
FCM_SERVER_KEY=
SENTRY_DSN=
```

---

## 4. Secret Management Notes
- 🔒 Variables marked with Secret MUST NOT be committed to version control.
- Use a cloud secret manager (AWS Secrets Manager, GCP Secret Manager, HashiCorp Vault) for staging and production.
- Rotate `JWT_SECRET` every 90 days. Use a minimum of 64 characters (HS512).
- `DB_PASSWORD` must use strong, unique passwords per environment.
- `FCM_SERVER_KEY` should be scoped to the SkyRoster project only.
