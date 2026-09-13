package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;

import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // GET OR CREATE CART
    public Cart getOrCreateCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() ->
                        cartRepository.save(new Cart(user))
                );
    }

    // ADD PRODUCT TO CART
    public CartItem addToCart(
            String email,
            Long productId,
            int quantity) {

        if (quantity <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (product.getStock() < quantity) {
            throw new BadRequestException(
                    "Not enough stock available");
        }

        CartItem cartItem =
                cartItemRepository
                        .findByCartAndProduct(cart, product)
                        .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Not enough stock available");
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem(
                    cart,
                    product,
                    quantity
            );
        }

        return cartItemRepository.save(cartItem);
    }

    // GET CART ITEMS
    public List<CartItem> getCartItems(String email) {

        Cart cart = getOrCreateCart(email);

        return cartItemRepository.findAll()
                .stream()
                .filter(item ->
                        item.getCart()
                                .getId()
                                .equals(cart.getId()))
                .toList();
    }

    // REMOVE CART ITEM
    public void removeItem(
            String email,
            Long cartItemId) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found"));

        if (!cartItem.getCart()
                .getId()
                .equals(cart.getId())) {

            throw new BadRequestException(
                    "You cannot remove this item");
        }

        cartItemRepository.delete(cartItem);
    }

    // CLEAR CART
    public void clearCart(String email) {

        Cart cart = getOrCreateCart(email);

        List<CartItem> items =
                cartItemRepository.findAll()
                        .stream()
                        .filter(item ->
                                item.getCart()
                                        .getId()
                                        .equals(cart.getId()))
                        .toList();

        cartItemRepository.deleteAll(items);
    }

    // UPDATE CART ITEM QUANTITY
public CartItem updateQuantity(
        String email,
        Long cartItemId,
        int quantity) {

    if (quantity <= 0) {
        throw new RuntimeException(
                "Quantity must be greater than 0");
    }

    Cart cart = getOrCreateCart(email);

    CartItem cartItem =
            cartItemRepository.findById(cartItemId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Cart item not found"));

    // Security check
    if (!cartItem.getCart()
            .getId()
            .equals(cart.getId())) {

        throw new BadRequestException(
                "You cannot update this item");
    }

    Product product = cartItem.getProduct();

    // Stock check
    if (quantity > product.getStock()) {
        throw new RuntimeException(
                "Not enough stock available");
    }

    cartItem.setQuantity(quantity);

    return cartItemRepository.save(cartItem);
}
}