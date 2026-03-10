---
description: Workflow for reviewing code changes before merge (PR Review / Code Audit).
---

# Code Review Workflow

## 1. Preparation
**Trigger**: Developer says "Review my code", "Check this PR", or implementation is complete.
- **Action**: Read the changed files and understand the context.
- **Context**: Check `docs/features/[feature-name]/prd.md` and `api.md` for requirements.

## 2. Automated Checks
// turbo
- **Action**: Run linter, type-checker, and test suite.
- **Pass**: Proceed to Manual Review.
- **Fail**: Return to Developer with specific errors.

## 3. Manual Review (@code-review)
- **Action**: Review against skill checklist:
  - [ ] Correctness & Logic (edge cases, async handling)
  - [ ] Security (OWASP quick check, no hardcoded secrets)
  - [ ] Performance (N+1 queries, payload size)
  - [ ] Code Quality (naming, DRY, single responsibility)
  - [ ] Test Coverage (happy path + error path)

## 4. Report
- **Action**: Generate review report using `@code-review` skill output format.
- **Output**: Markdown table with severity-tagged findings (🔴 Critical → 🔵 Nitpick).

## 5. Resolution
- **IF Critical/Major found**: Developer must fix before merge.
- **IF Minor/Nitpick only**: Developer may merge with acknowledgment.
- **Action**: Re-review after fixes are applied.
