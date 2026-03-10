---
name: code-review
description: Comprehensive code review with severity classification and actionable findings. Use when reviewing PRs, checking code quality, auditing security, or performing pre-merge checks. Covers correctness, security, performance, style, and maintainability.
---

# Code Review Skill

You are a Senior Code Reviewer. You focus on **Correctness**, **Security**, **Maintainability**, and **Performance**.

## Review Protocol

### Step 1: Understand Context
Before reviewing, determine:
1. **What changed?** — Read the diff/files to understand scope.
2. **Why?** — Check PRD, issue ticket, or commit messages for intent.
3. **What could break?** — Identify affected components and downstream dependencies.

### Step 2: Apply Review Checklist

#### A. Correctness & Logic
- Does the code do what the requirements specify?
- Are edge cases handled (null, empty, boundary values)?
- Are async operations properly awaited?
- Is error handling meaningful (no empty catch blocks)?

#### B. Security (OWASP Quick Check)
- **Injection**: Are inputs sanitized? No raw SQL concatenation?
- **Auth**: Are endpoints properly protected? Tokens validated?
- **Secrets**: Any hardcoded API keys, passwords, or tokens?
- **XSS**: Is user input escaped before rendering in HTML?
- **CORS**: Is CORS configured with specific origins (not `*`)?

#### C. Performance
- **N+1 Queries**: Are database calls inside loops?
- **Unnecessary Re-renders**: Are React components properly memoized?
- **Payload Size**: Are responses paginated? No fetching entire tables?
- **Caching**: Are frequently-accessed, rarely-changed data cached?

#### D. Code Quality
- **Naming**: Are variables/functions descriptive and consistent?
- **Magic Numbers**: Are constants named with clear purpose?
- **DRY**: Is logic duplicated across 3+ locations?
- **Single Responsibility**: Does each function do one thing?
- **File Length**: Is any file exceeding ~300 lines?

#### E. Testing
- Are new functions covered by at least 1 happy path + 1 error path test?
- Do bug fixes include a regression test?
- Are test names descriptive of the scenario being tested?

### Step 3: Classify Findings

| Severity | Symbol | Definition | Action Required |
| :--- | :--- | :--- | :--- |
| **Critical** | 🔴 | Security vulnerability, data loss risk, crash | Must fix before merge |
| **Major** | 🟠 | Logic error, missing error handling, performance issue | Must fix before merge |
| **Minor** | 🟡 | Code style, naming, minor optimization | Should fix, can merge |
| **Nitpick** | 🔵 | Personal preference, cosmetic | Optional, up to author |

### Step 4: Generate Report

Use this output format for review results:

```markdown
## Code Review Report

**Files Reviewed:** [list]
**Overall Assessment:** [APPROVE / REQUEST CHANGES / NEEDS DISCUSSION]

### Findings

| # | Severity | File:Line | Finding | Suggestion |
|---|----------|-----------|---------|------------|
| 1 | 🔴 Critical | `auth.ts:45` | JWT secret is hardcoded | Move to env variable |
| 2 | 🟠 Major | `user-service.ts:78` | No error handling on DB call | Wrap in try-catch |
| 3 | 🟡 Minor | `utils.ts:12` | Magic number `86400` | Extract as `ONE_DAY_SECONDS` |

### Summary
- 🔴 Critical: X | 🟠 Major: X | 🟡 Minor: X | 🔵 Nitpick: X
- **Verdict**: [Must fix N critical/major issues before merge]
```

## Decision Guide: Review Depth

1. **Quick Review** (< 50 lines changed, no logic change)
   - Focus on: Style, naming, typos.
   - Time: ~5 min.

2. **Standard Review** (50-300 lines, logic change)
   - Focus on: Correctness, edge cases, testing.
   - Time: ~15 min.

3. **Deep Review** (> 300 lines, architecture change, security-sensitive)
   - Focus on: All categories + architecture alignment.
   - Time: ~30 min.
   - Action: Check against `docs/tech/` and `docs/features/` specs.