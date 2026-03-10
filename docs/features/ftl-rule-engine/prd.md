# Product Requirement Document (PRD)

**Feature Name:** Flight Time Limitation (FTL) Rule Engine
**Status:** Approved
**Owner:** @product-management

## 1. Problem Statement

Aviation regulatory bodies (CASR 121 / ICAO Annex 6) impose strict Flight Time Limitations to prevent crew fatigue and ensure flight safety. Airlines must track cumulative flight hours across rolling windows (7-day, monthly, yearly) and enforce minimum rest periods between duties. Violations carry severe penalties including fines, license suspension, and aircraft grounding.

Manual FTL tracking in spreadsheets is unreliable and cannot provide real-time enforcement during the rostering process. **SkyRoster's FTL Rule Engine** dynamically calculates accumulated hours on every assignment attempt and throws a hard-blocking exception if any limit would be exceeded.

## 2. Goals & Success Metrics

| Metric Type | Metric Name | Target |
| :--- | :--- | :--- |
| **Product** | FTL Violations (crew exceeding any limit) | 0 (zero tolerance) |
| **Product** | Roster Officers blocked by false-positive FTL rejections | < 2% of valid assignments |
| **Technical** | FTL calculation latency per crew per assignment | < 200ms |
| **Technical** | FTL batch report generation (500 crew) | < 10s |

## 3. User Stories

- As a **Roster Officer**, I want the system to automatically reject an assignment if it would cause the crew member to exceed 30 flight hours in any rolling 7-day window so that we comply with CASR 121.
- As a **Roster Officer**, I want the system to reject an assignment if it would cause the crew member to exceed 100 flight hours in the current calendar month.
- As a **Roster Officer**, I want the system to reject an assignment if it would cause the crew member to exceed 1050 flight hours in the current calendar year.
- As a **Roster Officer**, I want the system to reject an assignment if the crew member would not have at least 12 hours of rest between Block-On of the previous flight and Sign-On of the next flight.
- As a **Roster Officer**, I want to see an FTL summary (7-day, monthly, yearly accumulated hours with progress bars) when viewing a crew member's profile so that I can make informed rostering decisions.
- As a **Crew Member**, I want to see my own FTL summary on the mobile app so that I know my remaining capacity.
- As a **System Admin**, I want a monthly FTL compliance report for all crew so that we can provide evidence to the regulator.
- As a **System**, I want FTL to be recalculated from actual Block-Off/Block-On times (voyage reports) rather than scheduled times, to maintain accuracy.

## 4. Functional Requirements

### A. Frontend (Web Admin)

**Crew Detail — FTL Tab:**
- [ ] Progress bar for 7-day rolling hours (e.g., 22/30 hours — amber if > 80%, red if > 90%)
- [ ] Progress bar for monthly hours (e.g., 78/100 hours)
- [ ] Progress bar for yearly hours (e.g., 890/1050 hours)
- [ ] Table of recent flights contributing to current totals
- [ ] Rest period indicator: hours since last Block-On

**Roster Assignment — Validation Panel:**
- [ ] When assigning crew to a pairing, show real-time FTL check results
- [ ] ✅ Pass / ❌ Fail for each rule (7-day, monthly, yearly, rest period)
- [ ] If any rule fails: show the specific violation message and block the "Assign" button
- [ ] Show projected hours after assignment (before confirmation)

**FTL Compliance Report (`/reports/ftl-compliance`):**
- [ ] Table: Crew Name, Role, 7-Day Hours, Monthly Hours, Yearly Hours, Status (Compliant 🟢 / Warning 🟡 / Violation 🔴)
- [ ] Export to CSV/Excel for regulatory submission
- [ ] Date range filter

### B. Frontend (Mobile — Flutter)

**Home Dashboard:**
- [ ] Circular progress indicators for Monthly and Yearly flight hours
- [ ] "Hours This Month: XX / 100" and "Hours This Year: XX / 1050"

**Flight Detail:**
- [ ] Show projected FTL impact for each upcoming assigned flight

### C. Backend (API)

**FTL Query Endpoints:**
- [ ] `GET /api/v1/crew/{id}/ftl-summary` — Returns accumulated hours for all 3 windows + rest hours since last Block-On
- [ ] `GET /api/v1/reports/ftl-compliance?month=2026-03` — Monthly FTL report for all crew

**FTL Validation (Internal Domain Service — Not Directly Exposed):**
- [ ] Called internally by the Rostering use case before any assignment
- [ ] Returns a validation result object with pass/fail for each rule and violation details

### D. Business Rules (Domain Layer)

| Rule ID | Rule | Value | Source | Enforcement |
| :--- | :--- | :--- | :--- | :--- |
| FTL-001 | Maximum flight hours in 7 consecutive days | 30 hours | CASR 121 | Hard-block on assignment |
| FTL-002 | Maximum flight hours in 1 calendar month | 100 hours | CASR 121 | Hard-block on assignment |
| FTL-003 | Maximum flight hours in 1 calendar year | 1050 hours | CASR 121 | Hard-block on assignment |
| FTL-004 | Minimum rest period between Block-On and next Sign-On | 12 hours | CASR 121 | Hard-block on assignment |
| FTL-005 | FTL is calculated from ACTUAL Block-Off/Block-On times when available (voyage report) | — | Best practice | Recalculation on voyage report submission |
| FTL-006 | FTL limits are configurable via environment variables | — | Operational flexibility | `FTL_MAX_7_DAY_HOURS`, etc. |
| FTL-007 | FTL warning threshold at 80% of any limit | — | Proactive alerting | system_notifications with severity WARNING |

### E. Calculation Logic (Pseudocode)

```
function validateFtlForAssignment(crewId, pairing):
    estimatedHours = pairing.totalScheduledFlightHours

    // Rule FTL-001: Rolling 7-day window
    for each day in pairing.dateRange:
        rolling7DayHours = SUM(flight_hour_logs WHERE crew = crewId
                           AND flight_date BETWEEN day-6 AND day)
        if (rolling7DayHours + estimatedHours) > FTL_MAX_7_DAY_HOURS:
            throw FtlViolationException("7-day limit exceeded", rolling7DayHours, FTL_MAX_7_DAY_HOURS)

    // Rule FTL-002: Calendar month
    monthlyHours = SUM(flight_hour_logs WHERE crew = crewId
                   AND flight_date IN currentMonth)
    if (monthlyHours + estimatedHours) > FTL_MAX_MONTHLY_HOURS:
        throw FtlViolationException("Monthly limit exceeded", monthlyHours, FTL_MAX_MONTHLY_HOURS)

    // Rule FTL-003: Calendar year
    yearlyHours = SUM(flight_hour_logs WHERE crew = crewId
                  AND flight_date IN currentYear)
    if (yearlyHours + estimatedHours) > FTL_MAX_YEARLY_HOURS:
        throw FtlViolationException("Yearly limit exceeded", yearlyHours, FTL_MAX_YEARLY_HOURS)

    // Rule FTL-004: Minimum rest
    lastBlockOn = MAX(flight_hour_logs.block_on WHERE crew = crewId)
    nextSignOn = pairing.firstFlight.scheduledDeparture - SIGN_ON_WINDOW_MINUTES
    restHours = HOURS_BETWEEN(lastBlockOn, nextSignOn)
    if restHours < FTL_MIN_REST_HOURS:
        throw FtlViolationException("Rest period violated", restHours, FTL_MIN_REST_HOURS)

    return FtlValidationResult.PASS
```

## 5. Data & Privacy

- **Data Attributes**: Crew ID, flight dates, actual block times, calculated flight hours, duty hours.
- **Retention**: Flight hour logs retained indefinitely for regulatory audits.
- **Access Control**: ADMIN and ROSTER_OFFICER can view all crew FTL. CREW can view only their own.

## 6. Risk Mitigation

| Risk | Fallback Strategy |
| :--- | :--- |
| Incorrect FTL due to missing voyage reports | Use scheduled times as fallback; flag flights without voyage reports |
| Performance degradation on yearly calculation for high-frequency crew | Index `flight_hour_logs(crew_member_id, flight_date)`; use monthly partitions; cache recent sums |
| Timezone ambiguity on block times | Store all times in UTC (TIMESTAMPTZ); convert for display only |
| Edge case: flight spans midnight / crosses calendar month boundary | Attribute hours to the Block-Off date for monthly/yearly; use actual date range for 7-day rolling |
