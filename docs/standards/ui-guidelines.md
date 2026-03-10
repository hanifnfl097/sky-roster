# UI Guidelines & Visual Standards — SkyRoster

**Last Updated:** 2026-03-09

---

## 1. Design Philosophy

> **Precision. Clarity. Trust.**

SkyRoster is a mission-critical aviation operations tool. The design must prioritize:
- **Scannable layouts**: Crew and roster officers make time-sensitive decisions — data must be instantly readable.
- **Status-driven UI**: Color-coded badges and indicators convey compliance state at a glance.
- **Aviation-grade professionalism**: Dark, sleek aesthetics inspired by cockpit instrument panels and airline ops software.
- **Zero ambiguity**: Labels, tooltips, and confirmations prevent costly operational mistakes.

---

## 2. Color Palette (Tailwind)

### Primary Colors
| Name | Class | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Primary | `bg-sky-600` | `#0284C7` | Main buttons, active states, primary CTA |
| Primary Dark | `bg-sky-700` | `#0369A1` | Hover states, focus rings |
| Surface Dark | `bg-slate-900` | `#0F172A` | Sidebar, header backgrounds |
| Surface | `bg-slate-50` | `#F8FAFC` | Main content background |
| Card | `bg-white` | `#FFFFFF` | Card backgrounds |

### Functional / Status Colors
| Name | Class | Hex | Usage |
| :--- | :--- | :--- | :--- |
| Active / Fit to Fly | `text-emerald-600` / `bg-emerald-100` | `#059669` | Active crew, valid docs, sign-on confirmed |
| Grounded / Expired | `text-rose-600` / `bg-rose-100` | `#E11D48` | Grounded crew, expired docs, FTL violation, no-show |
| Warning / Expiring | `text-amber-600` / `bg-amber-100` | `#D97706` | 14-day expiry warning, approaching FTL limit |
| Caution | `text-orange-500` / `bg-orange-100` | `#F97316` | 7-day expiry critical warning |
| Info / Pending | `text-blue-600` / `bg-blue-100` | `#2563EB` | Pending sign-on, draft roster |
| Standby | `text-violet-600` / `bg-violet-100` | `#7C3AED` | Standby crew assignments |

### Roster Board Colors (Gantt Blocks)
| Name | Hex | Usage |
| :--- | :--- | :--- |
| Flight Duty | `#0284C7` (sky-600) | Assigned flight pairing |
| Standby | `#7C3AED` (violet-600) | Standby duty |
| Rest / Off | `#94A3B8` (slate-400) | Day off / rest period |
| Leave | `#10B981` (emerald-500) | Annual leave / personal leave |
| Training | `#F59E0B` (amber-500) | Simulator check / training |

---

## 3. Typography

**Font Family**: `Inter` (Sans-serif) — loaded via Google Fonts

| Style | Tailwind Classes | Usage |
| :--- | :--- | :--- |
| **H1** | `text-3xl font-bold tracking-tight text-slate-900` | Page titles (Dashboard, Crew List) |
| **H2** | `text-xl font-semibold text-slate-800` | Section headers, card titles |
| **H3** | `text-lg font-medium text-slate-700` | Subsection headers |
| **Body** | `text-sm text-slate-600 leading-relaxed` | Standard text, descriptions |
| **Table Text** | `text-sm text-slate-700` | Table cell content |
| **Badge** | `text-xs font-medium px-2.5 py-0.5 rounded-full` | Status badges, role tags |
| **Small / Caption** | `text-xs text-slate-500` | Metadata, timestamps, hints |
| **Mono** | `font-mono text-sm` | Flight numbers, staff IDs, times |

---

## 4. Components

### Buttons
- **Primary**: `rounded-lg bg-sky-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-sky-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-sky-600 transition-colors`
- **Secondary**: `rounded-lg bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 transition-colors`
- **Danger**: `rounded-lg bg-rose-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-rose-700 transition-colors`
- **Ghost**: `rounded-lg px-4 py-2.5 text-sm font-medium text-slate-600 hover:bg-slate-100 transition-colors`

### Inputs
- **Default**: `block w-full rounded-lg border-0 py-2 px-3 text-sm text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-sky-600`

### Badge / Status Chip
- **Active**: `inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800`
- **Grounded**: `inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-rose-100 text-rose-800`
- **Warning**: `inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-100 text-amber-800`

### Cards
- **Default**: `bg-white rounded-xl shadow-sm ring-1 ring-slate-200 p-6`
- **Stat Card**: `bg-white rounded-xl shadow-sm ring-1 ring-slate-200 p-5 flex items-center gap-4`
- **Alert Card**: `bg-rose-50 border-l-4 border-rose-500 rounded-r-lg p-4`

---

## 5. Status Badge System (Domain-Specific)

### Crew Status
| Status | Badge Style | Icon |
| :--- | :--- | :--- |
| Active | `bg-emerald-100 text-emerald-800` | ✅ |
| Grounded | `bg-rose-100 text-rose-800` | 🔴 |
| On Leave | `bg-slate-100 text-slate-600` | 🏖️ |

### Document Status
| Status | Badge Style | Icon |
| :--- | :--- | :--- |
| Valid (> 30 days) | `bg-emerald-100 text-emerald-800` | ✅ |
| Expiring (14-30 days) | `bg-amber-100 text-amber-800` | ⚠️ |
| Critical (< 14 days) | `bg-orange-100 text-orange-800` | 🔶 |
| Expired | `bg-rose-100 text-rose-800` | ❌ |

### Sign-On Status
| Status | Badge Style | Icon |
| :--- | :--- | :--- |
| Signed On | `bg-emerald-100 text-emerald-800` | ✅ |
| Pending | `bg-blue-100 text-blue-800` | ⏳ |
| No Show | `bg-rose-100 text-rose-800` | ❌ |
| Sick | `bg-amber-100 text-amber-800` | 🤒 |

---

## 6. Iconography

Use **Heroicons** (outline style) for web admin, **Material Icons** for Flutter mobile app.

| Context | Icon Suggestion |
| :--- | :--- |
| Flight / Aviation | `PaperAirplaneIcon` |
| Crew | `UserGroupIcon` |
| Document | `DocumentTextIcon` |
| Clock / FTL | `ClockIcon` |
| Alert / Warning | `ExclamationTriangleIcon` |
| Settings | `Cog6ToothIcon` |
| Calendar / Roster | `CalendarDaysIcon` |
| Location / Geofence | `MapPinIcon` |

---

## 7. Responsive Breakpoints

| Breakpoint | Min Width | Usage |
| :--- | :--- | :--- |
| `sm` | 640px | Single-column forms |
| `md` | 768px | Sidebar appears |
| `lg` | 1024px | Full dashboard grid |
| `xl` | 1280px | Roster board timeline |
| `2xl` | 1536px | Maximum content width |
