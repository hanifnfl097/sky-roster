---
trigger: always_on
---

# Code Quality Standards

## Principle
**"Ship Clean, Not Fast"**

All generated code must meet baseline quality standards. These rules prevent tech debt from accumulating.

## Naming Conventions

| Element | Convention | Example |
| :--- | :--- | :--- |
| Variables / Functions | camelCase (JS/TS), snake_case (Python/Go) | `getUserById`, `get_user_by_id` |
| Classes / Types | PascalCase | `UserService`, `OrderItem` |
| Constants | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `API_BASE_URL` |
| Files | kebab-case | `user-service.ts`, `order-item.py` |
| Database Tables | snake_case (plural) | `user_accounts`, `order_items` |
| Database Columns | snake_case | `created_at`, `is_active` |

## Code Structure Rules

1. **No Magic Numbers/Strings**: All constants must be named.
   - ❌ `if (status === 3)` / `setTimeout(fn, 86400000)`
   - ✅ `if (status === STATUS.APPROVED)` / `setTimeout(fn, ONE_DAY_MS)`

2. **No Empty Catch Blocks**: Every `catch` must log, re-throw, or handle meaningfully.
   - ❌ `catch(e) {}`
   - ✅ `catch(e) { logger.error('Payment failed', e); throw e; }`

3. **No TODO Without Tracking**: Every `// TODO` must reference a ticket or issue.
   - ❌ `// TODO: fix this later`
   - ✅ `// TODO(PROJ-123): Add retry logic for transient failures`

4. **Single Responsibility**: One function = one job. Max ~50 lines per function, ~300 lines per file. Refactor if exceeded.

5. **DRY Threshold**: If logic is repeated 3+ times, extract into a shared utility or helper.

6. **Explicit Return Types**: Functions should have explicit return types (TypeScript) or docstrings (Python).

## Error Handling

1. **Fail Fast**: Validate inputs at function entry, return early on invalid state.
2. **Custom Error Classes**: Use domain-specific error types, not generic `Error` or `Exception`.
3. **User-Facing vs Internal**: Never expose stack traces or internal error messages to end users.

## SOLID Principles

1. **S — Single Responsibility**: One function = one job. One class = one reason to change.
2. **O — Open/Closed**: Code should be open for extension, closed for modification. Use interfaces/abstractions to add behavior without modifying existing code.
   - ✅ Add new payment provider by implementing `PaymentProvider` interface.
   - ❌ Modify existing `PaymentService` with `if/else` for each new provider.
3. **L — Liskov Substitution**: Subtypes must be substitutable for their base types without breaking behavior.
4. **I — Interface Segregation**: Don't force classes to implement methods they don't use. Split large interfaces into smaller, focused ones.
5. **D — Dependency Inversion**: High-level modules should depend on abstractions, not concrete implementations. Inject dependencies, don't hardcode them.
   - ✅ `constructor(private db: DatabasePort)` — accepts any DB implementation.
   - ❌ `constructor() { this.db = new PostgresDB() }` — hardcoded dependency.

## Protocol
- **When generating code**: Apply these standards automatically.
- **When reviewing code**: Flag violations with specific rule reference (e.g., "Violates CQ-1: Magic Number").
