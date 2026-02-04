package com.company.payment.payment_backend.service;

import com.company.payment.payment_backend.gateway.AuthorizeNetClient;
import com.company.payment.payment_backend.model.PaymentOrder;
import com.company.payment.payment_backend.model.dto.*;
import com.company.payment.payment_backend.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.TransactionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final AuthorizeNetClient authorizeNetClient;

    public OrderService(OrderRepository orderRepository, AuthorizeNetClient authorizeNetClient) {
        this.orderRepository = orderRepository;
        this.authorizeNetClient = authorizeNetClient;
    }

    // -------------------------------
    // PURCHASE (AUTH + CAPTURE)
    // -------------------------------
    public PurchaseResponse purchase(PurchaseRequest request) {

        CreateTransactionResponse apiResponse =
                authorizeNetClient.chargeUsingNonce(
                        BigDecimal.valueOf(request.getAmount()),
                        request.getPaymentMethodNonce()
                );

        String orderId = UUID.randomUUID().toString();
        PurchaseResponse response = new PurchaseResponse();
        log.info("API Response: {}", apiResponse);

        if (isSuccess(apiResponse)) {
            TransactionResponse txn = apiResponse.getTransactionResponse();

            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setOrderId(orderId);
            paymentOrder.setAmount(request.getAmount());
            paymentOrder.setStatus("PURCHASED");
            paymentOrder.setCreatedAt(LocalDateTime.now());
            paymentOrder.setGateway("AUTHORIZE_NET");
            paymentOrder.setGatewayTransactionId(txn.getTransId());
            paymentOrder.setGatewayAuthCode(txn.getAuthCode());

            orderRepository.save(paymentOrder);

            response.setOrderId(orderId);
            response.setStatus("PURCHASED");
            response.setMessage("Payment processed successfully");
        } else {
            response.setOrderId(orderId);
            response.setStatus("FAILED");
            response.setMessage(extractError(apiResponse));
        }

        return response;
    }

    // -------------------------------
    // AUTHORIZE ONLY
    // -------------------------------
    public AuthorizeResponse authorize(AuthorizeRequest request) {

        CreateTransactionResponse apiResponse =
                authorizeNetClient.authorizeUsingNonce(
                        BigDecimal.valueOf(request.getAmount()),
                        request.getPaymentMethodNonce()
                );

        String orderId = UUID.randomUUID().toString();
        AuthorizeResponse  response = new AuthorizeResponse();

        if (isSuccess(apiResponse)) {
            TransactionResponse txn = apiResponse.getTransactionResponse();

            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setOrderId(orderId);
            paymentOrder.setAmount(request.getAmount());
            paymentOrder.setStatus("AUTHORIZED");
            paymentOrder.setCreatedAt(LocalDateTime.now());
            paymentOrder.setGateway("AUTHORIZE_NET");
            paymentOrder.setGatewayTransactionId(txn.getTransId());
            paymentOrder.setGatewayAuthCode(txn.getAuthCode());

            orderRepository.save(paymentOrder);

            response.setOrderId(orderId);
            response.setStatus("AUTHORIZED");
            response.setMessage("Payment authorized");
        } else {
            response.setOrderId(orderId);
            response.setStatus("FAILED");
            response.setMessage(extractError(apiResponse));
        }

        return response;
    }

    // -------------------------------
    // CAPTURE
    // -------------------------------
    public CaptureResponse capture(CaptureRequest request) {

        PaymentOrder paymentOrder = orderRepository.findByOrderId(request.getOrderId());
        CaptureResponse response = new CaptureResponse();

        if (paymentOrder == null || !"AUTHORIZED".equals(paymentOrder.getStatus())) {
            response.setOrderId(request.getOrderId());
            response.setStatus("FAILED");
            response.setMessage("Order not found or not authorized");
            return response;
        }

        CreateTransactionResponse apiResponse =
                authorizeNetClient.capturePreviouslyAuthorized(
                        BigDecimal.valueOf(request.getAmount()),
                        paymentOrder.getGatewayTransactionId()
                );

        if (isSuccess(apiResponse)) {
            paymentOrder.setStatus("CAPTURED");
            orderRepository.save(paymentOrder);

            response.setOrderId(paymentOrder.getOrderId());
            response.setStatus("CAPTURED");
            response.setMessage("Payment captured");
        } else {
            response.setOrderId(paymentOrder.getOrderId());
            response.setStatus("FAILED");
            response.setMessage(extractError(apiResponse));
        }

        return response;
    }

    // -------------------------------
    // CANCEL (LOCAL ONLY)
    // -------------------------------
    public CancelResponse cancel(CancelRequest request) {

        PaymentOrder paymentOrder = orderRepository.findByOrderId(request.getOrderId());
        CancelResponse response = new CancelResponse();

        if (paymentOrder == null) {
            response.setOrderId(request.getOrderId());
            response.setStatus("FAILED");
            response.setMessage("Order not found");
            return response;
        }

        paymentOrder.setStatus("CANCELLED");
        orderRepository.save(paymentOrder);

        response.setOrderId(paymentOrder.getOrderId());
        response.setStatus("CANCELLED");
        response.setMessage("Order cancelled");
        return response;
    }

    // -------------------------------
    // REFUND
    // -------------------------------
    public RefundResponse refund(RefundRequest request) {

        PaymentOrder paymentOrder = orderRepository.findByOrderId(request.getOrderId());
        RefundResponse response = new RefundResponse();

        if (paymentOrder == null || (!"PURCHASED".equals(paymentOrder.getStatus()) && !"CAPTURED".equals(paymentOrder.getStatus()))) {
            response.setOrderId(request.getOrderId());
            response.setStatus("FAILED");
            response.setMessage("Order not refundable");
            return response;
        }

        CreateTransactionResponse apiResponse =
                authorizeNetClient.refundTransaction(
                        BigDecimal.valueOf(request.getAmount()),
                        paymentOrder.getGatewayTransactionId()
                );

        if (isSuccess(apiResponse)) {
            paymentOrder.setStatus("REFUNDED");
            orderRepository.save(paymentOrder);

            response.setOrderId(paymentOrder.getOrderId());
            response.setStatus("REFUNDED");
            response.setMessage("Payment refunded");
        } else {
            response.setOrderId(paymentOrder.getOrderId());
            response.setStatus("FAILED");
            response.setMessage(extractError(apiResponse));
        }

        return response;
    }

    // -------------------------------
    // HELPERS
    // -------------------------------
    private boolean isSuccess(CreateTransactionResponse res) {
        return res != null
                && res.getMessages().getResultCode() == MessageTypeEnum.OK
                && res.getTransactionResponse() != null
                && "1".equals(res.getTransactionResponse().getResponseCode());
    }

    private String extractError(CreateTransactionResponse res) {
        if (res == null) return "Null response from gateway";

        if (res.getTransactionResponse() != null &&
                res.getTransactionResponse().getErrors() != null &&
                !res.getTransactionResponse().getErrors().getError().isEmpty()) {
            return res.getTransactionResponse().getErrors().getError().get(0).getErrorText();
        }

        if (res.getMessages() != null &&
                res.getMessages().getMessage() != null &&
                !res.getMessages().getMessage().isEmpty()) {
            return res.getMessages().getMessage().get(0).getText();
        }

        return "Unknown payment error";
    }
}