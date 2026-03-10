---
description: Workflow for fixing bugs reported by QA or User.
---

# Bug Fix Workflow

## 1. Triage (QA)
**Trigger**: Bug reported by user or detected in testing.
- **Action**: `@quality-assurance` creates a formal report in `docs/bugs/BUG-[ID].md`.
  - Must include: Reproduction Steps, Expected vs Actual, Screenshots/Logs.
- **Severity Assessment**:

| Severity | Definition | Response SLA |
| :--- | :--- | :--- |
| **Critical** | System down, data loss, security breach | 1 hour |
| **High** | Core feature broken, no workaround | 4 hours |
| **Medium** | Feature partially broken, workaround exists | 1 business day |
| **Low** | Cosmetic, minor UX issue | Next sprint |

- **Rule**: Critical/High bugs → use `/hotfix` workflow instead if in production.

## 2. Diagnosis (Dev)
**Trigger**: Bug report created.
- **Action**: `@backend-dev` or `@frontend-dev` identifies the root cause.
- **Document**: Add root cause analysis to `docs/bugs/BUG-[ID].md`:
  - What caused it?
  - Which component(s) are affected?
  - Could this happen elsewhere? (Systemic check)
- **Rule**: If the fix requires a logic change, update `docs/` first!

## 3. Resolution (Dev)
- **Action**: Apply fix + Add Regression Test.
- **Regression Test**: Must reproduce the original bug and verify it's fixed.
  - Test should **fail** without the fix and **pass** with it.
- **Commit**: Use `fix(<scope>): <description>` format.

## 4. Verification (QA)
- **Action**: `@quality-assurance` verifies:
  - [ ] Original bug no longer reproducible.
  - [ ] Regression test exists and passes.
  - [ ] No new issues introduced (smoke test related areas).
- **Closure**: Update `docs/bugs/BUG-[ID].md` with resolution summary.
