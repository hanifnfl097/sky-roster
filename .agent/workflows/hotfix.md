---
description: Workflow for critical production bug fixes that need immediate resolution.
---

# Hotfix Workflow

## 1. Triage (Immediate)
**Trigger**: Critical bug reported in production.
- **Action**: Assess severity and impact.
- **Rule**: Only proceed with hotfix for **Critical** or **High** severity bugs that affect production users.

## 2. Branch Creation
- **Action**: Create `hotfix/[short-description]` branch from `main` (or production branch).
- **Rule**: NEVER branch from `develop` for hotfixes.

## 3. Minimal Fix (Dev)
- **Action**: `@backend-dev` or `@frontend-dev` applies the minimal fix.
- **Rules**:
  - Fix the bug ONLY. No feature additions, no refactoring.
  - If the fix requires a logic change, document it in `docs/bugs/BUG-[ID].md`.
  - Add a regression test that reproduces the original bug.

## 4. Fast-Track QA
- **Action**: `@quality-assurance` verifies:
  - [ ] Bug is resolved (reproduction steps no longer trigger the issue).
  - [ ] Regression test passes.
  - [ ] No new issues introduced (smoke test core flows).

## 5. Deploy & Merge
- **Action**: Merge hotfix branch into BOTH `main` AND `develop`.
- **Rule**: Deploy to production immediately after QA approval.
- **Post-Deploy**: Monitor error rates for 30 minutes.

## 6. Post-Mortem (Optional)
- **Action**: Document root cause and prevention strategy in `docs/bugs/BUG-[ID].md`.
