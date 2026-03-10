# Product Requirement Document (PRD)

**Feature Name:** Post-Flight — Block Times & Voyage Report
**Status:** Approved
**Owner:** @product-management

## 1. Problem Statement

After every flight, the airline must record actual Block-Off (engine start) and Block-On (engine stop) times to calculate real flight hours. These actual times feed into the FTL Rule Engine, crew payroll (per diem and flight allowance calculations), and regulatory compliance reports. Without a digital voyage reporting flow, data entry relies on paper forms that are manually transcribed — leading to delays, errors, and payroll disputes.

**SkyRoster's Post-Flight module** enables the Captain or Purser to submit voyage reports directly from the mobile app, triggering immediate FTL recalculation and aggregating hours for HR payroll extraction.

## 2. Goals & Success Metrics

| Metric Type | Metric Name | Target |
| :--- | :--- | :--- |
| **Product** | Voyage report submission rate (within 2h of landing) | > 95% |
| **Product** | Payroll data extraction accuracy | 100% (zero manual corrections) |
| **Technical** | Voyage report API P95 Latency | < 300ms |
| **Technical** | Monthly payroll report generation (500 crew) | < 15s |

## 3. User Stories

- As a **Captain**, I want to input actual Block-Off and Block-On times via the mobile app after landing so that the system records accurate flight hours.
- As a **Purser**, I want to submit a voyage report for the cabin crew perspective (confirming block times and noting any cabin incidents) so that the record is complete.
- As a **System**, I want to automatically calculate flight hours from the submitted block times and update the `flight_hour_logs` table so that FTL is recalculated with actual data.
- As a **Roster Officer**, I want to review and approve voyage reports before they feed into payroll so that I can catch any discrepancies.
- As a **System Admin**, I want a monthly payroll report showing total flight hours, duty hours, and per-diem days per crew member so that HR can process allowances accurately.
- As a **System Admin**, I want voyage reports to be immutable after approval (read-only) so that they serve as tamper-proof audit records.

## 4. Functional Requirements

### A. Frontend (Web Admin)

**Voyage Reports List (`/reports/voyage`):**
- [ ] Table: Flight No, Route, Date, Submitted By, Block-Off, Block-On, Flight Hours, Status (Submitted 🔵 / Approved ✅ / Rejected 🔴)
- [ ] Filters: Date range, Status, Flight number
- [ ] Click row → Voyage report detail

**Voyage Report Detail:**
- [ ] Read-only display of all submitted data
- [ ] Comparison: Scheduled times vs. Actual times (highlighted if difference > 30 min)
- [ ] "Approve" / "Reject" buttons (ROSTER_OFFICER only)
- [ ] Rejection requires a reason comment

**Payroll Export (`/reports/payroll`):**
- [ ] Month selector
- [ ] Table: Staff ID, Name, Role, Total Flight Hours, Total Duty Hours, Number of Duty Days, Per Diem Count
- [ ] "Export CSV" and "Export Excel" buttons
- [ ] Summary row: Totals across all crew

### B. Frontend (Mobile — Flutter)

**Voyage Report Screen (`/voyage-report/:flightId`):**
- [ ] Pre-filled flight info: Flight No, Route, Date
- [ ] Time pickers for Actual Block-Off and Actual Block-On
- [ ] Calculated flight hours displayed in real-time (Block-On - Block-Off)
- [ ] Delay code dropdown (IATA delay codes: passenger, weather, technical, etc.)
- [ ] Remarks textarea for additional notes
- [ ] "Submit Report" button → Confirmation dialog with summary
- [ ] After submission: form becomes read-only with "Submitted ✅" status badge

**Access Control:**
- [ ] Only **Captain** and **Purser** roles see the "Submit Voyage Report" option
- [ ] Only available for flights with status = ARRIVED
- [ ] Cannot submit if report already exists for this flight

### C. Backend (API)

**Voyage Report Submission:**
- [ ] `POST /api/v1/voyage-reports` — Submit voyage report
  ```json
  {
    "flightId": "uuid",
    "actualBlockOff": "2026-03-09T08:15:00Z",
    "actualBlockOn": "2026-03-09T10:45:00Z",
    "delayCode": "WEATHER",
    "remarks": "Holding pattern due to thunderstorm at destination"
  }
  ```
  - Calculates `calculated_flight_hours` = Block-On - Block-Off in decimal hours
  - Creates `flight_hour_logs` entries for ALL crew assigned to this flight
  - Updates `flights.actual_block_off` and `flights.actual_block_on`

**Voyage Report Review:**
- [ ] `GET /api/v1/voyage-reports` — List voyage reports (paginated, filterable)
- [ ] `GET /api/v1/voyage-reports/{id}` — Get voyage report detail
- [ ] `PATCH /api/v1/voyage-reports/{id}/approve` — Approve voyage report
  - Marks report as APPROVED
  - Triggers FTL recalculation for all crew on the flight (replacing scheduled with actual)
- [ ] `PATCH /api/v1/voyage-reports/{id}/reject` — Reject with reason
  - Marks report as REJECTED; allows re-submission

**Payroll Report:**
- [ ] `GET /api/v1/reports/payroll?month=2026-03` — Monthly aggregation
  ```json
  {
    "month": "2026-03",
    "crewPayroll": [
      {
        "staffId": "SKY-001",
        "name": "John Doe",
        "role": "CAPTAIN",
        "totalFlightHours": 78.5,
        "totalDutyHours": 112.0,
        "dutyDays": 18,
        "perDiemCount": 18
      }
    ],
    "summary": {
      "totalCrew": 120,
      "totalFlightHours": 8940.5
    }
  }
  ```

### D. Business Rules (Domain Layer)

| Rule ID | Rule | Enforcement |
| :--- | :--- | :--- |
| VR-001 | Only Captain or Purser can submit a voyage report | Role-based access check |
| VR-002 | Voyage report can only be submitted for flights with status = ARRIVED | Status check on submission |
| VR-003 | Block-On must be after Block-Off | Validation: Block-On timestamp > Block-Off timestamp |
| VR-004 | Calculated flight hours must be reasonable (> 0 and < 18 hours) | Range validation (configurable) |
| VR-005 | One voyage report per flight (duplicate submission blocked) | Unique constraint: flight_id in voyage_reports |
| VR-006 | Upon approval, flight_hour_logs are created/updated for ALL crew assigned to the flight (not just the submitter) | Batch insert into flight_hour_logs |
| VR-007 | Approved voyage reports are immutable — no further edits or deletes | Status-based write protection |
| VR-008 | FTL is recalculated for all affected crew upon voyage report approval | Event-driven: VoyageReportApproved → FtlRecalculationService |
| VR-009 | Payroll calculation uses only APPROVED voyage reports | Query filter: status = APPROVED |

### E. Flight Hours Calculation Flow

```
function onVoyageReportApproved(voyageReport):
    flight = voyageReport.flight
    actualHours = hoursBetween(voyageReport.actualBlockOff, voyageReport.actualBlockOn)

    // Update flight record with actual times
    flight.actualBlockOff = voyageReport.actualBlockOff
    flight.actualBlockOn = voyageReport.actualBlockOn
    flightRepository.save(flight)

    // Create flight_hour_logs for ALL crew assigned to this flight
    assignments = rosterAssignmentRepository.findByPairingAndFlight(flight)
    for each assignment in assignments:
        log = FlightHourLog(
            crewMemberId = assignment.crewMember.id,
            flightId = flight.id,
            voyageReportId = voyageReport.id,
            flightDate = flight.scheduledDeparture.toLocalDate(),
            flightHours = actualHours,
            dutyHours = calculateDutyHours(assignment, flight),
            blockOff = voyageReport.actualBlockOff,
            blockOn = voyageReport.actualBlockOn
        )
        flightHourLogRepository.save(log)

    // Trigger FTL recalculation for all affected crew
    for each assignment in assignments:
        ftlRecalculationService.recalculate(assignment.crewMember.id)
```

## 5. Data & Privacy

- **Data Attributes**: Actual block times, calculated flight hours, delay codes, payroll aggregations.
- **Retention**: Voyage reports retained for 5 years (regulatory + payroll audit requirement).
- **Access Control**: Captain/Purser can submit. ROSTER_OFFICER reviews/approves. ADMIN exports payroll.

## 6. Risk Mitigation

| Risk | Fallback Strategy |
| :--- | :--- |
| Captain forgets to submit voyage report | System reminder notification 2 hours after landing; Roster Officer can view flights missing reports |
| Incorrect block times submitted | Reasonableness check (VR-004); Roster Officer review before approval; comparison with scheduled times |
| Payroll report includes unapproved reports | VR-009 enforces filter on approved-only; report header shows count of missing/pending reports |
| FTL recalculation race condition with concurrent roster assignments | Process FTL recalculation asynchronously via a domain event queue; use optimistic locking on flight_hour_logs |
