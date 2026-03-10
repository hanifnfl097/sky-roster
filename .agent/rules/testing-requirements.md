---
trigger: always_on
---

# Testing Requirements

## Principle
**"No Merge Without Tests"**

Every code change that modifies logic must include corresponding tests.

## Minimum Test Coverage Rules

1. **New Endpoint / Service Function**:
   - At least **1 happy path** test (valid input → expected output).
   - At least **1 error path** test (invalid input → proper error handling).

2. **Bug Fix**:
   - **Must** include a regression test that reproduces the original bug.
   - The test must fail without the fix and pass with it.

3. **Refactoring**:
   - Existing tests must still pass. No test rewriting unless interfaces change.

## Test Naming Convention

Use descriptive names that explain the scenario:
- ✅ `should return 401 when token is expired`
- ✅ `should calculate total price with discount applied`
- ❌ `test1`
- ❌ `it works`

## Test Organization

- **Unit Tests**: Colocate with source files or in `__tests__/` directory.
- **Integration Tests**: In `tests/integration/` directory.
- **E2E Tests**: In `tests/e2e/` directory.

## Testing Pyramid (Recommended Proportions)

```
         /  E2E  \            5-10%  — Slow, expensive, test critical user flows only
        / Integr. \          20-30%  — API + DB interaction, service boundaries
       /   Unit    \         60-70%  — Fast, isolated, test business logic
      ‾‾‾‾‾‾‾‾‾‾‾‾‾
```

- **Unit Tests** are the foundation. They run fast, are cheap, and catch most bugs.
- **Integration Tests** verify components work together (API → Service → DB).
- **E2E Tests** are the most expensive. Only test critical happy paths (login, checkout, core feature).

## Protocol
- **When generating code**: Automatically include test files for new functions/endpoints.
- **When reviewing code**: Flag missing tests for new logic as 🟠 Major finding.
