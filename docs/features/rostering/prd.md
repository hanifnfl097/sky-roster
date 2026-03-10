# Product Requirement Document (PRD)

**Feature Name:** Rostering, Pairing & Crew Complement
**Status:** Approved
**Owner:** @product-management

## 1. Problem Statement

Creating a flight roster is the most complex planning task in airline operations. A Roster Officer must build multi-leg flight pairings, assign crew that satisfy aircraft-specific crew complement rules, and ensure every assignment passes FTL validation, document currency checks, and unavailability checks — all simultaneously. A single invalid assignment can cascade failures across the entire operation.

**SkyRoster's Rostering Module** provides a structured pairing builder, real-time multi-rule validation, a visual Gantt-style roster board, and a publish workflow that guarantees no incomplete or non-compliant roster reaches the crew.

## 2. Goals & Success Metrics

| Metric Type | Metric Name | Target |
| :--- | :--- | :--- |
| **Product** | Published rosters with incomplete crew complement | 0 (hard-blocked) |
| **Product** | Average time for Roster Officer to build a weekly roster | < 2 hours (vs. 6+ hours manual) |
| **Technical** | Assignment validation API P95 latency | < 500ms |
| **Technical** | Roster board render time (100 crew × 7 days) | < 2s |

## 3. User Stories

- As a **Roster Officer**, I want to create a pairing (multi-leg flight sequence like CGK→SUB→DPS) so that I can group flights for crew assignment efficiency.
- As a **Roster Officer**, I want the system to show the required crew complement when I select an aircraft type (e.g., B737 = 1 Captain + 1 FO + 1 Purser + min 3 FA) so that I know exactly who to assign.
- As a **Roster Officer**, I want to assign crew to a pairing with real-time validation (FTL, documents, complement, unavailability) so that I get immediate feedback on eligibility.
- As a **Roster Officer**, I want the system to BLOCK publishing a roster if any pairing has an incomplete crew complement so that no under-crewed flight enters operations.
- As a **Roster Officer**, I want a Gantt-style roster board showing all crew assignments for a week so that I can visually manage the schedule.
- As a **Roster Officer**, I want to drag-and-drop reassign crew on the roster board with automatic re-validation.
- As a **Crew Member**, I want to see my published roster on the mobile app so that I know my upcoming schedule.

## 4. Functional Requirements

### A. Frontend (Web Admin)

**Pairing Builder (`/pairings/new`):**
- [ ] Date selector for pairing start date
- [ ] Dynamic leg builder: add/remove flight legs (Flight No, Origin IATA, Destination IATA, STD, STA)
- [ ] Aircraft type dropdown — triggers crew complement display
- [ ] Complement panel: shows required roles with fill indicators (e.g., "Captain: 0/1 ❌")
- [ ] Crew picker: searchable crew list filtered by role + type rating + eligibility
- [ ] Validation panel: real-time ✅/❌ for each rule per crew member
- [ ] "Save Pairing" button (saves as DRAFT)

**Roster Board (`/roster`):**
- [ ] Gantt-style timeline: Y-axis = crew (grouped by role), X-axis = dates (week view)
- [ ] Color-coded blocks: Flight (sky blue), Standby (violet), Off (grey), Leave (green), Training (amber)
- [ ] Click a block → show pairing detail popover
- [ ] Unassigned pairings panel: list of pairings that still need crew
- [ ] "Publish Roster" button — disabled if any pairing fails validation
- [ ] Publish confirmation modal with summary (total assigned, warnings count)

**Pairing List (`/pairings`):**
- [ ] Table: Pairing Code, Date, Route Summary, Aircraft Type, Status (Draft/Assigned/Published), Crew Fill %
- [ ] Filters: Date range, Status, Aircraft type
- [ ] Click row → Pairing detail with full crew assignment

### B. Frontend (Mobile — Flutter)

**My Roster (`/roster`):**
- [ ] Weekly calendar view with assigned pairings
- [ ] Pairing card: Flight legs, dates, times, crew list
- [ ] Pull-to-refresh for latest published roster

### C. Backend (API)

**Pairing Management:**
- [ ] `POST /api/v1/pairings` — Create pairing with flight legs
- [ ] `GET /api/v1/pairings` — List pairings (filter by date, status)
- [ ] `GET /api/v1/pairings/{id}` — Pairing detail with legs and assigned crew
- [ ] `PUT /api/v1/pairings/{id}` — Update pairing (only in DRAFT status)
- [ ] `DELETE /api/v1/pairings/{id}` — Delete DRAFT pairing

**Crew Assignment:**
- [ ] `POST /api/v1/roster/assign` — Assign crew to pairing (triggers full validation pipeline)
- [ ] `DELETE /api/v1/roster/assign/{assignmentId}` — Unassign crew

**Roster Publishing:**
- [ ] `GET /api/v1/roster?from=2026-03-01&to=2026-03-07` — Get roster board data
- [ ] `POST /api/v1/roster/publish` — Publish roster for date range (validates all pairings)
- [ ] `GET /api/v1/roster/my` — Get current crew member's roster (mobile)

### D. Business Rules (Domain Layer)

| Rule ID | Rule | Enforcement |
| :--- | :--- | :--- |
| RST-001 | Crew complement per aircraft type must be fully satisfied before publishing | Hard-block publish if any role is under minimum |
| RST-002 | Assigned crew must have a valid type rating for the aircraft type | Domain validation on assignment |
| RST-003 | Assigned crew must not be GROUNDED | Domain validation on assignment |
| RST-004 | Assigned crew must pass all FTL rules (delegates to FTL Rule Engine) | Domain validation on assignment |
| RST-005 | Assigned crew must not have an approved unavailability overlapping the pairing dates | Domain validation on assignment |
| RST-006 | Crew cannot be double-assigned to overlapping pairings | Unique constraint + domain check |
| RST-007 | Only DRAFT pairings can be modified; PUBLISHED pairings require a new version | Status-based write protection |
| RST-008 | Publishing triggers push notifications to all assigned crew | system_notifications + FCM |

### E. Validation Pipeline (Assignment Orchestration)

```
function assignCrewToPairing(crewId, pairingId, role):
    crew = crewRepository.findById(crewId)
    pairing = pairingRepository.findById(pairingId)

    // Step 1: Status check
    if crew.status != ACTIVE:
        throw CrewGroundedException(crew)

    // Step 2: Role match
    if crew.crewRole != role:
        throw RoleMismatchException(crew.crewRole, role)

    // Step 3: Type rating check
    if !crew.hasActiveTypeRating(pairing.aircraftType):
        throw TypeRatingException(crew, pairing.aircraftType)

    // Step 4: Document currency check
    if crew.hasExpiredDocuments():
        throw DocumentExpiredException(crew, expiredDocs)

    // Step 5: Unavailability check
    if crew.hasApprovedUnavailability(pairing.startDate, pairing.endDate):
        throw CrewUnavailableException(crew, unavailability)

    // Step 6: Double-assignment check
    if crew.hasOverlappingAssignment(pairing.startDate, pairing.endDate):
        throw DoubleAssignmentException(crew, existingPairing)

    // Step 7: FTL validation (delegates to FTL Rule Engine)
    ftlRuleEngine.validateFtlForAssignment(crewId, pairing)

    // All checks passed — persist assignment
    assignment = RosterAssignment.create(crew, pairing, role)
    auditLogService.log(ROSTER_ASSIGNMENT, assignment, CREATED)
    return assignment
```

## 5. Data & Privacy

- **Data Attributes**: Pairing codes, flight legs, crew assignments, publication timestamps.
- **Retention**: Published rosters retained for 3 years for regulatory and HR records.
- **Access Control**: ROSTER_OFFICER can create/assign/publish. CREW can view their own roster only.

## 6. Risk Mitigation

| Risk | Fallback Strategy |
| :--- | :--- |
| Roster board performance with large crew pool | Virtualized rendering (react-window); server-side pagination by role group |
| Concurrent roster edits by multiple Roster Officers | Optimistic locking on pairing status; WebSocket conflict alerts |
| Pairing published with stale FTL data | FTL recalculation at publish time (not just at assignment time) |
| Drag-and-drop UX complexity | Fallback to manual assign/unassign forms; drag-and-drop as progressive enhancement |
