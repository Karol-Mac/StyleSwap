package com.restapi.vinted.service;

import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.CategoryDto;
import com.restapi.vinted.payload.CategoryEdditDto;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.service.impl.CategoryServiceimpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.restapi.vinted.entity.Category;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class CategoryServiceimplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceimpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_createsAndReturnsCategory() {
        CategoryEdditDto categoryDto = new CategoryEdditDto();
        categoryDto.setName("T-Shirts");
        categoryDto.setDescription("Category for various T-Shirts");

        Category category = new Category();
        category.setName("T-Shirts");
        category.setDescription("Category for various T-Shirts");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("T-Shirts", result.getName());
        assertEquals("Category for various T-Shirts", result.getDescription());
    }

    @Test
    void getCategory_returnsCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("T-Shirts");
        category.setDescription("Category for various T-Shirts");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategory(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("T-Shirts", result.getName());
        assertEquals("Category for various T-Shirts", result.getDescription());
    }

    @Test
    void getCategory_throwsResourceNotFoundException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategory(1L));
    }

    @Test
    void getAllCategories_returnsAllCategories() {
        Category category1 = new Category();
        category1.setName("T-Shirts");
        category1.setDescription("Category for various T-Shirts");

        Category category2 = new Category();
        category2.setName("Jeans");
        category2.setDescription("Category for different styles of Jeans");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<CategoryDto> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
    }

    @Test
    void updateCategory_updatesAndReturnsCategory() {
        CategoryEdditDto categoryDto = new CategoryEdditDto();
        categoryDto.setName("T-Shirts");
        categoryDto.setDescription("Updated description");

        Category category = new Category();
        category.setId(1L);
        category.setName("T-Shirts");
        category.setDescription("Category for various T-Shirts");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.updateCategory(1L, categoryDto);

        assertNotNull(result);
        assertEquals("T-Shirts", result.getName());
        assertEquals("Updated description", result.getDescription());
    }

    @Test
    void deleteCategory_deletesCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("T-Shirts");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        String result = categoryService.deleteCategory(1L);

        assertEquals("Category successfully deleted!", result);
        verify(categoryRepository, times(1)).delete(category);
    }
}