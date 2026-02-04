package com.company.payment.payment_backend.controller;

import com.company.payment.payment_backend.model.dto.*;
import com.company.payment.payment_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/purchase")
    public PurchaseResponse purchase(@RequestBody PurchaseRequest request) {
        return orderService.purchase(request);
    }

    @PostMapping("/authorize")
    public AuthorizeResponse authorize(@RequestBody AuthorizeRequest request) {
        return orderService.authorize(request);
    }

    @PostMapping("/capture")
    public CaptureResponse capture(@RequestBody CaptureRequest request) {
        return orderService.capture(request);
    }

    @PostMapping("/cancel")
    public CancelResponse cancel(@RequestBody CancelRequest request) {
        return orderService.cancel(request);
    }

    @PostMapping("/refund")
    public RefundResponse refund(@RequestBody RefundRequest request) {
        return orderService.refund(request);
    }
}
