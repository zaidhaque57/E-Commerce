package com.ecommerce.controller;

import com.ecommerce.dto.RevenueResponse;
import com.ecommerce.dto.TopProductResponse;
import com.ecommerce.service.AnalyticsService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }


    // =========================================================
    // TOP SELLING PRODUCTS
    // =========================================================

    @GetMapping("/top-products")
    public List<TopProductResponse> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {

        if (limit < 1 || limit > 50) {

            throw new IllegalArgumentException(
                    "Limit must be between 1 and 50"
            );
        }

        return analyticsService
                .getTopSellingProducts(limit);
    }


    // =========================================================
    // DAILY REVENUE
    // =========================================================

    @GetMapping("/revenue")
    public List<RevenueResponse> getDailyRevenue(
            @RequestParam(defaultValue = "7") int days) {

        if (days < 1 || days > 90) {

            throw new IllegalArgumentException(
                    "Days must be between 1 and 90"
            );
        }

        return analyticsService
                .getDailyRevenue(days);
    }
}