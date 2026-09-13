package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;

import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;

import com.ecommerce.specification.ProductSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE PRODUCT
    public Product createProduct(ProductRequest request) {

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return productRepository.save(product);
    }

    // GET ALL PRODUCTS
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // GET PRODUCT BY ID
    public Optional<Product> getProductById(Long id) {

        return productRepository.findById(id);
    }

    // UPDATE PRODUCT
    public Product updateProduct(
            Long id,
            ProductRequest request) {

        Product existingProduct =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"
                                )
                        );

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        existingProduct.setName(
                request.getName()
        );

        existingProduct.setDescription(
                request.getDescription()
        );

        existingProduct.setPrice(
                request.getPrice()
        );

        existingProduct.setStock(
                request.getStock()
        );

        existingProduct.setImageUrl(
                request.getImageUrl()
        );

        existingProduct.setCategory(
                category
        );

        return productRepository.save(
                existingProduct
        );
    }

    // DELETE PRODUCT
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Product not found"
            );
        }

        productRepository.deleteById(id);
    }

    // DYNAMIC SEARCH + FILTER
    public Page<Product> searchProducts(
            String search,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        Specification<Product> specification =
                Specification
                        .where(
                                ProductSpecification.hasName(
                                        search
                                )
                        )
                        .and(
                                ProductSpecification.hasCategory(
                                        categoryId
                                )
                        )
                        .and(
                                ProductSpecification.hasMinPrice(
                                        minPrice
                                )
                        )
                        .and(
                                ProductSpecification.hasMaxPrice(
                                        maxPrice
                                )
                        );

        return productRepository.findAll(
                specification,
                pageable
        );
    }

    // ENTITY → RESPONSE DTO
    public ProductResponse convertToResponse(
            Product product) {

        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {

            categoryId =
                    product.getCategory().getId();

            categoryName =
                    product.getCategory().getName();
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                categoryId,
                categoryName
        );
    }

    // GET PRODUCT RESPONSE BY ID
    public Optional<ProductResponse>
    getProductResponseById(Long id) {

        return productRepository
                .findById(id)
                .map(this::convertToResponse);
    }

    // SEARCH → RESPONSE DTO
    public Page<ProductResponse>
    searchProductResponses(
            String search,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        return searchProducts(
                search,
                categoryId,
                minPrice,
                maxPrice,
                pageable
        ).map(this::convertToResponse);
    }
}