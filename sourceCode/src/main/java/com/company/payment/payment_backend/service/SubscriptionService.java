package com.company.payment.payment_backend.service;
import com.company.payment.payment_backend.gateway.AuthorizeNetARBClient;
import com.company.payment.payment_backend.model.Subscription;
import com.company.payment.payment_backend.model.dto.SubscriptionRequest;
import com.company.payment.payment_backend.model.dto.SubscriptionResponse;
import com.company.payment.payment_backend.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SubscriptionService {

    private  final SubscriptionRepository subscriptionRepository;
    private final AuthorizeNetARBClient arbClient;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, AuthorizeNetARBClient arbClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.arbClient = arbClient;
    }

    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        // 1. Create ARB subscription in Authorize.Net
        String gatewaySubscriptionId = arbClient.createMonthlySubscription(request.getMonthlyAmount());

        // 2. Persist local subscription linked to gateway ID
        String subscriptionId = UUID.randomUUID().toString();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionId);
        subscription.setMonthlyAmount(request.getMonthlyAmount());
        subscription.setStatus("ACTIVE");
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setGatewaySubscriptionId(gatewaySubscriptionId);
        subscriptionRepository.save(subscription);

        // 3. Build response
        SubscriptionResponse response = new SubscriptionResponse();
        response.setSubscriptionId(subscriptionId);
        response.setStatus("ACTIVE");
        response.setMessage("Subscription created and registered with Authorize.Net");
        return response;
    }
}
