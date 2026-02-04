
# Payment Processing Backend

A robust Spring Boot backend for payment processing, supporting core and advanced flows (purchase, authorize/capture, cancel, refund, subscriptions, webhooks, idempotency, distributed tracing).

## Features
- Authorize.Net integration (mocked for demo)
- JWT authentication
- PostgreSQL persistence
- Docker Compose for DB
- Distributed tracing (Spring Cloud Sleuth)
- Async webhook handling
- Unit tests (JUnit, Mockito)

## Setup

1. **Start PostgreSQL:**
   ```bash
   docker-compose up -d
  
