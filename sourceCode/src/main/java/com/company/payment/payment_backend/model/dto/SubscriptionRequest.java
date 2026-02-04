package com.company.payment.payment_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class SubscriptionRequest {
    private Double monthlyAmount;

    public SubscriptionRequest() {
    }

    public SubscriptionRequest(Double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public Double getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(Double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }
}
