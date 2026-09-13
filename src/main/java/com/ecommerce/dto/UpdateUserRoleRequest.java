package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUserRoleRequest {

    @NotBlank(message = "Role is required")
    @Pattern(
        regexp = "ADMIN|CUSTOMER",
        message = "Role must be ADMIN or CUSTOMER"
    )
    private String role;

    public UpdateUserRoleRequest() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}