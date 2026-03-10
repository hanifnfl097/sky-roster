---
description: Workflow for updating documentation when features change or are completed.
---

# Documentation Update Workflow

## 1. Identify Scope
**Trigger**: Feature completed, API changed, or architecture decision made.
- **Action**: Determine which docs are affected by the change.

## 2. Update Feature Docs
- **IF** API changed: Update `docs/features/[feature-name]/api.md`.
- **IF** UI changed: Update `docs/features/[feature-name]/design.md`.
- **IF** Requirements changed: Update `docs/features/[feature-name]/prd.md`.
- **IF** Tests changed: Update `docs/features/[feature-name]/test.md`.

## 3. Update Technical Docs
- **IF** New endpoint added: Update `docs/tech/api-inventory.md`.
- **IF** Schema changed: Update `docs/tech/database-schema.md`.
- **IF** Environment variable added: Update `docs/tech/env-config.md` and `.env.example`.

## 4. Update Standards (If Applicable)
- **IF** New pattern established: Document in `docs/standards/`.
- **IF** Architecture decision made: Create `docs/decisions/ADR-[N].md`.

## 5. Review
- **Action**: User approves documentation changes.
- **Rule**: Documentation changes should be committed alongside code changes.
