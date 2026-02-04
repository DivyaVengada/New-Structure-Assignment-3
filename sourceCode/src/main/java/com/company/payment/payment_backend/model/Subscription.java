package com.company.payment.payment_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
public class Subscription {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Double getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(Double monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGatewaySubscriptionId() {
        return gatewaySubscriptionId;
    }

    public void setGatewaySubscriptionId(String gatewaySubscriptionId) {
        this.gatewaySubscriptionId = gatewaySubscriptionId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subscriptionId;        // local UUID
    private Double monthlyAmount;
    private String status;
    private LocalDateTime createdAt;

    public Subscription() {
    }

    public Subscription(Long id, String subscriptionId, Double monthlyAmount, String status, LocalDateTime createdAt, String gatewaySubscriptionId) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.monthlyAmount = monthlyAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.gatewaySubscriptionId = gatewaySubscriptionId;
    }

    // NEW: link to Authorize.Net ARB subscription
    private String gatewaySubscriptionId;
}
