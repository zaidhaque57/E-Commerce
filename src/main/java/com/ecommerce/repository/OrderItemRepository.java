package com.ecommerce.repository;

import com.ecommerce.dto.TopProductResponse;
import com.ecommerce.entity.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // =========================================================
    // TOP SELLING PRODUCTS
    // =========================================================

    @Query("""
        SELECT new com.ecommerce.dto.TopProductResponse(
            oi.product.id,
            oi.product.name,
            SUM(oi.quantity),
            SUM(oi.quantity * oi.price)
        )
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.status <> com.ecommerce.entity.OrderStatus.CANCELLED
        GROUP BY oi.product.id, oi.product.name
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<TopProductResponse> findTopSellingProducts();
}