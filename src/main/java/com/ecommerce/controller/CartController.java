package com.ecommerce.controller;

import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ADD PRODUCT TO CART
    @PostMapping("/add")
    public CartItem addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        String email = authentication.getName();

        return cartService.addToCart(
                email,
                productId,
                quantity
        );
    }

    // GET CART
    @GetMapping
    public List<CartItem> getCart(
            Authentication authentication) {

        String email = authentication.getName();

        return cartService.getCartItems(email);
    }

    // REMOVE ITEM
    @DeleteMapping("/items/{id}")
    public String removeItem(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        cartService.removeItem(email, id);

        return "Item removed from cart";
    }

    // CLEAR CART
    @DeleteMapping("/clear")
    public String clearCart(
            Authentication authentication) {

        String email = authentication.getName();

        cartService.clearCart(email);

        return "Cart cleared successfully";
    }

    // UPDATE CART ITEM QUANTITY
@PutMapping("/items/{id}")
public CartItem updateQuantity(
        Authentication authentication,
        @PathVariable Long id,
        @RequestParam int quantity) {

    String email = authentication.getName();

    return cartService.updateQuantity(
            email,
            id,
            quantity
    );
}
}