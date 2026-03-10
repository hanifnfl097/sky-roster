# Documentation Standards & Architecture

This directory serves as the **Single Source of Truth** for the entire product development lifecycle. In an Agentic Workflow, these documents are not just for humans—they are the context instructions for our AI Agents.

## 📂 Directory Structure

```bash
docs/
├── README.md                # (You are here) Guide to documentation
├── standards/               # Global Guidelines/Standards
│   ├── api/                 # API Guidelines (split by topic)
│   │   ├── api-versioning.md      # API Versioning Rules
│   │   ├── naming-convention.md   # URL Naming & HTTP Methods
│   │   ├── response-structure.md  # Standard Response Format
│   │   └── status-code.md         # HTTP Status Codes Usage
│   └── skill-best-practices.md  # Best practices for writing skills
├── product/                 # Product Specifications (PM Domain)
│   ├── app-structure.md     # App Structure
│   ├── user-flows.md        # Diagrams of core journeys
│   └── user-stories.md      # High-level stories not tied to specific features
├── templates/               # MASTER TEMPLATES (Copy to create new docs)
│   ├── ADR_TEMPLATE.md              # Architecture Decision Records
│   ├── API_INVENTORY_TEMPLATE.md    # Master API listing
│   ├── API_SPEC_TEMPLATE.md         # Per-feature API specification
│   ├── APP_STRUCTURE_TEMPLATE.md    # Application layout & wireframes
│   ├── CHANGELOG_TEMPLATE.md        # Version changelog
│   ├── DB_SCHEMA_TEMPLATE.md        # Database schema design
│   ├── DEPLOYMENT_GUIDE_TEMPLATE.md # Deployment procedures
│   ├── ENV_CONFIG_TEMPLATE.md       # Environment variables documentation
│   ├── FEATURE_DESIGN_TEMPLATE.md   # Feature-specific UI specs
│   ├── MIGRATION_PLAN_TEMPLATE.md   # Data/system migration planning
│   ├── PRD_TEMPLATE.md              # Product Requirements Document
│   ├── RUNBOOK_TEMPLATE.md          # Production troubleshooting guide
│   ├── TECH_SPIKE_TEMPLATE.md       # Technical research report
│   ├── TEST_PLAN_TEMPLATE.md        # Test plan & QA strategy
│   ├── UI_GUIDELINES_TEMPLATE.md    # Visual standards & design system
│   ├── USER_FLOWS_TEMPLATE.md       # Sitemap & navigation diagrams
│   └── USER_STORIES_TEMPLATE.md     # High-level user epics
├── examples/                # Example filled documents (for reference)
│   ├── prd-login-example.md         # Example: Login feature PRD
│   └── api-spec-auth-example.md     # Example: Auth API specification
├── features/
│   └── [feature-name]/          # Feature-Specific Documentation
│       ├── api.md               # API spec for this feature
│       ├── design.md            # UI specs for this feature
│       ├── prd.md               # Requirements for this feature
│       └── test.md              # Test Plan for this feature
├── decisions/               # Architecture Decision Records
│   └── ADR-001.md           # (Use ADR_TEMPLATE.md)
├── bugs/                    # Bug reports
│   └── BUG-001.md           # (From bug-fix workflow)
└── tech/                    # Technical Implementation Details
    ├── api-inventory.md     # Master API List
    ├── database-schema.md   # Master DB Schema
    ├── env-config.md        # Environment variable documentation
    └── deployment-guide.md  # Deployment procedures
```

## 📝 Document Creation Guide

When a role starts a task, they should **COPY** the relevant template to the appropriate folder.

### 1. Product Manager (@product-management)
*   **To Define Product**: Copy `USER_STORIES_TEMPLATE.md` → `docs/product/user-stories.md`.
*   **To Define Architecture**: Copy `APP_STRUCTURE_TEMPLATE.md` → `docs/product/app-structure.md`.
*   **To Define Feature**: Copy `PRD_TEMPLATE.md` → `docs/features/[feature-name]/prd.md`.

### 2. Frontend & Design (@ux-design)
*   **To Define Style**: Copy `UI_GUIDELINES_TEMPLATE.md` → `docs/standards/ui-guidelines.md`.
*   **To Define Feature UI**: Copy `FEATURE_DESIGN_TEMPLATE.md` → `docs/features/[feature-name]/design.md`.

### 3. Backend Developer (@backend-dev)
*   **To Define DB**: Copy `DB_SCHEMA_TEMPLATE.md` → `docs/tech/database-schema.md`.
*   **To Define API List**: Copy `API_INVENTORY_TEMPLATE.md` → `docs/tech/api-inventory.md`.
*   **To Define Feature API**: Copy `API_SPEC_TEMPLATE.md` → `docs/features/[feature-name]/api.md`.
*   **To Define Env Config**: Copy `ENV_CONFIG_TEMPLATE.md` → `docs/tech/env-config.md`.

### 4. DevOps (@devops-deployment)
*   **To Define Deployment**: Copy `DEPLOYMENT_GUIDE_TEMPLATE.md` → `docs/tech/deployment-guide.md`.
*   **To Create Runbook**: Copy `RUNBOOK_TEMPLATE.md` → `docs/tech/runbook-[service].md`.

### 5. QA (@quality-assurance)
*   **To Define Tests**: Copy `TEST_PLAN_TEMPLATE.md` → `docs/features/[feature-name]/test.md`.

### 6. Architecture Decisions (Any Role)
*   **To Record Decision**: Copy `ADR_TEMPLATE.md` → `docs/decisions/ADR-[N].md`.
*   **To Document Research**: Copy `TECH_SPIKE_TEMPLATE.md` → `docs/decisions/SPIKE-[name].md`.

### 7. Release Management
*   **To Track Changes**: Copy `CHANGELOG_TEMPLATE.md` → `CHANGELOG.md` (project root).
*   **To Plan Migration**: Copy `MIGRATION_PLAN_TEMPLATE.md` → `docs/tech/migration-[name].md`.

## 🚀 Workflow

1.  **PM** creates the PRD.
2.  **Backend** reads PRD → creates API Spec + DB Schema.
3.  **Frontend** reads API Spec → builds UI (mocked).
4.  **AI Engineer** reads DB Schema → builds RAG pipeline.
5.  **QA** verifies against PRD Acceptance Criteria.
6.  **DevOps** deploys using Deployment Guide.

## 📚 Examples

See `docs/examples/` for filled-in templates that demonstrate how to use the templates in practice:
- `prd-login-example.md` — A complete PRD for a Login feature.
- `api-spec-auth-example.md` — A complete API spec for Auth endpoints.
