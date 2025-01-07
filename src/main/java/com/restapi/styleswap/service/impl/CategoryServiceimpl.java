package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;
import com.restapi.styleswap.repository.CategoryRepository;
import com.restapi.styleswap.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceimpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceimpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto createCategory(CategoryEdditDto categoryDto) {

        Category category = mapCategoryToEntity(categoryDto);

        Category savedCategory = categoryRepository.save(category);

        return mapCategoryToDto(savedCategory);
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = getCategoryFromDB(categoryId);

        return mapCategoryToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(this::mapCategoryToDto).toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CategoryDto updateCategory(long categoryId, CategoryEdditDto categoryDto) {
        Category category = getCategoryFromDB(categoryId);
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category savedCategory = categoryRepository.save(category);

        return mapCategoryToDto(savedCategory);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCategory(long categoryId) {
        Category category = getCategoryFromDB(categoryId);
        categoryRepository.delete(category);
    }

    private Category getCategoryFromDB(long id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", id)
        );
    }

    private Category mapCategoryToEntity(CategoryEdditDto categoryDto){
        var category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return category;
    }

    private CategoryDto mapCategoryToDto(Category category){
        var categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setCreatedAt(category.getCreatedAt());
        categoryDto.setUpdatedAt(category.getUpdatedAt());
        var clothes = category.getClothes()==null ? 0 : category.getClothes().size();
        categoryDto.setClothesCount(clothes);

        return categoryDto;
    }
}
