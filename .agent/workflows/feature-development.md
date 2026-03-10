---
description: Workflow for building a new feature from scratch (Idea -> Code -> Test).
---

# Feature Development Workflow

This workflow orchestrates the entire team to build a feature safely and systematically.

## 1. Product Definition (PM)
**Trigger**: User says "I want to build feature X."
- **Action**: `@product-management` creates `docs/features/[feature-name]/prd.md`.
- **Validation**: User approves PRD.

## 2. Technical Design (Backend + UX)
**Trigger**: PRD exists.
- **Action 1**: `@ux-design` creates `docs/features/[feature-name]/design.md` (Wireflow).
- **Action 2**: `@backend-dev` updates `database-schema.md` and creates `docs/features/[feature-name]/api.md`.
- **Action 3** (If applicable): `@backend-dev` creates Architecture Decision Record in `docs/decisions/ADR-[N].md`.

## 3. Implementation (Devs)
**Trigger**: Design & API Specs exist.
// turbo
- **Action 1**: `@backend-dev` implements API & Unit Tests.
- **Action 2**: `@frontend-dev` implements UI Components & Logic.
- **Rule**: Follow `code-quality-standards` and `security-guardrails` rules.

## 4. Code Review
**Trigger**: Implementation complete.
- **Action**: `@code-review` performs review using `/code-review` workflow.
- **Gate**: All 🔴 Critical and 🟠 Major findings must be resolved before proceeding.

## 5. Verification (QA)
**Trigger**: Code review passed.
- **Action**: `@quality-assurance` creates `docs/features/[feature-name]/test.md` and runs checks.
- **Rule**: Must include happy path + error path + AI evaluation (if applicable).

## 6. Deployment
**Trigger**: QA Passed.
- **Action 1**: Merge to `develop` branch → auto-deploy to staging.
- **Action 2**: Run smoke tests on staging environment.
- **Action 3**: After staging validation, merge to `main` → deploy to production with approval.
- **Post-Deploy**: Monitor error rates and P95 latency for 30 minutes.

## 7. Documentation Update
**Trigger**: Feature deployed.
- **Action**: Run `/documentation-update` workflow to sync API inventory, env config, and changelog.