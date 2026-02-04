package com.company.payment.payment_backend.controller;
import com.company.payment.payment_backend.model.dto.SubscriptionRequest;
import com.company.payment.payment_backend.model.dto.SubscriptionResponse;
import com.company.payment.payment_backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/create")
    public SubscriptionResponse create(@RequestBody SubscriptionRequest request) {
        return subscriptionService.createSubscription(request);
    }
}
