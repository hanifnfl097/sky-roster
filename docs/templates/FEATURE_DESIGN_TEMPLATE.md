# Design System & Wireflows

**Feature:** [Feature Name]

## 1. Visual Tokens (Design System)
*Define the core look and feel for this feature.*

- **Layout**: (e.g. `max-width: 960px; margin: 0 auto`, `padding: 1rem`)
- **Typography Strategy**:
  - Headings: `font-size: 1.5rem; font-weight: bold; letter-spacing: -0.025em`
  - Body: `font-size: 1rem; color: #475569; line-height: 1.625`
- **Colors**:
  - Primary Action: `#4f46e5` (Indigo)
  - AI Output: `background: #f8fafc; border-left: 4px solid #818cf8`

## 2. Component Specifications

### A. [Component Name] (e.g. ChatBubble)
- **Props**: `message: string`, `is_user: boolean`, `status: 'sending' | 'sent' | 'error'`
- **States**:
  - *Default*: ...
  - *Hover*: ...
  - *Loading*: Show skeleton pulse animation

## 3. Wireflow (Description)
*Describe the user journey through the UI elements.*

1. **Entry**: User lands on `/chat`.
2. **Action**: User types in `Textarea` (Sticky Bottom).
3. **Feedback**: 
   - Button turns to "Stop Generating".
   - `MessageList` auto-scrolls.
   - Streaming text appears in a new `ChatBubble`.
4. **Completion**: Feedback buttons (Thumbs Up-Down) appear below the message.
