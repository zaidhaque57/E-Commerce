package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(
            min = 2,
            max = 50,
            message = "Category name must be between 2 and 50 characters"
    )
    private String name;

    @NotBlank(message = "Category description is required")
    @Size(
            max = 255,
            message = "Description cannot exceed 255 characters"
    )
    private String description;

    public CategoryRequest() {
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
}