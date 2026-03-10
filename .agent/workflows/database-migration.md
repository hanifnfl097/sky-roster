---
description: Workflow for database schema changes and migrations.
---

# Database Migration Workflow

## 1. Schema Design
**Trigger**: Feature requires new tables, columns, or relationship changes.
- **Action**: `@backend-dev` updates `docs/tech/database-schema.md` FIRST.
- **Validation**: Review ERD and table definitions with team.

## 2. Migration File
- **Action**: Generate migration file using the project's migration tool.
  - **SQL-based**: Flyway (`V[version]__[description].sql`)
  - **ORM-based**: Prisma (`npx prisma migrate dev`), Alembic (`alembic revision`), TypeORM
- **Rule**: Migration files must be idempotent and reversible when possible.

## 3. Rollback Plan
- **Action**: Create a corresponding rollback/down migration.
- **Document**: Add rollback steps in `docs/tech/database-schema.md`.

## 4. Test on Development
// turbo
- **Action**: Run migration on local/dev database.
- **Verify**: Data integrity checks, FK constraints, index creation.

## 5. Apply to Staging
- **Action**: Run migration on staging database.
- **Verify**: Application functions correctly with new schema.

## 6. Apply to Production
- **Action**: Run migration during maintenance window (if breaking change).
- **Post-Deploy**: Monitor query performance for 30 minutes.
