package com.company.payment.payment_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public class PurchaseRequest {
    private Double amount;
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PurchaseRequest() {
    }

    public PurchaseRequest(Double amount, String paymentMethodNonce) {
        this.amount = amount;
        this.paymentMethodNonce = paymentMethodNonce;
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }

    private String paymentMethodNonce;
}
