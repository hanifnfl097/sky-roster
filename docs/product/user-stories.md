# Global User Stories (Epics) — SkyRoster

This document tracks the **High-Level Epics** of the SkyRoster Aviation Crew Management System. Fine-grained feature stories are in `docs/features/[feature-name]/prd.md`.

## Core Epics

### 1. Authentication & Authorization
- As a **System Admin**, I want to securely log into the web dashboard so that I can manage all crew operations.
- As a **Crew Member (Captain/FO/Purser/FA)**, I want to securely log into the mobile app so that I can access my roster and perform operational tasks.
- As a **System Admin**, I want to assign granular roles (ADMIN, ROSTER_OFFICER, CREW) so that access control follows the principle of least privilege.

### 2. Crew Profile & License Gatekeeper
- As a **System Admin**, I want to register crew members with their full profile (role, base, type rating) so that the system has accurate master data for rostering.
- As a **Roster Officer**, I want to be warned 30/14/7 days before any crew document expires so that I can proactively manage crew currency.
- As a **System**, I want to automatically ground any crew member whose mandatory documents (MEDEX, License, SEP, Type Rating, Sim Check) have expired so that non-compliant crew are hard-blocked from being rostered.

### 3. Flight Time Limitation (FTL) Rule Engine
- As a **Roster Officer**, I want the system to enforce CASR 121 / ICAO Flight Time Limitations (30h/7d, 100h/month, 1050h/year) so that regulatory compliance is guaranteed.
- As a **Roster Officer**, I want the system to enforce a minimum 12-hour rest period between Block-On and next Sign-On so that crew fatigue rules are never violated.
- As a **System Admin**, I want a full audit trail of every FTL calculation so that we have evidence for regulatory audits.

### 4. Rostering, Pairing & Crew Complement
- As a **Roster Officer**, I want to create flight pairings (multi-leg sequences like CGK-SUB-DPS) so that crew assignments are efficient.
- As a **Roster Officer**, I want the system to enforce crew complement rules per aircraft type (e.g., B737 = 1 Captain + 1 FO + 1 Purser + min 3 FA) so that no under-crewed flight can be published.
- As a **Roster Officer**, I want to publish a finalized roster so that crew members see their assignments on the mobile app.

### 5. Day of Operations (Sign-On, Geofencing, Disruption)
- As a **Crew Member**, I want to perform a geofenced Sign-On via the mobile app 120 minutes before ETD so that my attendance is digitally recorded at the Crew Center.
- As a **System**, I want to detect No-Show or Sick crew and automatically alert the Roster Officer to deploy matching Standby Crew so that flight operations are not disrupted.
- As a **Roster Officer**, I want to see real-time crew sign-on status on the admin dashboard so that I can monitor Day of Operations.

### 6. Post-Flight: Block Times & Voyage Report
- As a **Captain/Purser**, I want to input actual Block-Off and Block-On times via the mobile app after landing so that FTL is calculated on real data.
- As a **System**, I want to aggregate actual flight hours per crew member per month so that HR Payroll can extract Per Diem and Allowance data.
- As a **System Admin**, I want all voyage reports to have an immutable audit trail so that historical records are tamper-proof.

### 7. Notifications & Alerts
- As a **Crew Member**, I want to receive push notifications for new roster assignments, roster changes, and document expiry warnings so that I stay informed.
- As a **Roster Officer**, I want to receive real-time alerts for operational disruptions (No-Show, Sick) so that I can act immediately.
