package com.ecommerce.controller;

import com.ecommerce.dto.WishlistResponse;
import com.ecommerce.service.WishlistService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(
            WishlistService wishlistService) {

        this.wishlistService = wishlistService;
    }

    // ADD PRODUCT
    @PostMapping("/{productId}")
    public WishlistResponse addToWishlist(
            Authentication authentication,
            @PathVariable Long productId) {

        String email =
                authentication.getName();

        return wishlistService.addToWishlist(
                email,
                productId
        );
    }

    // GET WISHLIST
    @GetMapping
    public List<WishlistResponse> getWishlist(
            Authentication authentication) {

        String email =
                authentication.getName();

        return wishlistService.getWishlist(email);
    }

    // REMOVE PRODUCT
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {

        String email =
                authentication.getName();

        wishlistService.removeFromWishlist(
                email,
                productId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}