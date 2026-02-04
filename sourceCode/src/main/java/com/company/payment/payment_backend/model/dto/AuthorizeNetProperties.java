package com.company.payment.payment_backend.model.dto;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "authorizenet")

public class AuthorizeNetProperties {
    private String apiLoginId;

    public AuthorizeNetProperties() {
    }

    public String getApiLoginId() {
        return apiLoginId;
    }

    public AuthorizeNetProperties(String apiLoginId, String transactionKey, String environment) {
        this.apiLoginId = apiLoginId;
        this.transactionKey = transactionKey;
        this.environment = environment;
    }

    public void setApiLoginId(String apiLoginId) {
        this.apiLoginId = apiLoginId;
    }

    public String getTransactionKey() {
        return transactionKey;
    }

    public void setTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    private String transactionKey;
    private String environment; // "SANDBOX" or "PRODUCTION"
}
