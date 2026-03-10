---
description: Workflow for initializing a new project from scratch (greenfield setup).
---

# Project Setup Workflow

## 1. Product Definition (PM)
**Trigger**: User says "I want to start a new project" or "Build a new app".
- **Action**: `@product-management` creates:
  - `docs/product/user-stories.md` (from `USER_STORIES_TEMPLATE.md`)
  - `docs/product/app-structure.md` (from `APP_STRUCTURE_TEMPLATE.md`)
- **Validation**: User confirms product scope.

## 2. Technical Architecture
- **Action**: `@backend-dev` creates:
  - `docs/tech/database-schema.md` (from `DB_SCHEMA_TEMPLATE.md`)
  - `docs/tech/api-inventory.md` (from `API_INVENTORY_TEMPLATE.md`)
  - `docs/tech/env-config.md` (from `ENV_CONFIG_TEMPLATE.md`)
- **Action**: `@ux-design` creates:
  - `docs/standards/ui-guidelines.md` (from `UI_GUIDELINES_TEMPLATE.md`)

## 3. Project Scaffolding
// turbo
- **Action**: Initialize project structure:
  - Backend framework (e.g., `npx -y create-next-app@latest ./`)
  - Database setup (schema + migration)
  - Linting & formatting config (`.eslintrc`, `.prettierrc`)
  - `.env.example` with all required variables
  - `.gitignore` with standard exclusions
  - `README.md` with setup instructions

## 4. Git Initialization
// turbo
- **Action**: Initialize repository:
  - `git init`
  - Create initial commit: `chore: initialize project scaffold`
  - Set up branch protection rules (if applicable)

## 5. Local Development Setup
// turbo
- **Action**: Verify local development environment works:
  - Install dependencies
  - Start dev server
  - Verify database connection
  - Run initial test suite (smoke test)

## 6. First Feature
- **Action**: Proceed to `/feature-development` workflow for the first feature.
