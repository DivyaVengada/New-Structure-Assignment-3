
// src/main/java/com/company/payment/payment_backend/service/WebhookEventAsyncListener.java
package com.company.payment.payment_backend.service;

import com.company.payment.payment_backend.events.WebhookEventMessage;
import com.company.payment.payment_backend.model.Subscription;
import com.company.payment.payment_backend.model.WebhookEvent;
import com.company.payment.payment_backend.repository.SubscriptionRepository;
import com.company.payment.payment_backend.repository.WebhookEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WebhookEventAsyncListener {

    private final WebhookEventRepository webhookRepo;
    private  final SubscriptionRepository subscriptionRepo;
    private  ObjectMapper mapper = new ObjectMapper();

    public WebhookEventAsyncListener(WebhookEventRepository webhookRepo, SubscriptionRepository subscriptionRepo) {
        this.webhookRepo = webhookRepo;
        this.subscriptionRepo = subscriptionRepo;
    }

    @Async("webhookExecutor")
    @EventListener
    public void handle(WebhookEventMessage msg) {
        // propagate correlation ID into async thread
        if (msg.getCorrelationId() != null) {
            MDC.put("correlationId", msg.getCorrelationId());
        }

        try {
            // idempotency
            WebhookEvent existing = webhookRepo.findByEventId(msg.getEventId());
            if (existing != null) return;

            // persist event
            WebhookEvent event = new WebhookEvent();
            event.setEventId(msg.getEventId());
            event.setEventType(msg.getEventType());
            event.setPayload(msg.getPayload());
            event.setReceivedAt(LocalDateTime.now());
            webhookRepo.save(event);

            // subscription updates (ARB-aware)
            if (msg.getEventType() != null && msg.getEventType().toLowerCase().contains("subscription")) {
                String gatewayId = extractGatewaySubscriptionId(msg.getPayload());
                if (gatewayId != null && !gatewayId.isBlank()) {
                    Subscription sub = subscriptionRepo.findByGatewaySubscriptionId(gatewayId);
                    if (sub != null) {
                        String type = msg.getEventType().toLowerCase();
                        if (type.contains("suspended")) {
                            sub.setStatus("SUSPENDED");
                        } else if (type.contains("cancelled")) {
                            sub.setStatus("CANCELLED");
                        } else if (type.contains("reactivated") || type.contains("created")) {
                            sub.setStatus("ACTIVE");
                        }
                        subscriptionRepo.save(sub);
                    }
                }
            }
        } catch (Exception e) {
            // log and continue; avoid blocking ingestion
            // In production, add DLQ/retry strategy
        } finally {
            MDC.remove("correlationId");
        }
    }

    private String extractGatewaySubscriptionId(String payload) {
        try {
            JsonNode root = mapper.readTree(payload);
            JsonNode idNode = root.path("payload").path("customerSubscriptionId");
            return idNode.isMissingNode() ? null : idNode.asText();
        } catch (Exception e) {
            return null;
        }
    }
}