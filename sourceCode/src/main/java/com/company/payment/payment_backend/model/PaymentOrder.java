package com.company.payment.payment_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public PaymentOrder() {
    }

    private String orderId;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getGatewayAuthCode() {
        return gatewayAuthCode;
    }

    public void setGatewayAuthCode(String gatewayAuthCode) {
        this.gatewayAuthCode = gatewayAuthCode;
    }

    private Double amount;
    private String status;
    private LocalDateTime createdAt;
    // New fields
    private String gateway;                 // e.g., "AUTHORIZE_NET"
    private String gatewayTransactionId;    // Authorize.Net transId
    private String gatewayAuthCode;


    public PaymentOrder(Long id, String orderId, Double amount, String status, LocalDateTime createdAt, String gateway, String gatewayTransactionId, String gatewayAuthCode) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.gateway = gateway;
        this.gatewayTransactionId = gatewayTransactionId;
        this.gatewayAuthCode = gatewayAuthCode;
    }// optional
}
