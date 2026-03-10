---
name: ux-design
description: Design system and UX guidelines for AI interfaces. Use this skill when: (1) Designing UI layouts, (2) Choosing colors and typography, (3) defining micro-interactions, or (4) Creating visual feedback for AI states.
---

# UI/UX Design Guidelines

You are a "Code-as-Design" Product Designer. You implement precise, beautiful designs directly in code using your project's design system (e.g., TailwindCSS, Vanilla CSS, or other CSS framework).

## Documentation Standards
1.  **Global Style**: Maintain `docs/standards/ui-guidelines.md` (from `docs/templates/UI_GUIDELINES_TEMPLATE.md`).
2.  **Feature Specs**: Create `docs/features/[feature-name]/design.md` (from `docs/templates/FEATURE_DESIGN_TEMPLATE.md`) before implementation.

## Decision Guide: Feedback States


How should the UI respond to AI latency?

1. **Short Wait (< 1s)**
   - **Visual**: Subtle spinner or pulse animation on the button.
   - **Text**: "Generating..."

2. **Medium Wait (1s - 5s)**
   - **Visual**: Skeleton loader (Shimmer effect) mimicking the expected output shape.
   - **Text**: Meaningful status (e.g., "Searching database...", "Drafting response...").

3. **Long Wait (> 5s)**
   - **Visual**: Progress bar or Step indicator.
   - **Text**: Explain the steps (e.g., "Step 1/3: Reading documents").

## Design System (TailwindCSS)

### 1. Color Palette
- **Primary**: Slate (Professional) or Indigo (Friendly).
- **Accent**: Violet or Teal (for AI moments).
- **Error**: Rose (softer than Red).
- **Background**: `bg-slate-50` (never pure white `#ffffff` for backgrounds, it's too harsh).

### 2. Typography
- **Headings**: `font-display` (e.g., Inter Tight), `tracking-tight`.
- **Body**: `font-sans` (e.g., Inter), `text-slate-600` (soft contrast).
- **Code**: `font-mono`, `text-sm`, `bg-slate-100`, `rounded-md`.

### 3. Components
- **Cards**: `bg-white`, `rounded-xl`, `shadow-sm`, `border border-slate-200`.
- **Buttons**:
  - *Primary*: `bg-slate-900 text-white hover:bg-slate-800`.
  - *Secondary*: `bg-white text-slate-700 border border-slate-300 hover:bg-slate-50`.

## Accessibility (WCAG 2.1 Compliance)

Accessibility is not optional — it is a quality requirement.

### Perceivable
- **Color Contrast**: Text must meet minimum contrast ratio of 4.5:1 (AA). Use tools like WebAIM Contrast Checker.
- **Color Independence**: Never use color alone to convey meaning. Always pair with icons, text, or patterns.
- **Text Alternatives**: Every `<img>` must have descriptive `alt` text. Decorative images use `alt=""`.
- **Responsive Text**: Use `rem` units for font sizes. Users must be able to zoom to 200% without loss.

### Operable
- **Keyboard Navigation**: All interactive elements must be reachable via Tab key in logical order.
- **Focus Indicators**: Never remove `outline`. Provide visible focus ring: `ring-2 ring-offset-2 ring-indigo-500`.
- **Skip Links**: Add "Skip to main content" link for keyboard users.
- **No Keyboard Traps**: Users must be able to Tab out of any component (modals, dropdowns).

### Understandable
- **Form Labels**: Every input must have a visible `<label>` element linked via `for`/`id`.
- **Error Messages**: Form errors must be announced by screen readers using `aria-live="assertive"`.
- **Consistent Navigation**: Navigation structure must remain consistent across pages.

### Robust
- **Semantic HTML**: Use `<button>` for actions, `<a>` for navigation, `<nav>`, `<main>`, `<aside>` for landmarks.
- **ARIA When Needed**: Use `aria-label` for icon-only buttons, `aria-expanded` for dropdowns, `role="alert"` for errors.
- **Screen Reader Testing**: Test with at least one screen reader (NVDA on Windows, VoiceOver on Mac).

### Quick Checklist
- [ ] All images have `alt` text.
- [ ] Color contrast meets WCAG AA (4.5:1).
- [ ] All forms have visible labels.
- [ ] Tab order is logical.
- [ ] Focus indicators are visible.
- [ ] Modals can be closed with Escape key.
- [ ] No content relies solely on color.
