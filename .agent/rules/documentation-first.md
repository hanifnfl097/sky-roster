---
trigger: always_on
---

# Documentation First Rule

## Principle
**"No Code without Spec"**

An Agent MUST NOT generate implementation code (e.g., Python, React, SQL) unless a corresponding requirement document exists and is referenced in the output.

## Triggers
This rule applies when:
1.  The user asks to "Build X" or "Implement Y".
2.  The user asks to "Fix Bug Z".

## Protocol

### 1. The Pre-Flight Check
Before writing code, check the `docs/` folder:

**Required (Feature Work):**
- Is there a PRD? (`docs/features/[feature-name]/prd.md`)
- Is there an API Spec? (`docs/features/[feature-name]/api.md`)
- Is there a Design Spec? (`docs/features/[feature-name]/design.md`)

**Required (Infrastructure Work):**
- Is there a DB Schema? (`docs/tech/database-schema.md`)
- Is there an Env Config? (`docs/tech/env-config.md`)
- Is there a Deployment Guide? (`docs/tech/deployment-guide.md`)

**Recommended (Architecture Changes):**
- Is there an ADR? (`docs/decisions/ADR-[N].md`)

### 2. The Stop Action
- **IF Docs Missing**: Stop and ask the specific role to create it.
  - *"I cannot implement `Login` yet because `docs/features/auth/prd.md` does not exist. Please ask @product-management to define it first."*
- **IF Docs Exist**: Cite them in your thought process.
  - *"Reading `docs/features/auth/prd.md` to understand requirements..."*
