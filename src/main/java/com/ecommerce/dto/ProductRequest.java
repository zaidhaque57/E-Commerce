package com.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Product name must be between 2 and 100 characters"
    )
    private String name;

    @NotBlank(message = "Product description is required")
    @Size(
            max = 1000,
            message = "Product description cannot exceed 1000 characters"
    )
    private String description;

    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than 0"
    )
    private double price;

    @Min(
            value = 0,
            message = "Stock cannot be negative"
    )
    private int stock;

    private String imageUrl;

    @NotNull(message = "Category is required")
    private Long categoryId;

    public ProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}