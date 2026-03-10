# Product Requirement Document (PRD)

**Feature Name:** Day of Operations — Sign-On, Geofencing & Disruption Handling
**Status:** Approved
**Owner:** @product-management

## 1. Problem Statement

On the day of operations, airlines face two critical challenges: (1) ensuring all assigned crew are physically present at the Crew Center before departure, and (2) managing last-minute disruptions (no-shows, sickness) that can delay or cancel flights. Traditional paper-based sign-on processes offer no real-time visibility, and disruption handling relies on manual phone calls to find replacement crew.

**SkyRoster's Day of Operations module** provides GPS-verified geofenced sign-on via the mobile app, a real-time flight board for the Roster Officer, automated disruption detection, and one-click standby crew deployment.

## 2. Goals & Success Metrics

| Metric Type | Metric Name | Target |
| :--- | :--- | :--- |
| **Product** | Average disruption resolution time (no-show to standby deployed) | < 15 minutes |
| **Product** | Crew sign-on compliance rate (signed on before ETD - 60 min) | > 98% |
| **Technical** | Sign-on API P95 latency | < 500ms |
| **Technical** | Real-time status update propagation (WebSocket) | < 2s |

## 3. User Stories

- As a **Crew Member**, I want to sign on via the mobile app 120 minutes before ETD so that my attendance is recorded digitally.
- As a **System**, I want to verify that the crew member is within the geofence radius of the Crew Center during sign-on so that physical presence is confirmed.
- As a **Crew Member**, I want to report that I am sick before my flight so that the system can initiate standby replacement.
- As a **Roster Officer**, I want to see a real-time flight board showing each crew member's sign-on status so that I can monitor Day of Ops.
- As a **System**, I want to automatically create a disruption alert when a crew member has not signed on by ETD - 60 minutes so that the Roster Officer is notified.
- As a **Roster Officer**, I want to deploy a standby crew member with one click, with the system filtering only crew who match the required role and type rating so that flights are not delayed.
- As a **Roster Officer**, I want to manage standby duty windows (crew available for specific time slots on specific dates) so that I have a pool of replacements.

## 4. Functional Requirements

### A. Frontend (Web Admin)

**Day of Operations Board (`/operations`):**
- [ ] Flight board table: Flight No, Route, ETD, ATD, Status (On-Time / Delayed / Departed)
- [ ] Expandable crew column per flight: crew name, role, sign-on status (✅ Signed On, ⏳ Pending, ❌ No-show, 🤒 Sick)
- [ ] Real-time update via WebSocket (no manual refresh)
- [ ] Auto-highlight flights with incomplete crew sign-on (red border)

**Disruption Alert Panel:**
- [ ] Alert cards: "[Crew Name] — No-Show for [Flight No]. Role needed: [First Officer]. Standby available: [list]"
- [ ] "Deploy Standby" button → opens a filtered modal showing eligible standby crew
- [ ] One-click deploy: assigns standby crew + notifies them via push notification
- [ ] "Mark Resolved" button after deployment

**Standby Management (`/standby`):**
- [ ] Table: Crew Name, Role, Standby Date, Window (06:00–14:00), Status (Available / Deployed / Released)
- [ ] "Add Standby" form: select crew + date + time window
- [ ] Filter by date and role

### B. Frontend (Mobile — Flutter)

**Sign-On Screen (`/sign-on/:flightId`):**
- [ ] Map view showing Crew Center perimeter (circle overlay) + current GPS position (marker)
- [ ] Status indicator: "You are within the Crew Center ✅" or "You are outside the geofence ❌"
- [ ] "Confirm Sign-On" button — disabled if outside geofence
- [ ] Success screen with timestamp and flight details
- [ ] If < 120 minutes before ETD: show a countdown timer and "Sign-On Window Open" indicator
- [ ] If > 120 minutes before ETD: show "Sign-On window opens in [time]" (button disabled)

**Sick Report:**
- [ ] "Report Sick" button on the flight detail screen
- [ ] Confirmation dialog: "Are you sure? This will notify the Roster Officer and mark you as unfit for this flight."
- [ ] After confirmation: creates a disruption event + removes crew from assignment

**Home Screen — Next Flight Card:**
- [ ] Countdown to next ETD
- [ ] "Sign-On" CTA button (appears when window opens at ETD - 120 min)

### C. Backend (API)

**Sign-On:**
- [ ] `POST /api/v1/operations/sign-on` — Crew sign-on with GPS coordinates
  - Request: `{ flightId, latitude, longitude }`
  - Validates: geofence, time window, crew assignment existence
  - Response: sign-on record with timestamp
- [ ] `GET /api/v1/operations/sign-on/status/{flightId}` — Current sign-on status for all crew on a flight

**Disruption Management:**
- [ ] `POST /api/v1/disruptions` — Report disruption (No-Show or Sick)
  - Can be triggered by: crew (sick report) or auto-detection (no sign-on by ETD - 60 min)
  - Creates disruption_events record + system_notifications for Roster Officer
- [ ] `GET /api/v1/disruptions?status=OPEN` — List active disruptions
- [ ] `POST /api/v1/disruptions/{id}/deploy-standby` — Deploy standby crew
  - Validates: standby crew matches required role + type rating + is available + passes FTL
  - Updates: roster_assignments, standby_assignments, disruption_events
  - Triggers: push notification to standby crew
- [ ] `PATCH /api/v1/disruptions/{id}/resolve` — Mark disruption as resolved

**Standby Management:**
- [ ] `GET /api/v1/standby?date=2026-03-09` — List standby crew for a date
- [ ] `POST /api/v1/standby` — Create standby assignment
- [ ] `PATCH /api/v1/standby/{id}/deploy` — Deploy standby to flight

**Flight Board (WebSocket):**
- [ ] WebSocket endpoint: `/ws/operations` — Pushes real-time updates for sign-on events, disruption alerts, flight status changes
- [ ] Admin subscribers receive live updates without polling

### D. Business Rules (Domain Layer)

| Rule ID | Rule | Enforcement |
| :--- | :--- | :--- |
| OPS-001 | Sign-On window opens exactly 120 minutes before ETD (configurable via `SIGN_ON_WINDOW_MINUTES`) | Time check on sign-on request |
| OPS-002 | GPS coordinates must be within `GEOFENCE_RADIUS_METERS` of the assigned airport's crew center | Haversine distance calculation |
| OPS-003 | Each crew member can only sign on once per flight | Unique constraint: (crew_member_id, flight_id) |
| OPS-004 | Auto-disruption alert if crew has not signed on by ETD - 60 minutes | Scheduled check (cron or event-driven) |
| OPS-005 | Standby deployment must match: same role + valid type rating for the aircraft + FTL compliance | Full validation pipeline (same as roster assignment) |
| OPS-006 | Sick report creates an immediate disruption event and removes crew from assignment | Transactional: update roster_assignment status + create disruption |
| OPS-007 | All sign-on events, disruption events, and standby deployments generate audit_logs | Domain event → audit listener |

### E. Geofence Calculation (Haversine Formula)

```
function isWithinGeofence(crewLat, crewLon, airportLat, airportLon, radiusMeters):
    R = 6371000  // Earth radius in meters
    dLat = toRadians(airportLat - crewLat)
    dLon = toRadians(airportLon - crewLon)

    a = sin(dLat/2)² + cos(toRadians(crewLat)) * cos(toRadians(airportLat)) * sin(dLon/2)²
    c = 2 * atan2(sqrt(a), sqrt(1-a))
    distance = R * c

    return distance <= radiusMeters
```

## 5. Data & Privacy

- **Data Attributes**: GPS coordinates (latitude, longitude), sign-on timestamps, disruption details.
- **Retention**: Sign-on records retained for 2 years. GPS coordinates are operational data, not personal tracking.
- **Privacy Note**: GPS is only captured at the moment of sign-on (single point), NOT continuous tracking. This must be communicated clearly to crew in the app's privacy notice.

## 6. Risk Mitigation

| Risk | Fallback Strategy |
| :--- | :--- |
| GPS unavailable or inaccurate on crew device | Allow "Manual Override" sign-on by Roster Officer (requires authorization + audit log) |
| WebSocket connection dropped | Auto-reconnect with exponential backoff; fallback to 30s HTTP polling |
| No standby crew available for disruption | Alert escalation to ADMIN; system suggests off-duty crew willing to accept overtime |
| Crew signs on at wrong airport | Airport geofence is per-flight origin airport; system rejects with clear error message |
