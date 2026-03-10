# Migration Plan

**Migration Name:** [e.g., Legacy System to New Platform]
**Status:** [Planning / In Progress / Completed / Rolled Back]
**Owner:** [Name / Team]
**Target Date:** YYYY-MM-DD

## 1. Overview
*What is being migrated and why?*

### Source System
- **Platform**: [e.g., Legacy PHP App, Oracle DB]
- **Data Volume**: [e.g., 500K records, 10GB]
- **Active Users**: [e.g., 200 concurrent]

### Target System
- **Platform**: [e.g., Spring Boot + PostgreSQL]
- **Key Differences**: [e.g., different schema, new auth system]

## 2. Data Mapping

| Source Table/Field | Target Table/Field | Transformation | Notes |
| :--- | :--- | :--- | :--- |
| `users.fullname` | `users.first_name` + `users.last_name` | Split on first space | Handle single-name cases |
| `orders.status` (int) | `orders.status` (enum) | Map: 1→PENDING, 2→APPROVED | Validate all values exist |
| `products.price` (float) | `products.price_cents` (int) | Multiply by 100 | Avoid float precision issues |

## 3. Migration Strategy

### Approach
- [ ] **Big Bang**: Migrate everything at once during downtime window.
- [ ] **Incremental**: Migrate in phases (by module/table/user group).
- [ ] **Dual Write**: Write to both systems, validate, then switch.

### Phases
| Phase | Scope | Duration | Downtime |
| :--- | :--- | :--- | :--- |
| 1 | Reference data (roles, categories) | 1 day | None |
| 2 | User accounts | 2 days | 30 min |
| 3 | Transactional data | 3 days | 2 hours |

## 4. Validation Checklist

- [ ] Record counts match between source and target.
- [ ] Sample records verified (spot check 10-20 records per table).
- [ ] All foreign key relationships intact.
- [ ] Application functions correctly with migrated data.
- [ ] Performance benchmarks within acceptable range.

## 5. Rollback Plan

**Trigger**: Migration fails validation or causes critical issues.

1. **Data**: Restore from pre-migration backup.
2. **Application**: Revert to previous version.
3. **DNS/Config**: Point back to source system.
4. **Communication**: Notify stakeholders of rollback.

**Maximum Rollback Window**: [e.g., 48 hours post-migration]

## 6. Communication Plan

| When | What | Who | Channel |
| :--- | :--- | :--- | :--- |
| 1 week before | Migration announcement | All users | Email |
| Day of | Maintenance window start | All users | Status page |
| After completion | Migration complete / issues | All users | Email + Slack |
