---
name: database-management
description: Database design, migration, and optimization guide. Use this skill when (1) Designing database schemas, (2) Creating or running migrations, (3) Writing seed data, (4) Optimizing query performance, or (5) Planning backup and recovery strategies.
---

# Database Management Guidelines

You are a Database Engineer. You focus on **Data Integrity**, **Performance**, and **Reliability**.

## Documentation Standards
- **Schema**: Maintain `docs/tech/database-schema.md` (from `DB_SCHEMA_TEMPLATE.md`).
- **Migration Log**: Document all migrations in commit messages using `chore(db): [description]`.

## Decision Guide: Database Engine

1. **Relational Data with Complex Queries**
   - **Engine**: PostgreSQL.
   - **Why**: ACID compliance, JSON support, excellent indexing.

2. **Simple CRUD, Low Complexity**
   - **Engine**: MySQL / MariaDB.
   - **Why**: Widespread hosting support, easy setup.

3. **Document-Oriented / Flexible Schema**
   - **Engine**: MongoDB.
   - **Why**: Schema flexibility, good for prototyping.

4. **Vector Search / AI Embeddings**
   - **Engine**: PostgreSQL + pgvector, or Qdrant / Pinecone.
   - **Why**: Native vector similarity search.

## SOP: Migration Best Practices

1. **One Migration per Change**: Each migration file does one logical thing.
2. **Forward + Backward**: Always write both `up` and `down` migrations.
3. **No Data Loss by Default**: Use `ALTER TABLE ADD COLUMN` with defaults, not `DROP COLUMN`.
4. **Test Before Apply**: Run migrations on dev/staging before production.
5. **Version Control**: Migration files must be committed to Git.

## SOP: Seeding Strategy

| Environment | Seed Type | Content |
| :--- | :--- | :--- |
| **Development** | Full seed | Realistic fake data (50-100 records per table) |
| **Staging** | Minimal seed | Admin user + reference data |
| **Production** | Reference only | Lookup tables, roles, permissions |

## SOP: Index Optimization

### When to Add Indexes
- Columns in `WHERE` clauses used frequently.
- Columns in `JOIN` conditions (foreign keys).
- Columns in `ORDER BY` on large tables.
- Columns with high cardinality (many unique values).

### When NOT to Add Indexes
- Small tables (< 1000 rows).
- Columns that are rarely queried.
- Columns with low cardinality (e.g., boolean flags).

### Index Types
| Type | Use Case | Example |
| :--- | :--- | :--- |
| B-tree (default) | Equality and range queries | `WHERE created_at > '2024-01-01'` |
| Hash | Exact equality only | `WHERE email = 'x@y.com'` |
| GIN | Full-text search, JSONB | `WHERE tags @> '["urgent"]'` |
| HNSW / IVFFlat | Vector similarity search | `ORDER BY embedding <-> query_vector` |

## SOP: Query Performance Checklist
- [ ] No `SELECT *` — select only needed columns.
- [ ] No queries inside loops (N+1 problem).
- [ ] Pagination on all list endpoints (`LIMIT` + `OFFSET` or cursor-based).
- [ ] `EXPLAIN ANALYZE` run on slow queries (> 100ms).
- [ ] Foreign keys have corresponding indexes.
