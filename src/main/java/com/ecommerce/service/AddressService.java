package com.ecommerce.service;

import com.ecommerce.dto.AddressRequest;
import com.ecommerce.dto.AddressResponse;

import com.ecommerce.entity.Address;
import com.ecommerce.entity.User;

import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository) {

        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    @Transactional
    public AddressResponse createAddress(
            String email,
            AddressRequest request) {

        User user = getUser(email);

        // If this address should become default,
        // remove default status from existing addresses.
        if (request.isDefaultAddress()) {
            removeDefaultAddress(user);
        }

        Address address = new Address();

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefaultAddress(
                request.isDefaultAddress()
        );
        address.setUser(user);

        Address savedAddress =
                addressRepository.save(address);

        return convertToResponse(savedAddress);
    }

    // GET ALL USER ADDRESSES
    public List<AddressResponse> getAllAddresses(
            String email) {

        User user = getUser(email);

        return addressRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET SINGLE ADDRESS
    public AddressResponse getAddressById(
            String email,
            Long addressId) {

        User user = getUser(email);

        Address address =
                addressRepository
                        .findByIdAndUser(
                                addressId,
                                user
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found"
                                )
                        );

        return convertToResponse(address);
    }

    // UPDATE
    @Transactional
    public AddressResponse updateAddress(
            String email,
            Long addressId,
            AddressRequest request) {

        User user = getUser(email);

        Address address =
                addressRepository
                        .findByIdAndUser(
                                addressId,
                                user
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found"
                                )
                        );

        if (request.isDefaultAddress()) {
            removeDefaultAddress(user);
        }

        address.setFullName(
                request.getFullName()
        );

        address.setPhone(
                request.getPhone()
        );

        address.setAddressLine(
                request.getAddressLine()
        );

        address.setCity(
                request.getCity()
        );

        address.setState(
                request.getState()
        );

        address.setPostalCode(
                request.getPostalCode()
        );

        address.setCountry(
                request.getCountry()
        );

        address.setDefaultAddress(
                request.isDefaultAddress()
        );

        return convertToResponse(
                addressRepository.save(address)
        );
    }

    // DELETE
    @Transactional
    public void deleteAddress(
            String email,
            Long addressId) {

        User user = getUser(email);

        Address address =
                addressRepository
                        .findByIdAndUser(
                                addressId,
                                user
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found"
                                )
                        );

        addressRepository.delete(address);
    }

    // REMOVE DEFAULT FROM ALL USER ADDRESSES
    private void removeDefaultAddress(User user) {

        List<Address> addresses =
                addressRepository.findByUser(user);

        for (Address address : addresses) {

            address.setDefaultAddress(false);
        }

        addressRepository.saveAll(addresses);
    }

    // GET USER
    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    // ENTITY → DTO
    private AddressResponse convertToResponse(
            Address address) {

        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getAddressLine(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefaultAddress()
        );
    }
}