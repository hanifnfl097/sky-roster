# Runbook / Troubleshooting Guide

**Service Name:** [Service Name]
**Last Updated:** YYYY-MM-DD

## 1. Service Overview
- **Purpose**: Brief description of what this service does.
- **Tech Stack**: Language, framework, database, external dependencies.
- **Health Check Endpoint**: `GET /health`
- **Logs Location**: `[path or service, e.g., CloudWatch, Datadog, stdout]`

## 2. Common Issues & Resolutions

### Issue: [Title, e.g., "Service returns 503"]
**Symptoms**:
- What the user/monitoring sees.

**Diagnosis**:
1. Check service health: `curl https://[host]/health`
2. Check logs: `[command to view logs]`
3. Check resource usage: `[command to check CPU/memory]`

**Resolution**:
1. Step-by-step fix instructions.
2. Restart procedure if applicable.

**Prevention**:
- What to do to prevent this from happening again.

---

### Issue: [Title, e.g., "Database connection refused"]
**Symptoms**:
- API returns 500 errors with "Connection refused" in logs.

**Diagnosis**:
1. Check DB status: `[command]`
2. Check connection pool: `[command or dashboard]`

**Resolution**:
1. Verify DB credentials in environment variables.
2. Check if max connections limit is reached.
3. Restart connection pool / service.

**Prevention**:
- Implement connection pooling with health checks.

## 3. Escalation Matrix

| Severity | Response Time | Who to Contact | Channel |
| :--- | :--- | :--- | :--- |
| **Critical** (service down) | 15 min | On-call engineer | Phone/Slack alert |
| **High** (degraded performance) | 1 hour | Backend team lead | Slack #incidents |
| **Medium** (non-blocking issue) | 4 hours | Assigned developer | Jira ticket |

## 4. Rollback Procedure
1. Identify the last known good deployment/commit.
2. Revert to previous version: `[deployment command]`.
3. Verify service health after rollback.
4. Notify team of rollback with reason.

## 5. Useful Commands
```bash
# Check service status
[command]

# View recent logs
[command]

# Restart service
[command]

# Check database connectivity
[command]
```
