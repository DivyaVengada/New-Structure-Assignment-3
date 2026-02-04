
# Architecture

## API Endpoints
- `/api/payment/purchase` (POST): Purchase (auth + capture)
- `/api/payment/authorize` (POST): Authorize only
- `/api/payment/capture` (POST): Capture funds
- `/api/payment/cancel` (POST): Cancel transaction
- `/api/payment/refund` (POST): Refund (full/partial)
- `/api/subscription/create` (POST): Create subscription
- `/api/webhook/event` (POST): Handle webhook event

## Flows Implemented
- Core payment flows (purchase, authorize/capture, cancel, refund)
- Advanced flows (recurring billing, idempotency, async webhooks)

## DB Schema & Entity Relationships
- **Order**: orderId, amount, status, createdAt
- **Subscription**: subscriptionId, monthlyAmount, status, createdAt
- **WebhookEvent**: eventId, eventType, payload, receivedAt

## Design Trade-offs
- **Sync vs Async**: Webhook events handled async in service layer; can be extended to message queues.
- **Retry Strategies**: Idempotency via eventId storage in DB.
- **Queueing**: In-memory for demo; RabbitMQ/Kafka for scale.

## Compliance Considerations
- PCI DSS: No card data stored; secrets via env vars.
- Secrets Management: Use environment variables.
- Rate Limits: Can be added via Spring Security.
- Audit Logs: All actions logged with trace IDs.
