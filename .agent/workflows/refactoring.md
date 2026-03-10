---
description: Workflow for code refactoring to improve quality without changing functionality.
---

# Refactoring Workflow

## 1. Identify Scope
**Trigger**: Code smell detected, tech debt flagged, or team decision to refactor.
- **Action**: Document what will be refactored and why.
- **Rule**: Define clear boundaries — what is IN scope and OUT of scope.

## 2. Baseline Tests
- **Action**: Ensure all existing tests pass BEFORE refactoring.
- **Rule**: If there are no tests for the affected code, write them first.
  - This establishes a safety net for the refactoring.

## 3. Incremental Changes
- **Action**: Apply refactoring in small, focused commits.
- **Rules**:
  - One commit = one refactoring step (rename, extract, move).
  - Do NOT mix refactoring with feature additions or bug fixes.
  - Run tests after each commit to catch regressions early.

## 4. Documentation Update
- **IF** public interfaces changed: Update `docs/features/[feature-name]/api.md`.
- **IF** folder structure changed: Update project README.
- **IF** architectural decision made: Create `docs/decisions/ADR-[N].md`.

## 5. Review
- **Action**: Submit for code review using `/code-review` workflow.
- **Validation**: All existing tests still pass, no functionality changed.
