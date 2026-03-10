---
trigger: always_on
---

# Security Guardrails

## Principle
**"Secure by Default"**

An Agent MUST NOT generate code that introduces known security vulnerabilities. These rules apply to ALL generated code regardless of language or framework.

## Hard Stops (NEVER Do)

1. **No Hardcoded Secrets**: Never hardcode API keys, passwords, tokens, or connection strings.
   - ❌ `const API_KEY = "sk-abc123..."`
   - ✅ `const API_KEY = process.env.API_KEY`

2. **No Plaintext Passwords**: Never store or compare passwords in plaintext.
   - ❌ `if (password === user.password)`
   - ✅ `if (await bcrypt.compare(password, user.hashedPassword))`

3. **No `eval()` / `exec()`**: Never use dynamic code execution with user input.
   - ❌ `eval(userInput)` / `exec(userQuery)`
   - ✅ Use parameterized queries, AST parsers, or sandboxed environments.

4. **No Wildcard CORS in Production**: Never set `Access-Control-Allow-Origin: *` for authenticated endpoints.
   - ✅ Whitelist specific origins: `cors({ origin: ['https://app.example.com'] })`

5. **No SQL Injection Vectors**: Never concatenate user input into SQL queries.
   - ❌ `f"SELECT * FROM users WHERE id = {user_id}"`
   - ✅ `cursor.execute("SELECT * FROM users WHERE id = %s", (user_id,))`

6. **No Disabled Security Headers**: Never disable CSRF protection, HTTPS redirect, or security headers without explicit user approval.

## Mandatory Practices

1. **Environment Variables**: All secrets via `.env` files, accessed through a centralized config module.
2. **Input Validation**: Validate and sanitize ALL user inputs at the boundary (controller/route level).
3. **Authentication Checks**: Every protected endpoint must verify auth token before processing.
4. **Dependency Awareness**: Flag known vulnerable package versions when encountered.
5. **HTTPS**: All external API calls must use HTTPS in production configurations.

## Protocol
- **IF violation detected in existing code**: Flag it immediately with severity and fix suggestion.
- **IF generating new code**: Apply these rules automatically without prompting.
