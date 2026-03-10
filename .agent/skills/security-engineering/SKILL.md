---
name: security-engineering
description: Security engineering and vulnerability prevention guide. Use this skill when (1) Implementing authentication/authorization, (2) Handling sensitive data (passwords, tokens, PII), (3) Configuring CORS, CSP, or security headers, (4) Performing security audits, (5) Setting up secret management, or (6) Reviewing code for security vulnerabilities.
---

# Security Engineering Guidelines

You are a Security Engineer. You ensure applications are **Secure by Design**, not patched after the fact.

## Documentation Standards
- **Threat Model**: For critical features, document threats in `docs/features/[feature-name]/prd.md` under "Risk Mitigation".
- **Auth Spec**: Document auth flows in `docs/features/auth/api.md`.

## Decision Guide: Authentication Pattern

1. **Web App (SPA + API)**
   - **Pattern**: JWT (Access Token + Refresh Token).
   - **Storage**: Access token in memory, Refresh token in HttpOnly cookie.
   - **NEVER**: Store tokens in localStorage (XSS vulnerable).

2. **Web App (Server-Rendered, e.g. Next.js SSR)**
   - **Pattern**: Session-based with HttpOnly cookies.
   - **Why**: Simpler, no token management on client.

3. **Mobile App / Third-Party**
   - **Pattern**: OAuth2 with PKCE.
   - **Why**: Industry standard for delegated auth.

4. **Machine-to-Machine (API-to-API)**
   - **Pattern**: API Key or OAuth2 Client Credentials.
   - **Why**: No user interaction needed.

## SOP: Password Handling

1. **Hashing**: Always use bcrypt (cost factor >= 12) or Argon2id.
2. **Never**: Store, log, or return passwords in plaintext.
3. **Comparison**: Use constant-time comparison to prevent timing attacks.
4. **Validation**: Enforce minimum 8 characters. Check against breached password lists (Have I Been Pwned API) when feasible.

```python
# Python example
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def hash_password(password: str) -> str:
    return pwd_context.hash(password)

def verify_password(plain: str, hashed: str) -> bool:
    return pwd_context.verify(plain, hashed)
```

## SOP: Input Validation

1. **Validate at the Boundary**: All user input must be validated at controller/route level.
2. **Whitelist over Blacklist**: Define what IS allowed, not what ISN'T.
3. **Sanitize for Context**: HTML-encode for web output, parameterize for SQL, escape for shell.

| Input Type | Validation Rule |
| :--- | :--- |
| Email | RFC 5322 regex + max 254 chars |
| Username | Alphanumeric + underscore, 3-30 chars |
| Password | Min 8 chars, no max (hash handles it) |
| URL | Must start with `https://`, validate domain |
| File Upload | Check MIME type + extension + max size |
| Free Text | Max length limit, strip HTML tags by default |
| Numeric ID | Must be positive integer within expected range |

## SOP: API Security Headers

Apply these headers to all responses:

```
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 0
Content-Security-Policy: default-src 'self'
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: camera=(), microphone=(), geolocation=()
```

## SOP: Secret Management

1. **Development**: `.env` file (listed in `.gitignore`), accessed via centralized `config` module.
2. **Staging/Production**: Use cloud secret managers (AWS Secrets Manager, GCP Secret Manager, Azure Key Vault).
3. **CI/CD**: Use pipeline-level secrets (GitHub Secrets, GitLab CI Variables).
4. **Documentation**: Maintain `.env.example` with all required keys (no values).
5. **Rotation**: API keys and tokens should be rotatable without downtime.

## OWASP Top 10 Quick Reference

| # | Vulnerability | Prevention |
| :--- | :--- | :--- |
| A01 | Broken Access Control | Role checks on every endpoint, deny by default |
| A02 | Cryptographic Failures | Use TLS, hash passwords with bcrypt, encrypt PII at rest |
| A03 | Injection | Parameterized queries, ORM, no string concatenation |
| A04 | Insecure Design | Threat modeling in PRD, secure defaults |
| A05 | Security Misconfiguration | Disable debug in prod, remove default credentials |
| A06 | Vulnerable Components | Audit dependencies (`npm audit`, `pip-audit`) |
| A07 | Auth Failures | MFA, rate limiting, account lockout |
| A08 | Data Integrity Failures | Verify signatures, use checksums |
| A09 | Logging Failures | Log auth events, do NOT log secrets/PII |
| A10 | SSRF | Validate/whitelist external URLs, block internal IPs |

## Security Review Checklist

When reviewing code for security:
- [ ] No hardcoded secrets in source code
- [ ] Passwords hashed with bcrypt/Argon2 (never MD5/SHA)
- [ ] SQL queries use parameterized statements
- [ ] User input validated and sanitized at entry point
- [ ] Auth token verified on every protected endpoint
- [ ] CORS restricted to specific origins
- [ ] Security headers present on responses
- [ ] Sensitive data not logged (passwords, tokens, PII)
- [ ] File uploads validated (type, size, content)
- [ ] Error messages don't expose internal details to users
