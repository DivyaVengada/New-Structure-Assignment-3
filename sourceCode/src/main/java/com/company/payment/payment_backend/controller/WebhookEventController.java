// src/main/java/com/company/payment/payment_backend/controller/WebhookEventController.java
package com.company.payment.payment_backend.controller;

import com.company.payment.payment_backend.events.WebhookEventMessage;
import com.company.payment.payment_backend.model.dto.WebhookEventRequest;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class WebhookEventController {

    private final ApplicationEventPublisher publisher;

    public WebhookEventController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/event")
    public ResponseEntity<String> ingest(@RequestBody WebhookEventRequest req) {
        String correlationId = MDC.get("correlationId");
        publisher.publishEvent(new WebhookEventMessage(
                req.getEventId(),
                req.getEventType(),
                req.getPayload(),
                correlationId
        ));
        // Return quickly; processing happens asynchronously
        return ResponseEntity.ok("ACCEPTED");
    }
}