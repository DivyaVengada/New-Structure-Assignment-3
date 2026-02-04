package com.company.payment.payment_backend.service;

import com.company.payment.payment_backend.model.Subscription;
import com.company.payment.payment_backend.model.WebhookEvent;
import com.company.payment.payment_backend.model.dto.WebhookEventRequest;
import com.company.payment.payment_backend.model.dto.WebhookEventResponse;
import com.company.payment.payment_backend.repository.SubscriptionRepository;
import com.company.payment.payment_backend.repository.WebhookEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WebhookEventService {

    private final WebhookEventRepository webhookEventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebhookEventService(WebhookEventRepository webhookEventRepository, SubscriptionRepository subscriptionRepository) {
        this.webhookEventRepository = webhookEventRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public WebhookEventResponse handleEvent(WebhookEventRequest request) {
        WebhookEvent existing = webhookEventRepository.findByEventId(request.getEventId());
        if (existing != null) {
            WebhookEventResponse response = new WebhookEventResponse();
            response.setEventId(request.getEventId());
            response.setStatus("IGNORED");
            response.setMessage("Duplicate event");
            return response;
        }

        WebhookEvent event = new WebhookEvent();
        event.setEventId(request.getEventId());
        event.setEventType(request.getEventType());
        event.setPayload(request.getPayload());
        event.setReceivedAt(LocalDateTime.now());
        webhookEventRepository.save(event);

        // Try to interpret subscription-related events
        if (request.getEventType() != null &&
                request.getEventType().toLowerCase().contains("subscription")) {
            handleSubscriptionWebhook(request);
        }

        WebhookEventResponse response = new WebhookEventResponse();
        response.setEventId(request.getEventId());
        response.setStatus("PROCESSED");
        response.setMessage("Event processed");
        return response;
    }

    private void handleSubscriptionWebhook(WebhookEventRequest request) {
        try {
            JsonNode root = objectMapper.readTree(request.getPayload());
            // This assumes Authorize.Net-like structure. You can adjust based on real payload.
            JsonNode payload = root.path("payload");
            String gatewaySubscriptionId = payload.path("customerSubscriptionId").asText(null);

            if (gatewaySubscriptionId == null || gatewaySubscriptionId.isEmpty()) {
                return;
            }

            Subscription subscription = subscriptionRepository.findBySubscriptionId(gatewaySubscriptionId);
            if (subscription == null) {
                // or change to findByGatewaySubscriptionId if you add that repo method
                subscription = subscriptionRepository.findBySubscriptionId(gatewaySubscriptionId);
            }

            if (subscription != null) {
                if (request.getEventType().contains("suspended")) {
                    subscription.setStatus("SUSPENDED");
                } else if (request.getEventType().contains("cancelled")) {
                    subscription.setStatus("CANCELLED");
                } else if (request.getEventType().contains("created") ||
                        request.getEventType().contains("reactivated")) {
                    subscription.setStatus("ACTIVE");
                }
                subscriptionRepository.save(subscription);
            }
        } catch (Exception e) {
            // log in real code
        }
    }
}