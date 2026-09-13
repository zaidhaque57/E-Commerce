package com.ecommerce.service;

import com.ecommerce.dto.RevenueResponse;
import com.ecommerce.dto.TopProductResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public AnalyticsService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    // =========================================================
    // TOP SELLING PRODUCTS
    // =========================================================

    public List<TopProductResponse> getTopSellingProducts(
            int limit) {

        if (limit < 1) {
            limit = 10;
        }

        List<TopProductResponse> products =
                orderItemRepository.findTopSellingProducts();

        if (products.size() <= limit) {
            return products;
        }

        return products.subList(0, limit);
    }


    // =========================================================
    // DAILY REVENUE
    // =========================================================

    public List<RevenueResponse> getDailyRevenue(
            int days) {

        if (days < 1) {
            days = 7;
        }

        if (days > 90) {
            days = 90;
        }

        List<Order> orders =
                orderRepository.findAll();

        LocalDate today =
                LocalDate.now();

        List<RevenueResponse> result =
                new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {

            LocalDate date =
                    today.minusDays(i);

            LocalDateTime start =
                    date.atStartOfDay();

            LocalDateTime end =
                    date.plusDays(1)
                            .atStartOfDay();

            long orderCount =
                    orders.stream()
                            .filter(order ->
                                    order.getCreatedAt() != null)
                            .filter(order ->
                                    !order.getCreatedAt()
                                            .isBefore(start))
                            .filter(order ->
                                    order.getCreatedAt()
                                            .isBefore(end))
                            .count();

            double revenue =
                    orders.stream()
                            .filter(order ->
                                    order.getStatus()
                                            != OrderStatus.CANCELLED)
                            .filter(order ->
                                    order.getCreatedAt() != null)
                            .filter(order ->
                                    !order.getCreatedAt()
                                            .isBefore(start))
                            .filter(order ->
                                    order.getCreatedAt()
                                            .isBefore(end))
                            .mapToDouble(
                                    Order::getTotalAmount)
                            .sum();

            result.add(
                    new RevenueResponse(
                            date.toString(),
                            orderCount,
                            revenue
                    )
            );
        }

        return result;
    }
}