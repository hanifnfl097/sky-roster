# Application Structure & Wireframes — SkyRoster

**Version:** 1.0
**Last Updated:** 2026-03-09

---

## 1. Platform Overview

SkyRoster is a **3-platform system**:
| Platform | Technology | Target User |
| :--- | :--- | :--- |
| **Web Admin** | Next.js + Tailwind CSS | System Admin, Roster Officer |
| **API Backend** | Java Spring Boot + PostgreSQL | All platforms (RESTful API) |
| **Mobile App** | Flutter (Android & iOS) | Crew Members (Captain, FO, Purser, FA) |

---

## 2. Web Admin — Global Navigation (Sitemap)

| Page Name | Route Path | Layout Type | Access |
| :--- | :--- | :--- | :--- |
| **Login** | `/login` | Auth Layout (Centered) | Public |
| **Dashboard** | `/dashboard` | Sidebar Layout | ADMIN, ROSTER_OFFICER |
| **Crew List** | `/crew` | Sidebar Layout | ADMIN, ROSTER_OFFICER |
| **Crew Detail** | `/crew/:id` | Sidebar Layout | ADMIN, ROSTER_OFFICER |
| **Fleet Management** | `/fleet` | Sidebar Layout | ADMIN |
| **Schedule / Pairing** | `/pairings` | Sidebar Layout | ROSTER_OFFICER |
| **Pairing Builder** | `/pairings/new` | Sidebar Layout | ROSTER_OFFICER |
| **Roster Board** | `/roster` | Sidebar Layout | ROSTER_OFFICER |
| **Day of Ops** | `/operations` | Sidebar Layout | ROSTER_OFFICER |
| **Voyage Reports** | `/reports/voyage` | Sidebar Layout | ADMIN, ROSTER_OFFICER |
| **Payroll Export** | `/reports/payroll` | Sidebar Layout | ADMIN |
| **Audit Logs** | `/audit` | Sidebar Layout | ADMIN |
| **Settings** | `/settings` | Sidebar Layout | ADMIN |

---

## 3. Layout Definitions

### A. Sidebar Layout
*Fixed sidebar on the left, scrollable content on the right.*
- **Sidebar (Width: 280px)**:
  - Logo: "SkyRoster" (Top, with aviation icon)
  - Navigation Groups:
    - **Operations**: Dashboard, Day of Ops
    - **Crew**: Crew List, Fleet
    - **Planning**: Pairings, Roster Board
    - **Reports**: Voyage Reports, Payroll Export
    - **System**: Audit Logs, Settings
  - User Profile (Bottom): Avatar + Role Badge + Logout
- **Main Content**:
  - Breadcrumb navigation bar (top)
  - `max-w-screen-2xl mx-auto p-6` container
- **Mobile Behavior**: Sidebar collapses to a hamburger menu drawer.

### B. Auth Layout
*Centered card on a gradient background.*
- Full-screen gradient (dark navy → deep blue)
- Centered card: Logo + Login form + "Powered by SkyRoster"

---

## 4. Web Admin — Page Specifications (Wireframes)

### Page: Dashboard (`/dashboard`)

#### Zone 1: Header
- **Title**: "Operations Dashboard" (H1)
- **Subtitle**: Today's date + "Good Morning/Afternoon, {User}" greeting

#### Zone 2: Stats Grid (4 Cols)
- **Card 1**: "Active Crew" (Icon: 👨‍✈️ + Total count / Grounded count in red)
- **Card 2**: "Today's Flights" (Icon: ✈️ + Number + On-Time %)
- **Card 3**: "Expiring Documents" (Icon: ⚠️ + Count in next 30 days)
- **Card 4**: "FTL Warnings" (Icon: ⏱️ + Crew count approaching limits)

#### Zone 3: Today's Operations Table
- **Columns**: Flight No, Route, ETD, Crew Status (Badge: Complete/Incomplete), Sign-On Status
- **Row Action**: Click → Navigate to `/operations/:flightId`
- **Empty State**: "No flights scheduled for today."

#### Zone 4: Document Expiry Alerts (Side Panel / Collapsed)
- List of crew with documents expiring in < 30 days
- Color-coded: 🔴 < 7 days, 🟠 < 14 days, 🟡 < 30 days

---

### Page: Crew List (`/crew`)

#### Zone 1: Toolbar
- **Search**: Search by name, staff ID
- **Filters**: Role (Captain/FO/Purser/FA), Status (Active/Grounded), Base Station
- **Action**: "+ Add Crew" Button (Primary)

#### Zone 2: Crew Table
- **Columns**: Staff ID, Name, Role (Badge), Type Ratings, Status (Active 🟢 / Grounded 🔴), MEDEX Expiry
- **Row Action**: Click → `/crew/:id`

---

### Page: Crew Detail (`/crew/:id`)

#### Zone 1: Profile Header
- **Left**: Avatar, Full Name, Staff ID, Role Badge, Base Station
- **Right**: Status Badge (Active/Grounded), "Edit" Button

#### Zone 2: Tabs
- **Tab 1 — Documents**: Table of all credentials (Type, Issue Date, Expiry Date, Status Badge, Upload action)
- **Tab 2 — FTL Summary**: Accumulated hours (7-day, Monthly, Yearly) with progress bars and limits
- **Tab 3 — Roster History**: Past and upcoming assigned pairings (Timeline view)
- **Tab 4 — Audit Log**: All status changes for this crew member

---

### Page: Pairing Builder (`/pairings/new`)

#### Zone 1: Pairing Form
- **Date Selector**: Departure date
- **Legs Builder**: Dynamic form to add flight legs (Flight No, Origin IATA, Destination IATA, STD, STA)
- **Aircraft Type Selector**: Dropdown (B737, A320, etc.) — triggers crew complement rules

#### Zone 2: Crew Assignment Panel
- **Required Complement**: Shows required roles based on aircraft type
- **Crew Picker**: Searchable list filtered by role + type rating + eligibility (not grounded, FTL ok)
- **Validation Panel**: Real-time validation showing ✅ Pass / ❌ Fail for each rule (FTL, rest, complement, documents)

---

### Page: Roster Board (`/roster`)

#### Zone 1: Timeline View (Gantt-Style)
- **Y-Axis**: Crew members (grouped by role)
- **X-Axis**: Dates (week view, scrollable)
- **Entries**: Color-coded blocks (Flight, Standby, Off, Leave)
- **Drag & Drop**: Reassign pairings with validation feedback

#### Zone 2: Publish Panel
- **Button**: "Publish Roster" (Disabled if any validation errors)
- **Summary**: Total crew assigned, unassigned flights, FTL warnings

---

### Page: Day of Operations (`/operations`)

#### Zone 1: Flight Board
- **Table**: Flight No, Route, ETD, ATD, Status (On-Time/Delayed/Departed)
- **Crew Column**: Expandable to show each crew member's sign-on status (✅ Signed On, ⏳ Pending, ❌ No-show)

#### Zone 2: Disruption Alerts (Real-Time)
- **Alert Cards**: "[Crew Name] — No Show for [Flight No]. Standby crew available: [List]"
- **Action Button**: "Deploy Standby" → Reassign with one click

---

## 5. Mobile App (Flutter) — Screen Map

| Screen Name | Route | Access |
| :--- | :--- | :--- |
| **Login** | `/login` | Public |
| **Home / Dashboard** | `/home` | CREW |
| **My Roster** | `/roster` | CREW |
| **Flight Detail** | `/flight/:id` | CREW |
| **Sign-On** | `/sign-on/:flightId` | CREW |
| **Voyage Report** | `/voyage-report/:flightId` | Captain, Purser |
| **Documents** | `/documents` | CREW |
| **Profile** | `/profile` | CREW |
| **Notifications** | `/notifications` | CREW |

### Screen: Home / Dashboard
- **Greeting**: "Hello, Capt. {Name}" with role badge
- **Next Flight Card**: Flight No, Route, ETD, Countdown timer, "Sign-On" CTA button
- **Quick Stats**: Hours This Month / Hours This Year (circular progress)

### Screen: Sign-On
- **Geolocation Check**: Map showing crew center perimeter + current GPS position
- **Status**: "You are within the Crew Center area ✅" or "You are outside the geofence ❌"
- **Action**: "Confirm Sign-On" Button (disabled if outside geofence)
- **Confirmation**: Success screen with timestamp

### Screen: Voyage Report (`/voyage-report/:flightId`)
- **Form Fields**: Actual Block-Off time, Actual Block-On time, Delay reason (dropdown), Remarks (textarea)
- **Action**: "Submit Report" → Confirms with a summary dialog
- **Read-Only Mode**: After submission, the form becomes read-only audit record

---

## 6. Interaction Rules
- **Modals**: Confirmation dialogs for destructive actions (Publish Roster, Deploy Standby, Ground Crew).
- **Toasts**: Success toast (Top-Right) after form submissions. Error toast for validation failures.
- **Transitions**: Smooth page transitions using CSS transitions / Framer Motion (Web) and Flutter Hero animations (Mobile).
- **Loading States**: Skeleton loaders for data-heavy pages (Crew List, Roster Board). Shimmer effect on mobile.
- **Real-Time Updates**: WebSocket for Day of Operations page (crew sign-on status, disruption alerts).
