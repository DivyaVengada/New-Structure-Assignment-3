package com.company.payment.payment_backend.service;

import com.company.payment.payment_backend.gateway.AuthorizeNetClient;
import com.company.payment.payment_backend.model.PaymentOrder;
import com.company.payment.payment_backend.model.dto.*;
import com.company.payment.payment_backend.repository.OrderRepository;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.contract.v1.TransactionResponse;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class PaymentOrderServiceTest {

    private OrderRepository orderRepository;
    private AuthorizeNetClient authorizeNetClient;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        authorizeNetClient = mock(AuthorizeNetClient.class);
        orderService = new OrderService(orderRepository, authorizeNetClient);
    }

    private CreateTransactionResponse successResponse() {
        CreateTransactionResponse res = new CreateTransactionResponse();

        MessagesType messages = new MessagesType();
        messages.setResultCode(MessageTypeEnum.OK);
        res.setMessages(messages);

        TransactionResponse txn = new TransactionResponse();
        txn.setResponseCode("1");
        txn.setTransId("TXN123");
        txn.setAuthCode("AUTH123");
        res.setTransactionResponse(txn);

        return res;
    }

    @Test
    void testPurchaseSuccess() {
        PurchaseRequest req = new PurchaseRequest();
        req.setAmount(100.0);
        req.setPaymentMethodNonce("nonce123");

        when(authorizeNetClient.chargeUsingNonce(BigDecimal.valueOf(100.0), "nonce123"))
                .thenReturn(successResponse());

        PurchaseResponse res = orderService.purchase(req);

        assertEquals("PURCHASED", res.getStatus());
        verify(orderRepository, times(1)).save(any(PaymentOrder.class));
    }

    @Test
    void testAuthorizeSuccess() {
        AuthorizeRequest req = new AuthorizeRequest();
        req.setAmount(50.0);
        req.setPaymentMethodNonce("nonceXYZ");

        when(authorizeNetClient.authorizeUsingNonce(BigDecimal.valueOf(50.0), "nonceXYZ"))
                .thenReturn(successResponse());

        AuthorizeResponse res = orderService.authorize(req);

        assertEquals("AUTHORIZED", res.getStatus());
        verify(orderRepository, times(1)).save(any(PaymentOrder.class));
    }

    @Test
    void testCaptureSuccess() {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setOrderId("ORDER123");
        paymentOrder.setAmount(100.0);
        paymentOrder.setStatus("AUTHORIZED");
        paymentOrder.setGatewayTransactionId("TXN123");
        paymentOrder.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findByOrderId("ORDER123")).thenReturn(paymentOrder);
        when(authorizeNetClient.capturePreviouslyAuthorized(BigDecimal.valueOf(100.0), "TXN123"))
                .thenReturn(successResponse());

        CaptureRequest req = new CaptureRequest();
        req.setOrderId("ORDER123");
        req.setAmount(100.0);

        CaptureResponse res = orderService.capture(req);

        assertEquals("CAPTURED", res.getStatus());
        verify(orderRepository).save(paymentOrder);
    }

    @Test
    void testRefundSuccess() {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setOrderId("ORDER123");
        paymentOrder.setAmount(100.0);
        paymentOrder.setStatus("PURCHASED");
        paymentOrder.setGatewayTransactionId("TXN123");
        paymentOrder.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findByOrderId("ORDER123")).thenReturn(paymentOrder);
        when(authorizeNetClient.refundTransaction(BigDecimal.valueOf(50.0), "TXN123"))
                .thenReturn(successResponse());

        RefundRequest req = new RefundRequest();
        req.setOrderId("ORDER123");
        req.setAmount(50.0);

        RefundResponse res = orderService.refund(req);

        assertEquals("REFUNDED", res.getStatus());
        verify(orderRepository).save(paymentOrder);
    }
}
