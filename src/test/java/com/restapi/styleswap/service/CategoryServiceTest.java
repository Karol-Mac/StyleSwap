package com.restapi.styleswap.service;


import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;
import com.restapi.styleswap.repository.CategoryRepository;
import com.restapi.styleswap.service.impl.CategoryServiceimpl;
import com.restapi.styleswap.utils.CategoryUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryUtils categoryUtils;

    @InjectMocks
    private CategoryServiceimpl categoryService;

    private Category category;
    private CategoryEdditDto  categoryEdditDto;
    private CategoryDto categoryDto;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = Category.builder()
                .name("T-Shirts")
                .description("Category for various T-Shirts")
                .build();

        categoryEdditDto = CategoryEdditDto.builder()
                .name("T-Shirts")
                .description("Category for various T-Shirts")
                .build();

        categoryDto = CategoryDto.builder()
                .name("T-Shirts")
                .description("Category for various T-Shirts")
                .build();
    }

    @Test
    void createCategory_createsAndReturnsCategory() {

        when(categoryUtils.mapCategoryToEntity(categoryEdditDto)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryUtils.mapCategoryToDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(categoryEdditDto);

        assertNotNull(result);
        assertEquals("T-Shirts", result.getName());
        assertEquals("Category for various T-Shirts", result.getDescription());
    }

    @Test
    void getCategory_returnsCategory() {

        when(categoryUtils.getCategoryFromDB(1L)).thenReturn(category);
        when(categoryUtils.mapCategoryToDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategory(1L);

        assertNotNull(result);
        assertEquals("T-Shirts", result.getName());
        assertEquals("Category for various T-Shirts", result.getDescription());
    }

    @Test
    void getAllCategories_returnsAllCategories() {
        Category category2 = new Category();
        category2.setName("Jeans");
        category2.setDescription("Category for different styles of Jeans");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category, category2));

        List<CategoryDto> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
    }

    @Test
    void updateCategory_updatesAndReturnsCategory() {
        categoryDto.setDescription("Updated description");

        when(categoryUtils.getCategoryFromDB(1L)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryUtils.mapCategoryToDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.updateCategory(1L, categoryEdditDto);

        assertNotNull(result);
        assertEquals("T-Shirts", result.getName());
        assertEquals("Updated description", result.getDescription());
    }

    @Test
    void deleteCategory_deletesCategory() {

        when(categoryUtils.getCategoryFromDB(1L)).thenReturn(category);
        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).delete(category);
    }
}