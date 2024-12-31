package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;
import com.restapi.styleswap.service.CategoryService;
import com.restapi.styleswap.utils.CategoryModelAssembler;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;

    public CategoryController(CategoryService categoryService, CategoryModelAssembler assembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<CategoryDto>> createCategory(@RequestBody @Valid CategoryEdditDto categoryDto){

        var category = categoryService.createCategory(categoryDto);

        return ResponseEntity.created(getLocation(category.getId())).body(assembler.toModel(category));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoryDto>>> getAllCategories(){
        var categories = categoryService.getAllCategories();
        return ResponseEntity.ok(assembler.toCollectionModel(categories));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> getCategoryById(@PathVariable long categoryId){
        var category = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(assembler.toModel(category));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> updateCategory(@PathVariable long categoryId,
                                                      @RequestBody @Valid CategoryEdditDto categoryDto){

        var updatedCategory = categoryService.updateCategory(categoryId, categoryDto);
        return ResponseEntity.ok(assembler.toModel(updatedCategory));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    private URI getLocation(Object resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{resourceId}")
                .buildAndExpand(resourceId)
                .toUri();
    }
}