# Product Requirement Document (PRD)

**Feature Name:** Crew Management & License Gatekeeper
**Status:** Approved
**Owner:** @product-management

## 1. Problem Statement

Airlines must ensure every crew member holds valid, non-expired documents before they can legally operate a flight. Under CASR 121 / ICAO regulations, failing to track crew currency exposes the airline to regulatory penalties, safety risks, and potential grounding of operations. Manual spreadsheet tracking is error-prone and cannot enforce hard blocks in real-time during the rostering process.

**SkyRoster solves this** by maintaining a digital crew profile with automated document expiry tracking, proactive cron-based warnings, and a hard-blocking mechanism that prevents non-compliant crew from being scheduled.

## 2. Goals & Success Metrics

| Metric Type | Metric Name | Target |
| :--- | :--- | :--- |
| **Product** | Compliance Violations (crew rostered with expired docs) | 0 (zero tolerance) |
| **Product** | Admin time spent on manual document tracking | Reduced by > 80% |
| **Technical** | Document expiry cron job execution time | < 5s for 500 crew |
| **Technical** | Crew CRUD API P95 Latency | < 300ms |

## 3. User Stories

- As a **System Admin**, I want to register a new crew member with role, base station, and type ratings so that they appear in the rostering pool.
- As a **System Admin**, I want to upload and manage crew documents (MEDEX, ATPL/CPL, Instrument Rating, SEP, Sim Check) with expiry dates so that the system tracks currency automatically.
- As a **Roster Officer**, I want to see which documents are expiring in 30/14/7 days so that I can proactively arrange renewals.
- As a **System**, I want to run a daily cron job at 06:00 that scans all crew documents and triggers warnings at 30, 14, and 7 days before expiry, and auto-grounds the crew on expiry.
- As a **System Admin**, I want to manually ground/activate a crew member with a documented reason so that there is an audit trail.
- As a **Crew Member**, I want to view my own profile and document status on the mobile app so that I know my currency status.
- As a **System Admin**, I want to manage crew unavailabilities (annual leave, training blocks) so that the roster engine respects planned absences.

## 4. Functional Requirements

### A. Frontend (Web Admin)

**Crew List Page (`/crew`)**:
- [ ] Search bar: filter by name or staff ID
- [ ] Dropdown filters: Role (Captain/FO/Purser/FA), Status (Active/Grounded), Base Station
- [ ] "+Add Crew" primary button → opens Crew Registration form
- [ ] Paginated table: Staff ID, Name, Role (badge), Type Ratings, Status (Active 🟢 / Grounded 🔴), MEDEX Expiry
- [ ] Click row → navigate to `/crew/:id`

**Crew Detail Page (`/crew/:id`)**:
- [ ] Profile header: Avatar, name, staff ID, role badge, base, status badge
- [ ] Tab 1 — Documents: Table (Type, Doc Number, Issue Date, Expiry Date, Status Badge, Upload/Edit actions)
- [ ] Tab 2 — FTL Summary: Progress bars for 7-day / Monthly / Yearly hours
- [ ] Tab 3 — Roster History: Timeline of past and upcoming pairings
- [ ] Tab 4 — Unavailabilities: List of leave/training blocks with approval status
- [ ] Tab 5 — Audit Log: Chronological list of all status changes

**Document Expiry Dashboard Widget (on Dashboard `/dashboard`)**:
- [ ] Alert panel showing crew with documents expiring in < 30 days
- [ ] Color-coded: 🔴 < 7 days, 🟠 < 14 days, 🟡 < 30 days
- [ ] Click → navigate to crew detail

### B. Frontend (Mobile — Flutter)

**Documents Screen (`/documents`)**:
- [ ] List of all personal documents with status badges
- [ ] Read-only view (crew cannot edit their own documents)
- [ ] Countdown indicator for upcoming expirations

**Profile Screen (`/profile`)**:
- [ ] Display personal info, role, base station, status
- [ ] FTL quick summary (hours this month / this year)

### C. Backend (API)

**Crew CRUD:**
- [ ] `POST /api/v1/crew` — Register new crew member (with user account creation)
- [ ] `GET /api/v1/crew` — List all crew (paginated, filterable, searchable)
- [ ] `GET /api/v1/crew/{id}` — Get crew detail with all relationships
- [ ] `PUT /api/v1/crew/{id}` — Update crew profile
- [ ] `PATCH /api/v1/crew/{id}/status` — Change status (ground/activate) with audit log

**Document Management:**
- [ ] `POST /api/v1/crew/{crewId}/documents` — Add a new document
- [ ] `GET /api/v1/crew/{crewId}/documents` — List all documents for a crew member
- [ ] `PUT /api/v1/crew/{crewId}/documents/{docId}` — Update document
- [ ] `DELETE /api/v1/crew/{crewId}/documents/{docId}` — Soft-delete document
- [ ] `GET /api/v1/crew/documents/expiring?days=30` — List all documents expiring within N days

**Unavailability Management:**
- [ ] `POST /api/v1/crew/{crewId}/unavailabilities` — Create unavailability request
- [ ] `GET /api/v1/crew/{crewId}/unavailabilities` — List unavailabilities for a crew member
- [ ] `PATCH /api/v1/crew/{crewId}/unavailabilities/{id}/approve` — Approve/reject unavailability

**Document Expiry Cron Job:**
- [ ] Runs daily at 06:00 (configurable via `CRON_DOC_EXPIRY_SCHEDULE`)
- [ ] Scans all `crew_documents` and `crew_type_ratings` for upcoming expiries
- [ ] At 30/14/7 days: Updates status to `EXPIRING_SOON`, creates `system_notifications` with severity WARNING/CRITICAL
- [ ] At 0 days (expired): Updates status to `EXPIRED`, sets `crew_members.status` to `GROUNDED`, creates audit log entry

### D. Business Rules (Domain Layer)

| Rule ID | Rule | Enforcement |
| :--- | :--- | :--- |
| CR-001 | All crew must have valid MEDEX, Type Rating, and SEP | Hard-block from rostering |
| CR-002 | Flight Crew (Captain/FO) additionally need ATPL/CPL, Instrument Rating, and valid Sim Check (6-monthly) | Hard-block from rostering |
| CR-003 | Expired document → Auto-ground crew member | Cron job + status change |
| CR-004 | Grounded crew cannot be assigned to any pairing | Domain exception on assignment attempt |
| CR-005 | Approved unavailability blocks crew from assignment for the specified date range | Overlap check in rostering engine |
| CR-006 | All status changes must generate audit_logs entries | JPA event listener / domain event |

## 5. Data & Privacy

- **Data Attributes**: Staff ID, personal name, role, base station, document expiry dates, uploaded document scans (S3/storage URLs).
- **Retention**: Crew profiles and documents retained for 5 years after deactivation (regulatory requirement).
- **Access Control**: Only ADMIN can modify crew data. ROSTER_OFFICER can view. CREW can only view their own profile.

## 6. Risk Mitigation

| Risk | Fallback Strategy |
| :--- | :--- |
| Cron job fails silently | Health check endpoint for cron status; alert if last run > 25 hours ago |
| Document upload storage failure | Retry with exponential backoff; allow manual re-upload; don't block crew registration |
| Race condition on grounding (cron vs manual) | Use optimistic locking (`@Version` on crew_members) |
