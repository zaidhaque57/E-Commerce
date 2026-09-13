package com.ecommerce.service;

import com.ecommerce.dto.CategoryRequest;
import com.ecommerce.dto.CategoryResponse;

import com.ecommerce.entity.Category;

import com.ecommerce.exception.ResourceNotFoundException;

import com.ecommerce.repository.CategoryRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(
            CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public CategoryResponse createCategory(
            CategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory =
                categoryRepository.save(category);

        return convertToResponse(savedCategory);
    }

    // GET ALL
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET BY ID
    public CategoryResponse getCategoryById(Long id) {

        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        return convertToResponse(category);
    }

    // UPDATE
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category existingCategory =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                )
                        );

        existingCategory.setName(
                request.getName()
        );

        existingCategory.setDescription(
                request.getDescription()
        );

        Category updatedCategory =
                categoryRepository.save(
                        existingCategory
                );

        return convertToResponse(updatedCategory);
    }

    // DELETE
    public void deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Category not found"
            );
        }

        categoryRepository.deleteById(id);
    }

    // ENTITY → DTO
    private CategoryResponse convertToResponse(
            Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}