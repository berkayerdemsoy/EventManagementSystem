package com.example.event_service_app.service;

import com.example.event_service_client.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);
    CategoryDto getCategoryById(Long id);
    CategoryDto updateCategory(Long id, CategoryDto dto);
    void deleteCategory(Long id);
    List<CategoryDto> getAllCategories();
}

