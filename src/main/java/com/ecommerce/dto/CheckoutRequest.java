package com.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CheckoutRequest {

    @NotNull(message = "Address ID is required")
    @Positive(message = "Address ID must be greater than 0")
    private Long addressId;

    public CheckoutRequest() {
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}