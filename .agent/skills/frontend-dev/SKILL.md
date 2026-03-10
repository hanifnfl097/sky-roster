---
name: frontend-dev
description: Frontend development and UI implementation standards. Use this skill when: (1) Building UI components, (2) Managing client-side state, (3) Integrating APIs (especially streaming), or (4) Handling routing and navigation.
---

# Frontend Development Guidelines

You are a Senior Frontend Developer. You focus on **User Experience (UX)**, **Performance**, and **Component Reusability**.

## Documentation Standards
- **Source of Truth**: Always check `docs/product/app-structure.md` for Layout/Navigation rules.
- **Feature Design**: For complex features, collaborate with UX to fill `docs/features/[feature-name]/design.md` (using `docs/templates/FEATURE_DESIGN_TEMPLATE.md`).

## Decision Guide: State Management


1. **Server State** (User profile, List of items)
   - **Tool**: `TanStack Query` (React) or `swrv` (Vue).
   - **Why**: Handles caching, loading, and error states automatically.

2. **Local UI State** (Modal open/close, Form inputs)
   - **Tool**: `useState` (React) or `ref` (Vue).
   - **Why**: Keep it simple and close to the component.

3. **Global App State** (Theme, Auth Token)
   - **Tool**: Context API or Pinia/Zustand.
   - **Why**: Avoid prop drilling.

## AI-Specific Patterns

### 1. Streaming Text
When displaying AI responses, NEVER wait for the full response.
- **Pattern**: Use a stream reader loop.
- **UI**: Append text to the state variable as chunks arrive.
- **Auto-scroll**: Ensure the view scrolls to the bottom as new text appears.

### 2. Optimistic UI
Make the app feel fast.
- **Action**: When user sends a message.
- **Immediate UI**: Add the user's message to the chat list *immediately* with a "sending" status.
- **Async**: Then send the network request.
- **Error**: If request fails, mark the message as "Failed" and allow retry.

### 3. Component Structure
Follow Atomic Design principles loosely:
- `components/ui/`: Base elements (Buttons, Inputs, Cards).
- `components/features/`: Complex widgets (ChatWindow, UserProfile).
- `layouts/`: Page wrappers (SidebarLayout, AuthLayout).

### 4. Error Handling (Frontend)
Display errors meaningfully to users:

| Error Source | Display Pattern | Example |
| :--- | :--- | :--- |
| Form validation | Inline error below field | "Email is required" |
| API 4xx error | Toast notification or inline | "Invalid credentials" |
| API 5xx error | Generic error message | "Something went wrong. Please try again." |
| Network failure | Banner at top of page | "You are offline. Changes will sync when connected." |

**Rules**:
- Never show raw error messages from API (stack traces, SQL errors).
- Always provide a user action (retry button, back to home, contact support).
- Log errors to console/monitoring in development, suppress in production.

### 5. Quality Control
- **Component Tests**: Write tests for "dumb" UI components (ensure props render correctly).
- **Lighthouse**: Run a quick audit; ensure no accessibility (A11y) violations (e.g., missing labels).
