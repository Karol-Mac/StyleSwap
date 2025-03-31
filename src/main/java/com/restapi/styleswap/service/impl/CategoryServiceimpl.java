package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;
import com.restapi.styleswap.repository.CategoryRepository;
import com.restapi.styleswap.service.CategoryService;
import com.restapi.styleswap.utils.CategoryUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryUtils categoryUtils;

    public CategoryServiceimpl(CategoryRepository categoryRepository, CategoryUtils categoryUtils) {
        this.categoryRepository = categoryRepository;
        this.categoryUtils = categoryUtils;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto createCategory(CategoryEdditDto categoryDto) {

        Category category = categoryUtils.mapCategoryToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        return categoryUtils.mapCategoryToDto(savedCategory);
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = categoryUtils.getCategoryFromDB(categoryId);

        return categoryUtils.mapCategoryToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(categoryUtils::mapCategoryToDto).toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CategoryDto updateCategory(long categoryId, CategoryEdditDto categoryDto) {
        Category category = categoryUtils.getCategoryFromDB(categoryId);
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category savedCategory = categoryRepository.save(category);

        return categoryUtils.mapCategoryToDto(savedCategory);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCategory(long categoryId) {
        Category category = categoryUtils.getCategoryFromDB(categoryId);
        categoryRepository.delete(category);
    }
}