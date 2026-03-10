---
trigger: always_on
---

# Git Commit & Branch Standards

## Principle
**"Clean History, Clear Intent"**

Every commit and branch must communicate intent clearly.

## Commit Message Format (Conventional Commits)

```
<type>(<scope>): <short description>

[optional body]
[optional footer]
```

### Types
| Type | Usage |
| :--- | :--- |
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `refactor` | Code change that neither fixes a bug nor adds a feature |
| `test` | Adding or updating tests |
| `chore` | Build process, dependencies, tooling |
| `style` | Formatting, whitespace (no logic change) |
| `perf` | Performance improvement |
| `ci` | CI/CD configuration changes |

### Rules
1. Use **English** for all commit messages.
2. Use **imperative mood**: "Add feature" not "Added feature".
3. Keep the first line under **72 characters**.
4. One commit = one logical change. Do not bundle unrelated changes.

### Examples
- ✅ `feat(auth): add JWT refresh token endpoint`
- ✅ `fix(cart): resolve race condition on concurrent checkout`
- ✅ `docs(api): update response format for /users endpoint`
- ❌ `update stuff`
- ❌ `WIP`
- ❌ `fix bug`

## Branch Naming Convention

```
<type>/<short-description>
```

| Type | Usage | Example |
| :--- | :--- | :--- |
| `feature/` | New feature development | `feature/user-authentication` |
| `fix/` | Non-critical bug fix | `fix/login-validation-error` |
| `hotfix/` | Critical production fix | `hotfix/payment-gateway-crash` |
| `release/` | Release preparation | `release/v1.2.0` |
| `refactor/` | Code refactoring | `refactor/user-service-cleanup` |
| `docs/` | Documentation update | `docs/api-spec-update` |

### Rules
1. Use **kebab-case** for branch names.
2. Keep names descriptive but short (max 5 words after prefix).
3. Delete branches after merging.

## Protocol
- **When creating commits**: Apply Conventional Commits format automatically.
- **When creating branches**: Apply naming convention automatically.
