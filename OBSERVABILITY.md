# Observability Guide

This document outlines the observability strategy for the payment backend system, covering metrics, tracing, logging, and alerting.

---

## 📊 Metrics

### Application Metrics
- **Request throughput**
    - Number of API calls per endpoint (`/payment/purchase`, `/payment/authorize`, etc.)
- **Latency**
    - Average response time per endpoint
    - 95th/99th percentile latency
- **Error rates**
    - HTTP 4xx/5xx counts per endpoint
    - Gateway errors (Authorize.Net failures, OTS validation errors)
- **Business metrics**
    - Orders created per minute/hour
    - Successful vs failed transactions
    - Refunds issued
    - Subscription activations/cancellations

### JVM / Spring Boot Metrics
- Heap usage (used vs max)
- GC activity (pause times, frequency)
- Thread pool utilization (Tomcat/Netty request threads)
- Database connection pool stats (HikariCP: active, idle, wait time)

### Database Metrics (Postgres)
- Query latency (avg execution time per query)
- Slow queries (> 500ms)
- Connection count
- Deadlocks / lock waits

### Infrastructure Metrics
- Container health (CPU, memory, restart count)
- Network I/O (traffic between simulator ↔ backend ↔ gateway)
- Disk usage (Postgres volume size)

---

## 🔍 Tracing Strategy

- **Distributed tracing** with OpenTelemetry (Spring Boot starter).
- **Trace context propagation** across:
    - Simulator → Backend → Gateway client calls
- **Span creation**:
    - API entry points (Controller methods)
    - Service layer (business logic)
    - External calls (Authorize.Net client, Postgres queries)
- **Trace IDs** logged with each request for correlation.
- **Exporters**:
    - Jaeger or Zipkin for local dev
    - OTLP → Prometheus/Grafana in production

---

## 📝 Logging Strategy

- **Structured JSON logs** (via Logback/Logstash encoder).
- **Log levels**:
    - `INFO`: successful transactions, lifecycle events
    - `WARN`: recoverable issues (retry, timeout)
    - `ERROR`: failed transactions, gateway errors
    - `DEBUG`: detailed flow (enabled only in dev/test)
- **Correlation IDs**:
    - Include `orderId` and `traceId` in every log line.
    - Example:
      ```json
      {
        "timestamp": "2026-01-28T20:05:00Z",
        "level": "ERROR",
        "traceId": "abc123",
        "orderId": "f4671cde-fee9-4566-b040-ac4b9eae9bf1",
        "message": "User authentication failed due to invalid authentication values."
      }
      ```
- **Sensitive data**: Never log card numbers, CVV, or raw gateway credentials.
- **Log rotation**: Use Docker logging driver or ELK stack for central aggregation.

---

## 🚦 Alerting & Dashboards

### Alerts
- High error rate (>5% of requests in 5 min)
- Latency spikes (>2s p95)
- DB connection pool exhaustion
- Container restarts >3 in 10 min

### Dashboards
- API performance (throughput, latency, error rate)
- Business KPIs (orders, refunds, subscriptions)
- JVM health (heap, GC, threads)
- DB health (query times, locks)

---