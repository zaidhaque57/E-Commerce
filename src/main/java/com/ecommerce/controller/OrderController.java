package com.ecommerce.controller;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.dto.OrderResponse;

import com.ecommerce.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService) {

        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request) {

        String email =
                authentication.getName();

        return orderService.checkout(
                email,
                request.getAddressId()
        );
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(
            Authentication authentication) {

        String email =
                authentication.getName();

        return orderService.getUserOrders(email);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            Authentication authentication,
            @PathVariable Long id) {

        String email =
                authentication.getName();

        return orderService.getOrderById(
                email,
                id
        );
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(
            Authentication authentication,
            @PathVariable Long id) {

        String email =
                authentication.getName();

        return orderService.cancelOrder(
                email,
                id
        );
    }
}