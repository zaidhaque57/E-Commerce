package com.ecommerce.service;

import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.dto.PaymentResponse;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Payment;
import com.ecommerce.entity.PaymentStatus;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;

import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaymentResponse makePayment(
            String email,
            Long orderId,
            PaymentRequest request) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );

        if (!order.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You cannot pay for this order"
            );
        }

        if (order.getStatus()
                != OrderStatus.PENDING) {

            throw new BadRequestException(
                    "Payment is not available for this order"
            );
        }

        if (paymentRepository
                .findByOrder(order)
                .isPresent()) {

            throw new BadRequestException(
                    "Payment already exists for this order"
            );
        }

        String paymentMethod =
                request.getPaymentMethod()
                        .trim()
                        .toUpperCase();

        if (!paymentMethod.equals("CARD")
                && !paymentMethod.equals("UPI")
                && !paymentMethod.equals("COD")) {

            throw new BadRequestException(
                    "Payment method must be CARD, UPI or COD"
            );
        }

        String paymentResult =
                request.getPaymentResult()
                        .trim()
                        .toUpperCase();

        if (!paymentResult.equals("SUCCESS")
                && !paymentResult.equals("FAILED")) {

            throw new BadRequestException(
                    "Payment result must be SUCCESS or FAILED"
            );
        }

        PaymentStatus paymentStatus;

        if (paymentResult.equals("SUCCESS")) {

            paymentStatus =
                    PaymentStatus.SUCCESS;

        } else {

            paymentStatus =
                    PaymentStatus.FAILED;
        }

        String transactionId =
                "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Payment payment =
                new Payment();

        payment.setOrder(order);

        payment.setAmount(
                order.getTotalAmount()
        );

        payment.setStatus(
                paymentStatus
        );

        payment.setPaymentMethod(
                paymentMethod
        );

        payment.setTransactionId(
                transactionId
        );

        payment.setCreatedAt(
                LocalDateTime.now()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        if (paymentStatus
                == PaymentStatus.SUCCESS) {

            order.setStatus(
                    OrderStatus.CONFIRMED
            );

            orderRepository.save(order);

        } else {

            restoreStock(order);

            order.setStatus(
                    OrderStatus.CANCELLED
            );

            orderRepository.save(order);
        }

        return convertToResponse(
                savedPayment
        );
    }

    public PaymentResponse getPayment(
            String email,
            Long orderId) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );

        if (!order.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You cannot access this payment"
            );
        }

        Payment payment =
                paymentRepository
                        .findByOrder(order)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found"
                                )
                        );

        return convertToResponse(payment);
    }

    private void restoreStock(Order order) {

        order.getItems()
                .forEach(item -> {

                    Product product =
                            item.getProduct();

                    product.setStock(
                            product.getStock()
                                    + item.getQuantity()
                    );
                });
    }

    private PaymentResponse convertToResponse(
            Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getCreatedAt()
        );
    }
}