---
name: project-scaffolding
description: Project initialization and scaffolding guide. Use this skill when (1) Starting a new project from scratch, (2) Setting up linting, formatting, and Git hooks, (3) Configuring project structure and folder conventions, or (4) Creating README and contributing guidelines.
---

# Project Scaffolding Guidelines

You are a Lead Developer setting up a new project. You ensure the codebase starts with **Clean Architecture**, **Consistent Standards**, and **Developer Experience**.

## Decision Guide: Tech Stack

1. **Full-Stack Web App (SSR + API)**
   - **Framework**: Next.js (React) or Nuxt.js (Vue).
   - **Why**: Server-side rendering, API routes, file-based routing.

2. **SPA + Separate Backend**
   - **Frontend**: Vite + React/Vue.
   - **Backend**: Express/Fastify (Node), FastAPI (Python), Spring Boot (Java).
   - **Why**: Independent deployment, clear separation.

3. **API-Only Service**
   - **Framework**: FastAPI (Python), Express (Node), or Spring Boot (Java).
   - **Why**: No UI needed, focus on logic.

## SOP: Project Structure (Backend)

```
project-root/
├── src/
│   ├── config/           # Environment, database, app config
│   ├── controllers/      # Route handlers (thin layer)
│   ├── services/         # Business logic
│   ├── models/           # Database models / entities
│   ├── repositories/     # Data access layer
│   ├── middlewares/       # Auth, logging, error handling
│   ├── utils/            # Shared helpers
│   └── types/            # TypeScript types / interfaces
├── tests/
│   ├── unit/
│   ├── integration/
│   └── e2e/
├── docs/                 # Project documentation
├── .env.example          # Required environment variables
├── .gitignore
├── .eslintrc.json        # Linting configuration
├── .prettierrc           # Formatting configuration
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## SOP: Project Structure (Frontend)

```
project-root/
├── src/
│   ├── components/
│   │   ├── ui/           # Base components (Button, Input, Card)
│   │   └── features/     # Domain components (ChatWindow, UserProfile)
│   ├── layouts/          # Page wrappers (SidebarLayout, AuthLayout)
│   ├── pages/            # Route pages
│   ├── hooks/            # Custom React hooks
│   ├── services/         # API client functions
│   ├── stores/           # State management
│   ├── styles/           # Global CSS / Tailwind config
│   ├── types/            # TypeScript types
│   └── utils/            # Shared helpers
├── public/               # Static assets
├── tests/
└── README.md
```

## SOP: Essential Config Files

### `.gitignore` (Required)
Must exclude: `node_modules/`, `.env`, `dist/`, `build/`, `.DS_Store`, `*.log`, `coverage/`, `.idea/`, `.vscode/`.

### `.env.example` (Required)
List all required environment variables with descriptions, no actual values.

### Linting (Recommended)
- **JavaScript/TypeScript**: ESLint + Prettier.
- **Python**: Ruff or Flake8 + Black.
- **Java**: Checkstyle or SpotBugs.

### Git Hooks (Recommended)
- **Pre-commit**: Run linter + formatter.
- **Pre-push**: Run test suite.
- **Tool**: Husky (Node), pre-commit (Python).

## SOP: README Template

Every project README should include:
1. **Project Name & Description** — What does it do?
2. **Tech Stack** — Languages, frameworks, databases.
3. **Prerequisites** — Required software versions.
4. **Getting Started** — Clone, install, configure, run.
5. **Environment Variables** — Reference to `.env.example`.
6. **Available Scripts** — `npm run dev`, `npm test`, etc.
7. **Project Structure** — Brief folder overview.
8. **Contributing** — How to contribute (branch naming, commit format).
