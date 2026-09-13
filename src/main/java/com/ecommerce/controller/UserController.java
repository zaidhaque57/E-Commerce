package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.LoginResponse;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.entity.User;
import com.ecommerce.service.JwtService;
import com.ecommerce.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public UserResponse registerUser(
            @Valid @RequestBody RegisterRequest request) {

        User savedUser =
                userService.registerUser(request);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public LoginResponse loginUser(
            @Valid @RequestBody LoginRequest request) {

        User user =
                userService.loginUser(
                        request.getEmail(),
                        request.getPassword()
                );

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        UserResponse userResponse =
                new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                );

        return new LoginResponse(
                token,
                userResponse
        );
    }

    // =========================================================
    // CURRENT LOGGED-IN USER
    // =========================================================

    @GetMapping("/me")
    public UserResponse getCurrentUser(
            Authentication authentication) {

        String email =
                authentication.getName();

        User user =
                userService.getUserByEmail(email);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}