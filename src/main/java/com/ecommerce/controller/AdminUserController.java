package com.ecommerce.controller;

import com.ecommerce.dto.AdminUserResponse;
import com.ecommerce.dto.UpdateUserRoleRequest;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @GetMapping
    public Page<AdminUserResponse> getAllUsers(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            String role,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Size must be between 1 and 100");
        }

        if (!sortBy.equals("id")
                && !sortBy.equals("name")
                && !sortBy.equals("email")
                && !sortBy.equals("role")) {

            throw new IllegalArgumentException(
                    "Invalid sort field");
        }

        Sort.Direction sortDirection;

        if (direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        } else if (direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else {
            throw new IllegalArgumentException(
                    "Direction must be asc or desc");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return userService.getAllUsers(
                search,
                role,
                pageable
        );
    }

    // =========================================================
    // GET ONE USER
    // =========================================================

    @GetMapping("/{id}")
    public AdminUserResponse getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id);
    }

    // =========================================================
    // UPDATE ROLE
    // =========================================================

    @PutMapping("/{id}/role")
    public AdminUserResponse updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            Authentication authentication) {

        return userService.updateUserRole(
                id,
                request.getRole(),
                authentication.getName()
        );
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        userService.deleteUser(
                id,
                authentication.getName()
        );

        return "User deleted successfully";
    }
}