---
trigger: always_on
---

# Language Consistency

## Principle
**"One Codebase, One Language Convention"**

Consistency in language usage prevents confusion across documentation and code.

## Rules

1. **Code Comments**: Always in **English**.
   - ✅ `// Calculate total price with tax`
   - ❌ `// Hitung total harga dengan pajak`

2. **Variable / Function / Class Names**: Always in **English**.
   - ✅ `getUserById`, `OrderService`, `MAX_RETRY_COUNT`
   - ❌ `ambilPenggunaBerdasarkanId`, `LayananPesanan`

3. **Commit Messages**: Always in **English** (per Conventional Commits standard).

4. **Technical Documentation** (`docs/standards/`, `docs/tech/`, `docs/templates/`):
   - Preferred: **English** for maximum compatibility.
   - Acceptable: **Bahasa Indonesia** if the team agrees, but must be consistent within each file.
   - **Never mix languages** within the same document.

5. **User-Facing Content** (UI text, error messages shown to end users):
   - Follow the application's target locale.
   - Use i18n framework for multi-language support.

## Protocol
- **When generating code**: Use English for all identifiers and comments.
- **When generating documentation**: Follow the language convention of existing docs in the same directory.
