package com.company.payment.payment_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CaptureRequest {
    public String getOrderId() {
        return orderId;
    }

    public CaptureRequest() {
    }

    public CaptureRequest(String orderId, Double amount) {
        this.orderId = orderId;
        this.amount = amount;
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

    private String orderId;
    private Double amount;
}
