package com.ecommerce.controller;

import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.dto.PaymentResponse;

import com.ecommerce.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/order/{orderId}")
    public PaymentResponse makePayment(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request) {

        String email =
                authentication.getName();

        return paymentService.makePayment(
                email,
                orderId,
                request
        );
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getPayment(
            Authentication authentication,
            @PathVariable Long orderId) {

        String email =
                authentication.getName();

        return paymentService.getPayment(
                email,
                orderId
        );
    }
}