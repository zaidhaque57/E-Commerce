package com.ecommerce.service;

import com.ecommerce.dto.DashboardResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;

import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashboardService(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {

        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }


    // =========================================================
    // GET DASHBOARD
    // =========================================================

    public DashboardResponse getDashboard() {

        // =====================================================
        // BASIC STATISTICS
        // =====================================================

        long totalUsers =
                userRepository.count();

        long totalProducts =
                productRepository.count();

        long totalOrders =
                orderRepository.count();


        // =====================================================
        // CUSTOMER COUNT
        // =====================================================

        long customerCount =
                userRepository.countByRole("CUSTOMER");


        // =====================================================
        // LOW STOCK PRODUCTS
        // =====================================================

        List<Product> products =
                productRepository.findAll();

        long lowStockProducts =
                products.stream()
                        .filter(product ->
                                product.getStock() <= 5)
                        .count();


        // =====================================================
        // ALL ORDERS
        // =====================================================

        List<Order> orders =
                orderRepository.findAll();


        // =====================================================
        // TOTAL REVENUE
        // =====================================================

        double totalRevenue =
                orders.stream()
                        .filter(order ->
                                order.getStatus()
                                        != OrderStatus.CANCELLED)
                        .mapToDouble(
                                Order::getTotalAmount
                        )
                        .sum();


        // =====================================================
        // TODAY
        // =====================================================

        LocalDate today =
                LocalDate.now();

        LocalDateTime startOfToday =
                today.atStartOfDay();

        LocalDateTime startOfTomorrow =
                today.plusDays(1)
                        .atStartOfDay();


        long todayOrders =
                orders.stream()
                        .filter(order ->
                                order.getCreatedAt() != null)
                        .filter(order ->
                                !order.getCreatedAt()
                                        .isBefore(startOfToday))
                        .filter(order ->
                                order.getCreatedAt()
                                        .isBefore(startOfTomorrow))
                        .count();


        double todayRevenue =
                orders.stream()
                        .filter(order ->
                                order.getStatus()
                                        != OrderStatus.CANCELLED)
                        .filter(order ->
                                order.getCreatedAt() != null)
                        .filter(order ->
                                !order.getCreatedAt()
                                        .isBefore(startOfToday))
                        .filter(order ->
                                order.getCreatedAt()
                                        .isBefore(startOfTomorrow))
                        .mapToDouble(
                                Order::getTotalAmount
                        )
                        .sum();


        // =====================================================
        // CURRENT MONTH
        // =====================================================

        LocalDate firstDayOfMonth =
                today.withDayOfMonth(1);

        LocalDateTime startOfMonth =
                firstDayOfMonth.atStartOfDay();

        LocalDateTime startOfNextMonth =
                firstDayOfMonth
                        .plusMonths(1)
                        .atStartOfDay();


        double monthlyRevenue =
                orders.stream()
                        .filter(order ->
                                order.getStatus()
                                        != OrderStatus.CANCELLED)
                        .filter(order ->
                                order.getCreatedAt() != null)
                        .filter(order ->
                                !order.getCreatedAt()
                                        .isBefore(startOfMonth))
                        .filter(order ->
                                order.getCreatedAt()
                                        .isBefore(startOfNextMonth))
                        .mapToDouble(
                                Order::getTotalAmount
                        )
                        .sum();


        // =====================================================
        // ORDER STATUS
        // =====================================================

        long pendingOrders =
                orderRepository.countByStatus(
                        OrderStatus.PENDING
                );

        long confirmedOrders =
                orderRepository.countByStatus(
                        OrderStatus.CONFIRMED
                );

        long processingOrders =
                orderRepository.countByStatus(
                        OrderStatus.PROCESSING
                );

        long shippedOrders =
                orderRepository.countByStatus(
                        OrderStatus.SHIPPED
                );

        long deliveredOrders =
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                );

        long cancelledOrders =
                orderRepository.countByStatus(
                        OrderStatus.CANCELLED
                );


        // =====================================================
        // RETURN DASHBOARD RESPONSE
        // =====================================================

        return new DashboardResponse(

                totalUsers,
                totalProducts,
                totalOrders,
                totalRevenue,

                customerCount,
                lowStockProducts,

                todayOrders,
                todayRevenue,
                monthlyRevenue,

                pendingOrders,
                confirmedOrders,
                processingOrders,
                shippedOrders,
                deliveredOrders,
                cancelledOrders
        );
    }
}