package com.company.payment.payment_backend.gateway;

import com.company.payment.payment_backend.model.dto.AuthorizeNetProperties;
import lombok.extern.slf4j.Slf4j;
import net.authorize.Environment;
import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class AuthorizeNetClient {

    private final AuthorizeNetProperties properties;

    public AuthorizeNetClient(AuthorizeNetProperties properties) {
        this.properties = properties;
    }

    private void init() {
        Environment env = "PRODUCTION".equalsIgnoreCase(properties.getEnvironment())
                ? Environment.PRODUCTION
                : Environment.SANDBOX;

        ApiOperationBase.setEnvironment(env);
        log.info("Properties :{}",properties.getApiLoginId());
        log.info("Properties :{}",properties.getTransactionKey());

        MerchantAuthenticationType merchantAuth = new MerchantAuthenticationType();
        merchantAuth.setName(properties.getApiLoginId());
        merchantAuth.setTransactionKey(properties.getTransactionKey());

        ApiOperationBase.setMerchantAuthentication(merchantAuth);
    }

    // ---------------------------------------------------------
    // 1. PURCHASE (AUTH + CAPTURE)
    // ---------------------------------------------------------
    public CreateTransactionResponse chargeUsingNonce(BigDecimal amount, String nonce) {
        init();

        PaymentType payment = buildOpaquePayment(nonce);

        TransactionRequestType txn = new TransactionRequestType();
        txn.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txn.setAmount(amount);
        txn.setPayment(payment);

        return execute(txn);
    }

    // ---------------------------------------------------------
    // 2. AUTHORIZE ONLY
    // ---------------------------------------------------------
    public CreateTransactionResponse authorizeUsingNonce(BigDecimal amount, String nonce) {
        init();

        PaymentType payment = buildOpaquePayment(nonce);

        TransactionRequestType txn = new TransactionRequestType();
        txn.setTransactionType(TransactionTypeEnum.AUTH_ONLY_TRANSACTION.value());
        txn.setAmount(amount);
        txn.setPayment(payment);

        return execute(txn);
    }

    // ---------------------------------------------------------
    // 3. CAPTURE PREVIOUS AUTH
    // ---------------------------------------------------------
    public CreateTransactionResponse capturePreviouslyAuthorized(BigDecimal amount, String authTransactionId) {
        init();

        TransactionRequestType txn = new TransactionRequestType();
        txn.setTransactionType(TransactionTypeEnum.PRIOR_AUTH_CAPTURE_TRANSACTION.value());
        txn.setRefTransId(authTransactionId);
        txn.setAmount(amount);

        return execute(txn);
    }

    // ---------------------------------------------------------
    // 4. REFUND
    // ---------------------------------------------------------
    public CreateTransactionResponse refundTransaction(BigDecimal amount, String originalTransactionId) {
        init();

        // For refunds using opaque data, Authorize.Net allows passing only the transaction ID
        TransactionRequestType txn = new TransactionRequestType();
        txn.setTransactionType(TransactionTypeEnum.REFUND_TRANSACTION.value());
        txn.setRefTransId(originalTransactionId);
        txn.setAmount(amount);

        return execute(txn);
    }

    // ---------------------------------------------------------
    // Helper: Build Opaque Payment (Nonce)
    // ---------------------------------------------------------
    private PaymentType buildOpaquePayment(String nonce) {
        OpaqueDataType opaque = new OpaqueDataType();
        opaque.setDataDescriptor("COMMON.ACCEPT.INAPP.PAYMENT");
        opaque.setDataValue(nonce);

        PaymentType payment = new PaymentType();
        payment.setOpaqueData(opaque);

        return payment;
    }

    // ---------------------------------------------------------
    // Helper: Execute Transaction
    // ---------------------------------------------------------
    private CreateTransactionResponse execute(TransactionRequestType txn) {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionRequest(txn);

        CreateTransactionController controller = new CreateTransactionController(request);
        controller.execute();

        return controller.getApiResponse();
    }
}