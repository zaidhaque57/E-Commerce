package com.ecommerce.controller;

import com.ecommerce.dto.DashboardResponse;
import com.ecommerce.dto.OrderResponse;

import com.ecommerce.entity.OrderStatus;

import com.ecommerce.service.DashboardService;
import com.ecommerce.service.OrderService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final OrderService orderService;
    private final DashboardService dashboardService;

    public AdminController(
            OrderService orderService,
            DashboardService dashboardService) {

        this.orderService = orderService;
        this.dashboardService = dashboardService;
    }

    // =========================
    // DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {

        return dashboardService.getDashboard();
    }

    // =========================
    // ORDERS
    // =========================

    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {

        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrderById(
            @PathVariable Long id) {

        return orderService
                .getOrderByIdForAdmin(id);
    }

    @PutMapping("/orders/{id}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return orderService.updateOrderStatus(
                id,
                status
        );
    }
}