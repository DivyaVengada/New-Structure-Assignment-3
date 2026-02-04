package com.company.payment.payment_backend.service;

import com.company.payment.payment_backend.model.Subscription;
import com.company.payment.payment_backend.model.WebhookEvent;
import com.company.payment.payment_backend.model.dto.WebhookEventRequest;
import com.company.payment.payment_backend.model.dto.WebhookEventResponse;
import com.company.payment.payment_backend.repository.SubscriptionRepository;
import com.company.payment.payment_backend.repository.WebhookEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WebhookEventServiceTest {

    private WebhookEventRepository webhookRepo;
    private SubscriptionRepository subscriptionRepo;
    private WebhookEventService webhookService;

    @BeforeEach
    void setup() {
        webhookRepo = mock(WebhookEventRepository.class);
        subscriptionRepo = mock(SubscriptionRepository.class);
        webhookService = new WebhookEventService(webhookRepo, subscriptionRepo);
    }

    @Test
    void testHandleEvent_NewEventSaved() {
        // Arrange
        WebhookEventRequest req = new WebhookEventRequest();
        req.setEventId("EVT123");
        req.setEventType("net.authorize.payment.authcapture.created");
        req.setPayload("{\"payload\": {\"customerSubscriptionId\": \"SUB123\"}}");

        when(webhookRepo.findByEventId("EVT123")).thenReturn(null);
        when(webhookRepo.save(any(WebhookEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WebhookEventResponse res = webhookService.handleEvent(req);

        // Assert
        assertEquals("PROCESSED", res.getStatus());
        verify(webhookRepo, times(1)).save(any(WebhookEvent.class));
    }

    @Test
    void testHandleEvent_DuplicateIgnored() {
        // Arrange
        WebhookEvent existing = new WebhookEvent();
        existing.setEventId("EVT123");

        when(webhookRepo.findByEventId("EVT123")).thenReturn(existing);

        WebhookEventRequest req = new WebhookEventRequest();
        req.setEventId("EVT123");

        // Act
        WebhookEventResponse res = webhookService.handleEvent(req);

        // Assert
        assertEquals("IGNORED", res.getStatus());
        verify(webhookRepo, never()).save(any());
        verify(subscriptionRepo, never()).save(any());
    }

    @Test
    void testHandleEvent_SubscriptionUpdated() {
        WebhookEventRequest req = new WebhookEventRequest();
        req.setEventId("EVT999");
        req.setEventType("net.authorize.customer.subscription.suspended");
        req.setPayload("{\"payload\": {\"customerSubscriptionId\": \"GATEWAY123\"}}");

        when(webhookRepo.findByEventId("EVT999")).thenReturn(null);

        Subscription sub = new Subscription();
        sub.setSubscriptionId("LOCAL123");
        sub.setGatewaySubscriptionId("GATEWAY123");
        sub.setStatus("ACTIVE");
        sub.setCreatedAt(LocalDateTime.now());

        // ✅ Ensure this matches the service method
        when(subscriptionRepo.findByGatewaySubscriptionId("GATEWAY123"))
                .thenReturn(sub);

        when(webhookRepo.save(any(WebhookEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(subscriptionRepo.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WebhookEventResponse res = webhookService.handleEvent(req);

        assertEquals("PROCESSED", res.getStatus());
        assertEquals("ACTIVE", sub.getStatus()); // should now pass

       verify(webhookRepo, times(1)).save(any(WebhookEvent.class));
    }
    @Test
    void testHandleEvent_SubscriptionNotFound_NoCrash() {
        // Arrange
        WebhookEventRequest req = new WebhookEventRequest();
        req.setEventId("EVT777");
        req.setEventType("net.authorize.customer.subscription.created");
        req.setPayload("{\"payload\": {\"customerSubscriptionId\": \"UNKNOWN\"}}");

        when(webhookRepo.findByEventId("EVT777")).thenReturn(null);
        when(subscriptionRepo.findByGatewaySubscriptionId("UNKNOWN")).thenReturn(null);

        when(webhookRepo.save(any(WebhookEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WebhookEventResponse res = webhookService.handleEvent(req);

        // Assert
        assertEquals("PROCESSED", res.getStatus());

        verify(subscriptionRepo, never()).save(any());
        verify(webhookRepo, times(1)).save(any(WebhookEvent.class));
    }
}