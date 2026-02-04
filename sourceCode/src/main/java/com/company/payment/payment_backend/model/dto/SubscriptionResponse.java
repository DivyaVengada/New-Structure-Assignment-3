package com.company.payment.payment_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class SubscriptionResponse {
    private String subscriptionId;

    public SubscriptionResponse() {
    }

    public SubscriptionResponse(String subscriptionId, String status, String message) {
        this.subscriptionId = subscriptionId;
        this.status = status;
        this.message = message;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String status;
    private String message;
}
