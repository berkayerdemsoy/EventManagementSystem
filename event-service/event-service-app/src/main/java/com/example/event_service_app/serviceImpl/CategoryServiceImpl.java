package com.example.event_service_app.serviceImpl;

import com.example.ems_common.exceptions.AlreadyExistsException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.event_service_app.entity.Category;
import com.example.event_service_app.mapper.CategoryMapper;
import com.example.event_service_app.repository.CategoryRepository;
import com.example.event_service_app.service.CategoryService;
import com.example.event_service_client.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new AlreadyExistsException("Category with name '" + dto.getName() + "' already exists");
        }
        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(category.getName())
                && categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new AlreadyExistsException("Category with name '" + dto.getName() + "' already exists");
        }

        categoryMapper.updateCategoryFromDto(dto, category);
        Category updated = categoryRepository.save(category);
        return categoryMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }
}

