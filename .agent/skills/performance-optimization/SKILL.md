---
name: performance-optimization
description: Performance analysis and optimization guide. Use this skill when (1) Diagnosing slow page loads or API responses, (2) Implementing caching strategies, (3) Optimizing database queries, (4) Running Lighthouse or profiling tools, or (5) Reducing bundle size or improving Core Web Vitals.
---

# Performance Optimization Guidelines

You are a Performance Engineer. You ensure applications are **Fast**, **Efficient**, and **Scalable**.

## Decision Guide: Where Is the Bottleneck?

1. **Slow API Response (> 500ms)**
   - **Check**: Database queries (N+1, missing indexes, full table scans).
   - **Check**: External API calls without timeout/caching.
   - **Check**: Synchronous processing of heavy tasks.

2. **Slow Page Load (> 3s)**
   - **Check**: Large JavaScript bundle (> 500KB).
   - **Check**: Unoptimized images (no WebP, no lazy loading).
   - **Check**: No caching headers (CDN, browser cache).

3. **High Memory Usage**
   - **Check**: Memory leaks (unclosed connections, growing arrays).
   - **Check**: Loading entire datasets into memory.

## SOP: Backend Optimization

### Caching Strategy
| Data Type | Strategy | TTL | Tool |
| :--- | :--- | :--- | :--- |
| User session | In-memory / Redis | 15-60 min | Redis |
| API response (static) | HTTP cache headers | 1-24 hours | CDN / Nginx |
| Database query result | Application cache | 5-30 min | Redis |
| Static assets | Browser cache + CDN | 1 year | Cache-Control header |

### Database Query Optimization
1. **Use `EXPLAIN ANALYZE`** on queries taking > 100ms.
2. **Add indexes** on frequently filtered/sorted columns.
3. **Use pagination** — never return unbounded result sets.
4. **Avoid N+1** — use `JOIN` or batch queries instead of loops.
5. **Select specific columns** — never `SELECT *` in production.

### Async Processing
- **Heavy tasks** (email sending, PDF generation, AI calls): Use job queues (Bull, Celery).
- **Pattern**: Accept request → enqueue job → return 202 Accepted → process async.

## SOP: Frontend Optimization

### Bundle Size Reduction
1. **Code splitting**: Use dynamic `import()` for routes and heavy components.
2. **Tree shaking**: Import specific functions, not entire libraries.
   - ✅ `import { debounce } from 'lodash-es'`
   - ❌ `import _ from 'lodash'`
3. **Analyze**: Run `npx webpack-bundle-analyzer` or `npx vite-bundle-visualizer`.

### Image Optimization
1. **Format**: Use WebP/AVIF instead of PNG/JPEG.
2. **Lazy loading**: `<img loading="lazy">` for below-fold images.
3. **Responsive**: Use `srcset` for different screen sizes.
4. **CDN**: Serve images from CDN with resize transforms.

### Core Web Vitals Targets
| Metric | Target | Measurement |
| :--- | :--- | :--- |
| **LCP** (Largest Contentful Paint) | < 2.5s | Lighthouse |
| **INP** (Interaction to Next Paint) | < 200ms | Lighthouse |
| **CLS** (Cumulative Layout Shift) | < 0.1 | Lighthouse |

## Quick Wins Checklist
- [ ] Enable Gzip/Brotli compression on server.
- [ ] Set `Cache-Control` headers for static assets.
- [ ] Lazy load images and below-fold components.
- [ ] Add database indexes on foreign keys.
- [ ] Use connection pooling for database connections.
- [ ] Set timeouts on all external API calls.
