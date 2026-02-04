// src/main/java/com/company/payment/payment_backend/events/WebhookEventMessage.java
package com.company.payment.payment_backend.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class WebhookEventMessage {
    private String eventId;

    public WebhookEventMessage() {
    }

    public WebhookEventMessage(String eventId, String eventType, String payload, String correlationId) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.correlationId = correlationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    private String eventType;
    private String payload;
    private String correlationId; // propagate tracing
}