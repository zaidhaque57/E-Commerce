package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;

import com.ecommerce.service.AddressService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService) {

        this.addressService = addressService;
    }

    // CREATE ADDRESS
    @PostMapping
    public AddressResponse createAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {

        String email =
                authentication.getName();

        return addressService.createAddress(
                email,
                request
        );
    }

    // GET ALL ADDRESSES
    @GetMapping
    public List<AddressResponse> getAllAddresses(
            Authentication authentication) {

        String email =
                authentication.getName();

        return addressService.getAllAddresses(email);
    }

    // GET SINGLE ADDRESS
    @GetMapping("/{id}")
    public AddressResponse getAddressById(
            Authentication authentication,
            @PathVariable Long id) {

        String email =
                authentication.getName();

        return addressService.getAddressById(
                email,
                id
        );
    }

    // UPDATE ADDRESS
    @PutMapping("/{id}")
    public AddressResponse updateAddress(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {

        String email =
                authentication.getName();

        return addressService.updateAddress(
                email,
                id,
                request
        );
    }

    // DELETE ADDRESS
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable Long id) {

        String email =
                authentication.getName();

        addressService.deleteAddress(
                email,
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}