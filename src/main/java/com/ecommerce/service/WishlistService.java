package com.ecommerce.service;

import com.ecommerce.dto.WishlistResponse;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Wishlist;

import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ADD TO WISHLIST
    public WishlistResponse addToWishlist(
            String email,
            Long productId) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        if (wishlistRepository.existsByUserAndProduct(
                user,
                product)) {

            throw new ConflictException(
                    "Product already exists in wishlist"
            );
        }

        Wishlist wishlist =
                new Wishlist(
                        user,
                        product
                );

        Wishlist savedWishlist =
                wishlistRepository.save(wishlist);

        return convertToResponse(savedWishlist);
    }

    // GET WISHLIST
    public List<WishlistResponse> getWishlist(
            String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        return wishlistRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // REMOVE FROM WISHLIST
    public void removeFromWishlist(
            String email,
            Long productId) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        Wishlist wishlist =
                wishlistRepository
                        .findByUserAndProduct(
                                user,
                                product
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product is not in wishlist"
                                )
                        );

        wishlistRepository.delete(wishlist);
    }

    // ENTITY → DTO
    private WishlistResponse convertToResponse(
            Wishlist wishlist) {

        Product product =
                wishlist.getProduct();

        return new WishlistResponse(
                wishlist.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl()
        );
    }
}