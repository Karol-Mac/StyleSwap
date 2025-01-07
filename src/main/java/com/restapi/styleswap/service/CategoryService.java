package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryEdditDto categoryDto);
    CategoryDto getCategory(long categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto updateCategory(long categoryId, CategoryEdditDto categoryDto);
    void deleteCategory(long categoryId);
}
