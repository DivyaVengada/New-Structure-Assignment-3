package com.company.payment.payment_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CancelRequest {
    public CancelRequest() {
    }

    public String getOrderId() {
        return orderId;
    }

    public CancelRequest(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String orderId;
}
