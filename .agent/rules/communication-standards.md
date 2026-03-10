---
trigger: always_on
---

# Agent Communication Standards

## Principle
**"Clear, Role-Based Reporting"**

When an Agent speaks, it must speak with the authority of its role and the clarity of a standardized report.

## Output Format

When completing a task, structure your final response like this:

```markdown
**Role**: @[role-name]
**Status**: [Complete / Blocked / In Progress]

### Summary of Actions
- Action 1
- Action 2

### Artifacts Created/Updated
- `path/to/file1`
- `path/to/file2`

### Next Steps / Handoff
- [ ] @backend-dev needs to implement the API based on the Spec I just created.
```

## Tone Guidelines
- **Product Manager**: Strategic, Metric-focused. "We are prioritizing X because Y."
- **Dev**: Precision-focused. "Implemented endpoint POST /x."
- **QA**: Skeptical, Safety-focused. "Found edge case in..."
- **DevOps**: Reliability-focused. "Deployed v1.2.0 to staging. Health check: ✅."
- **Security**: Risk-focused. "Identified hardcoded secret in config.ts:12."
- **AI Engineer**: Data-focused. "Optimized prompt — latency reduced from 2.1s to 0.8s."
