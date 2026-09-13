package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;

import com.ecommerce.entity.Product;

import com.ecommerce.service.FileStorageService;
import com.ecommerce.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public AdminProductController(
            ProductService productService,
            FileStorageService fileStorageService) {

        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    // =========================
    // CREATE PRODUCT
    // =========================

    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product =
                productService.createProduct(request);

        return productService.convertToResponse(product);
    }

    // =========================
    // GET PRODUCTS
    // =========================

    @GetMapping
    public Page<ProductResponse> getProducts(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice,

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
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 100) {

            throw new IllegalArgumentException(
                    "Size must be between 1 and 100"
            );
        }

        if (minPrice != null &&
                minPrice < 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null &&
                maxPrice < 0) {

            throw new IllegalArgumentException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null &&
                maxPrice != null &&
                minPrice > maxPrice) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        Sort sort;

        if (direction.equalsIgnoreCase("asc")) {

            sort =
                    Sort.by(sortBy)
                            .ascending();

        } else if (
                direction.equalsIgnoreCase("desc")) {

            sort =
                    Sort.by(sortBy)
                            .descending();

        } else {

            throw new IllegalArgumentException(
                    "Direction must be asc or desc"
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        return productService
                .searchProductResponses(
                        search,
                        categoryId,
                        minPrice,
                        maxPrice,
                        pageable
                );
    }

    // =========================
    // GET SINGLE PRODUCT
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse>
    getProductById(
            @PathVariable Long id) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "Product ID must be greater than 0"
            );
        }

        return productService
                .getProductResponseById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

    // =========================
    // UPDATE PRODUCT
    // =========================

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "Product ID must be greater than 0"
            );
        }

        Product product =
                productService.updateProduct(
                        id,
                        request
                );

        return productService
                .convertToResponse(product);
    }

    // =========================
    // DELETE PRODUCT
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "Product ID must be greater than 0"
            );
        }

        productService.deleteProduct(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // =========================
    // UPLOAD IMAGE
    // =========================

    @PostMapping("/upload-image")
    public String uploadImage(
            @RequestParam("file")
            MultipartFile file) {

        return fileStorageService
                .saveFile(file);
    }
}