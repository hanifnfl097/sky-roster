# API Inventory — SkyRoster

This file lists all available services and their base URLs. For detailed Request/Response schemas, see `docs/features/[feature-name]/api.md`.

**Last Updated:** 2026-03-09

---

## Services

| Service | Base URL (Dev) | Technology | Description |
| :--- | :--- | :--- | :--- |
| **SkyRoster API** | `http://localhost:8080/api/v1` | Spring Boot | Monolithic API for all backend operations |
| **Web Admin** | `http://localhost:3000` | Next.js | Admin dashboard frontend |
| **Mobile App** | — | Flutter | Crew mobile application |

---

## Endpoint Summary

### Authentication (`/api/v1/auth`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Authenticate user, return JWT | Public |
| `POST` | `/auth/refresh` | Refresh access token | Refresh Token |
| `POST` | `/auth/logout` | Invalidate refresh token | Bearer Token |
| `GET` | `/auth/me` | Get current user profile | Bearer Token |

### Crew Management (`/api/v1/crew`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/crew` | List all crew (filter, search, paginate) | ADMIN, ROSTER_OFFICER |
| `POST` | `/crew` | Register new crew member | ADMIN |
| `GET` | `/crew/{id}` | Get crew detail | ADMIN, ROSTER_OFFICER |
| `PUT` | `/crew/{id}` | Update crew profile | ADMIN |
| `PATCH` | `/crew/{id}/status` | Change crew status (ground/activate) | ADMIN |
| `GET` | `/crew/{id}/ftl-summary` | Get FTL accumulated hours | ADMIN, ROSTER_OFFICER, SELF |
| `GET` | `/crew/{id}/roster-history` | Get past & upcoming assignments | ADMIN, ROSTER_OFFICER, SELF |

### Crew Documents (`/api/v1/crew/{crewId}/documents`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/crew/{crewId}/documents` | List all documents for crew | ADMIN, ROSTER_OFFICER |
| `POST` | `/crew/{crewId}/documents` | Add/upload a document | ADMIN |
| `PUT` | `/crew/{crewId}/documents/{docId}` | Update document details | ADMIN |
| `DELETE` | `/crew/{crewId}/documents/{docId}` | Soft-delete a document | ADMIN |
| `GET` | `/crew/documents/expiring` | List documents expiring within N days | ADMIN, ROSTER_OFFICER |

### Fleet / Aircraft Types (`/api/v1/fleet`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/fleet` | List all aircraft types | ADMIN, ROSTER_OFFICER |
| `POST` | `/fleet` | Add aircraft type + complement rules | ADMIN |
| `GET` | `/fleet/{id}` | Get aircraft type & complement | ADMIN, ROSTER_OFFICER |
| `PUT` | `/fleet/{id}` | Update aircraft type | ADMIN |

### Pairings (`/api/v1/pairings`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/pairings` | List pairings (filter by date, status) | ROSTER_OFFICER |
| `POST` | `/pairings` | Create new pairing with flight legs | ROSTER_OFFICER |
| `GET` | `/pairings/{id}` | Get pairing detail with legs & crew | ROSTER_OFFICER |
| `PUT` | `/pairings/{id}` | Update pairing | ROSTER_OFFICER |
| `DELETE` | `/pairings/{id}` | Delete draft pairing | ROSTER_OFFICER |

### Roster (`/api/v1/roster`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/roster` | Get roster board (date range, crew filter) | ROSTER_OFFICER |
| `POST` | `/roster/assign` | Assign crew to pairing (validates FTL, docs, complement) | ROSTER_OFFICER |
| `DELETE` | `/roster/assign/{assignmentId}` | Unassign crew from pairing | ROSTER_OFFICER |
| `POST` | `/roster/publish` | Publish roster for a date range | ROSTER_OFFICER |
| `GET` | `/roster/my` | Get my upcoming roster (mobile) | CREW |

### Day of Operations (`/api/v1/operations`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/operations/flights` | Today's flight board | ROSTER_OFFICER |
| `GET` | `/operations/flights/{id}` | Flight detail with crew sign-on status | ROSTER_OFFICER |
| `POST` | `/operations/sign-on` | Crew sign-on with GPS coordinates | CREW |
| `GET` | `/operations/sign-on/status/{flightId}` | Check sign-on status for a flight | ROSTER_OFFICER, CREW |

### Disruption (`/api/v1/disruptions`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/disruptions` | Report disruption (No-Show, Sick) | ROSTER_OFFICER, CREW |
| `GET` | `/disruptions` | List active disruptions | ROSTER_OFFICER |
| `POST` | `/disruptions/{id}/deploy-standby` | Deploy standby crew to replace affected | ROSTER_OFFICER |
| `PATCH` | `/disruptions/{id}/resolve` | Mark disruption as resolved | ROSTER_OFFICER |

### Voyage Reports (`/api/v1/voyage-reports`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/voyage-reports` | Submit voyage report (block times) | CAPTAIN, PURSER |
| `GET` | `/voyage-reports` | List voyage reports (filter by date) | ADMIN, ROSTER_OFFICER |
| `GET` | `/voyage-reports/{id}` | Get voyage report detail | ADMIN, ROSTER_OFFICER |
| `PATCH` | `/voyage-reports/{id}/approve` | Approve voyage report | ROSTER_OFFICER |

### Reports & Payroll (`/api/v1/reports`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/reports/payroll` | Monthly flight hours per crew (for HR) | ADMIN |
| `GET` | `/reports/ftl-compliance` | FTL compliance summary for all crew | ADMIN, ROSTER_OFFICER |

### Standby (`/api/v1/standby`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/standby` | List standby crew for a date | ROSTER_OFFICER |
| `POST` | `/standby` | Create standby assignment | ROSTER_OFFICER |
| `PATCH` | `/standby/{id}/deploy` | Deploy standby to flight | ROSTER_OFFICER |

### Audit Logs (`/api/v1/audit`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/audit` | List audit logs (filter by entity, date) | ADMIN |
| `GET` | `/audit/{entityType}/{entityId}` | Audit trail for specific entity | ADMIN |

### Notifications (`/api/v1/notifications`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/notifications` | List notifications for current user | ALL |
| `PATCH` | `/notifications/{id}/read` | Mark notification as read | ALL |

### Airports (`/api/v1/airports`)
| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/airports` | List/search airports | ALL |
| `POST` | `/airports` | Add airport | ADMIN |
