package com.company.payment.payment_backend.service;

import com.company.payment.payment_backend.gateway.AuthorizeNetARBClient;
import com.company.payment.payment_backend.model.Subscription;
import com.company.payment.payment_backend.model.dto.SubscriptionRequest;
import com.company.payment.payment_backend.model.dto.SubscriptionResponse;
import com.company.payment.payment_backend.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest {

    private SubscriptionRepository subscriptionRepository;
    private AuthorizeNetARBClient arbClient;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setup() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        arbClient = mock(AuthorizeNetARBClient.class);

        // Matches @RequiredArgsConstructor(onConstructor = @__(@Autowired))
        subscriptionService = new SubscriptionService(subscriptionRepository, arbClient);
    }

    @Test
    void createSubscription_success() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setMonthlyAmount(29.99);

        when(arbClient.createMonthlySubscription(29.99))
                .thenReturn("GATEWAY_SUB_123");

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SubscriptionResponse response = subscriptionService.createSubscription(request);

        assertNotNull(response);
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("Subscription created and registered with Authorize.Net", response.getMessage());
        assertNotNull(response.getSubscriptionId());

        verify(arbClient, times(1)).createMonthlySubscription(29.99);
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void createSubscription_arbFailure() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setMonthlyAmount(49.99);

        when(arbClient.createMonthlySubscription(49.99))
                .thenThrow(new RuntimeException("ARB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> subscriptionService.createSubscription(request));

        assertEquals("ARB error", ex.getMessage());
        verify(subscriptionRepository, never()).save(any());
    }
}