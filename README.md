# SkyRoster — Enterprise Aviation Crew Management System

An enterprise-grade aviation crew management system that handles **Flight Crew** (Captain, First Officer) and **Cabin Crew** (Purser, Flight Attendant) with strict CASR 121 / ICAO regulatory compliance.

## Tech Stack

| Platform | Technology | Directory |
| :--- | :--- | :--- |
| **API Backend** | Java 17, Spring Boot 3.5.0, PostgreSQL, JWT, Hibernate/JPA | `backend/` |
| **Web Admin** | Next.js 15, React, TypeScript, Tailwind CSS | `web-admin/` |
| **Mobile App** | Flutter 3.38, Dart, BLoC Architecture | `mobile-app/` |

## Prerequisites

- Java 17+
- Node.js 22+
- Flutter 3.38+
- PostgreSQL 15+
- Maven (included via wrapper: `./mvnw`)

## Getting Started

### 1. Clone & Configure

```bash
git clone <repository-url>
cd skyroster

# Backend
cp backend/.env.example backend/.env
# Fill in DB_USERNAME, DB_PASSWORD, JWT_SECRET

# Web Admin
cp web-admin/.env.example web-admin/.env.local
```

### 2. Start Backend

```bash
cd backend
./mvnw spring-boot:run
# API available at http://localhost:8080/api/v1
```

### 3. Start Web Admin

```bash
cd web-admin
npm install
npm run dev
# Dashboard available at http://localhost:3000
```

### 4. Start Mobile App

```bash
cd mobile-app
flutter pub get
flutter run
```

## Project Structure

```
skyroster/
├── backend/               # Spring Boot API (Clean Architecture)
│   └── src/main/java/com/skyroster/
│       ├── domain/        # Business rules (ZERO framework deps)
│       ├── application/   # DTOs, Mappers, Schedulers
│       ├── adapter/       # Controllers, JPA, Notifications
│       └── config/        # Spring Configuration
├── web-admin/             # Next.js Admin Dashboard
│   └── src/
│       ├── app/           # App Router pages
│       ├── components/    # UI + Feature components
│       ├── services/      # API client functions
│       └── types/         # TypeScript types
├── mobile-app/            # Flutter Crew App
│   └── lib/
│       ├── domain/        # Entities, Use Cases
│       ├── data/          # Repositories, Models
│       └── presentation/  # BLoC, Pages, Widgets
└── docs/                  # Specifications & Architecture
    ├── product/           # User Stories, App Structure
    ├── tech/              # DB Schema, API Inventory, Env Config
    ├── features/          # Per-feature PRDs
    ├── decisions/         # Architecture Decision Records
    └── standards/         # UI Guidelines
```

## Documentation

| Document | Description |
| :--- | :--- |
| [User Stories](docs/product/user-stories.md) | 7 core epics |
| [App Structure](docs/product/app-structure.md) | Sitemap & wireframes |
| [Database Schema](docs/tech/database-schema.md) | 16 tables with ER diagram |
| [API Inventory](docs/tech/api-inventory.md) | 50+ REST endpoints |
| [ADR-001](docs/decisions/ADR-001.md) | Clean Architecture decision |

## Branch Naming

```
feature/   → New features (e.g., feature/crew-management)
fix/       → Bug fixes
hotfix/    → Critical production fixes
release/   → Release preparation
```

## Commit Format

```
<type>(<scope>): <description>
feat(crew): add document expiry cron job
fix(ftl): correct 7-day rolling window calculation
```