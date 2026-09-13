package com.ecommerce.service;

import com.ecommerce.dto.AdminUserResponse;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.entity.Address;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Wishlist;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ConflictException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final WishlistRepository wishlistRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            OrderRepository orderRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            AddressRepository addressRepository,
            WishlistRepository wishlistRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.wishlistRepository = wishlistRepository;
    }

    // =========================================================
    // REGISTER USER
    // =========================================================

    public User registerUser(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new ConflictException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Every normal registration creates CUSTOMER
        user.setRole("CUSTOMER");

        return userRepository.save(user);
    }

    // =========================================================
    // LOGIN USER
    // =========================================================

    public User loginUser(
            String email,
            String password) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid email or password"
                                )
                        );

        // Compare entered password with encrypted password
        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    // =========================================================
    // CREATE ADMIN
    // =========================================================

    public User createAdmin(
            String name,
            String email,
            String password) {

        if (userRepository.existsByEmail(email)) {

            throw new ConflictException(
                    "Email is already registered"
            );
        }

        User admin = new User();

        admin.setName(name);
        admin.setEmail(email);

        admin.setPassword(
                passwordEncoder.encode(password)
        );

        admin.setRole("ADMIN");

        return userRepository.save(admin);
    }

    // =========================================================
    // ADMIN - GET ALL USERS
    // =========================================================

    public Page<AdminUserResponse> getAllUsers(
            String search,
            String role,
            Pageable pageable) {

        // Validate role
        if (role != null
                && !role.equals("ADMIN")
                && !role.equals("CUSTOMER")) {

            throw new BadRequestException(
                    "Role must be ADMIN or CUSTOMER"
            );
        }

        // Convert empty search to null
        if (search != null && search.isBlank()) {
            search = null;
        }

        return userRepository
                .searchUsers(search, role, pageable)
                .map(this::convertToAdminUserResponse);
    }

    // =========================================================
    // ADMIN - GET USER BY ID
    // =========================================================

    public AdminUserResponse getUserById(Long id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        return convertToAdminUserResponse(user);
    }

    // =========================================================
    // ADMIN - UPDATE USER ROLE
    // =========================================================

    @Transactional
    public AdminUserResponse updateUserRole(
            Long id,
            String newRole,
            String currentAdminEmail) {

        // Validate role
        if (!newRole.equals("ADMIN")
                && !newRole.equals("CUSTOMER")) {

            throw new BadRequestException(
                    "Role must be ADMIN or CUSTOMER"
            );
        }

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        // Prevent admin from changing their own role
        if (user.getEmail().equalsIgnoreCase(currentAdminEmail)
                && !user.getRole().equals(newRole)) {

            throw new BadRequestException(
                    "You cannot change your own admin role"
            );
        }

        // Prevent removing the last admin
        if (user.getRole().equals("ADMIN")
                && newRole.equals("CUSTOMER")) {

            long adminCount =
                    userRepository.countByRole("ADMIN");

            if (adminCount <= 1) {

                throw new BadRequestException(
                        "Cannot remove the last admin"
                );
            }
        }

        user.setRole(newRole);

        User savedUser =
                userRepository.save(user);

        return convertToAdminUserResponse(savedUser);
    }

    // =========================================================
    // ADMIN - DELETE USER
    // =========================================================

    @Transactional
    public void deleteUser(
            Long id,
            String currentAdminEmail) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: " + id
                                )
                        );

        // Prevent admin from deleting themselves
        if (user.getEmail()
                .equalsIgnoreCase(currentAdminEmail)) {

            throw new BadRequestException(
                    "You cannot delete your own account"
            );
        }

        // Prevent deleting the last admin
        if (user.getRole().equals("ADMIN")) {

            long adminCount =
                    userRepository.countByRole("ADMIN");

            if (adminCount <= 1) {

                throw new BadRequestException(
                        "Cannot delete the last admin"
                );
            }
        }

        // Do not delete users who have orders
        if (orderRepository.existsByUser(user)) {

            throw new ConflictException(
                    "User cannot be deleted because they have existing orders"
            );
        }

        // =====================================================
        // DELETE CART
        // =====================================================

        Cart cart =
                cartRepository.findByUser(user)
                        .orElse(null);

        if (cart != null) {

            cartItemRepository.deleteByCart(cart);

            cartRepository.delete(cart);
        }

        // =====================================================
        // DELETE ADDRESSES
        // =====================================================

        List<Address> addresses =
                addressRepository.findByUser(user);

        if (!addresses.isEmpty()) {

            addressRepository.deleteAll(addresses);
        }

        // =====================================================
        // DELETE WISHLIST
        // =====================================================

        List<Wishlist> wishlists =
                wishlistRepository.findByUser(user);

        if (!wishlists.isEmpty()) {

            wishlistRepository.deleteAll(wishlists);
        }

        // =====================================================
        // DELETE USER
        // =====================================================

        userRepository.delete(user);
    }

    // =========================================================
    // CONVERT USER → ADMIN USER RESPONSE
    // =========================================================

    private AdminUserResponse convertToAdminUserResponse(
            User user) {

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}