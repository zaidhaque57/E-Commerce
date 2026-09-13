package com.ecommerce.service;

import com.ecommerce.dto.OrderItemResponse;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.dto.ShippingAddressResponse;

import com.ecommerce.entity.Address;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ShippingAddress;
import com.ecommerce.entity.User;

import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            AddressRepository addressRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public OrderResponse checkout(
            String email,
            Long addressId) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Address address =
                addressRepository
                        .findByIdAndUser(
                                addressId,
                                user
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found"
                                )
                        );

        Cart cart =
                cartRepository.findByUser(user)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart not found"
                                )
                        );

        List<CartItem> cartItems =
                cartItemRepository.findAll()
                        .stream()
                        .filter(item ->
                                item.getCart()
                                        .getId()
                                        .equals(cart.getId()))
                        .toList();

        if (cartItems.isEmpty()) {
            throw new BadRequestException(
                    "Cart is empty"
            );
        }

        ShippingAddress shippingAddress =
                new ShippingAddress(
                        address.getFullName(),
                        address.getPhone(),
                        address.getAddressLine(),
                        address.getCity(),
                        address.getState(),
                        address.getPostalCode(),
                        address.getCountry()
                );

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);

        List<OrderItem> orderItems =
                new ArrayList<>();

        double totalAmount = 0;

        for (CartItem cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            if (cartItem.getQuantity()
                    > product.getStock()) {

                throw new BadRequestException(
                        "Not enough stock for "
                                + product.getName()
                );
            }

            double price =
                    product.getPrice();

            int quantity =
                    cartItem.getQuantity();

            double subtotal =
                    price * quantity;

            totalAmount += subtotal;

            OrderItem orderItem =
                    new OrderItem(
                            order,
                            product,
                            quantity,
                            price
                    );

            orderItems.add(orderItem);

            product.setStock(
                    product.getStock()
                            - quantity
            );
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder =
                orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return convertToResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        return orderRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public OrderResponse getOrderById(
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
                    "You cannot access this order"
            );
        }

        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(
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
                    "You cannot cancel this order"
            );
        }

        if (order.getStatus()
                        != OrderStatus.PENDING &&
                order.getStatus()
                        != OrderStatus.CONFIRMED) {

            throw new BadRequestException(
                    "Order cannot be cancelled now"
            );
        }

        for (OrderItem item :
                order.getItems()) {

            Product product =
                    item.getProduct();

            product.setStock(
                    product.getStock()
                            + item.getQuantity()
            );
        }

        order.setStatus(
                OrderStatus.CANCELLED
        );

        Order savedOrder =
                orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public OrderResponse getOrderByIdForAdmin(
            Long orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );

        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );

        if (order.getStatus()
                == OrderStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled order cannot be updated"
            );
        }

        if (order.getStatus()
                == OrderStatus.DELIVERED) {

            throw new BadRequestException(
                    "Delivered order cannot be updated"
            );
        }

        order.setStatus(newStatus);

        Order savedOrder =
                orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    private OrderResponse convertToResponse(
            Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item -> {

                            Product product =
                                    item.getProduct();

                            double subtotal =
                                    item.getPrice()
                                            * item.getQuantity();

                            return new OrderItemResponse(
                                    product.getId(),
                                    product.getName(),
                                    item.getQuantity(),
                                    item.getPrice(),
                                    subtotal
                            );
                        })
                        .toList();

        ShippingAddressResponse
                shippingAddressResponse = null;

        ShippingAddress shippingAddress =
                order.getShippingAddress();

        if (shippingAddress != null) {

            shippingAddressResponse =
                    new ShippingAddressResponse(
                            shippingAddress.getFullName(),
                            shippingAddress.getPhone(),
                            shippingAddress.getAddressLine(),
                            shippingAddress.getCity(),
                            shippingAddress.getState(),
                            shippingAddress.getPostalCode(),
                            shippingAddress.getCountry()
                    );
        }

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                shippingAddressResponse,
                items
        );
    }
}