package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;
import com.restapi.styleswap.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryUtils {

    private final CategoryRepository categoryRepository;

    public CategoryUtils(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category mapCategoryToEntity(CategoryEdditDto categoryDto){
        var category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return category;
    }

    public CategoryDto mapCategoryToDto(Category category){
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

    public Category getCategoryFromDB(long id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", id)
        );
    }
}
