package com.restapi.styleswap.controller;

import com.restapi.styleswap.exception.ErrorDetails;
import com.restapi.styleswap.payload.CategoryDto;
import com.restapi.styleswap.payload.CategoryEdditDto;
import com.restapi.styleswap.service.CategoryService;
import com.restapi.styleswap.utils.assemblers.CategoryModelAssembler;
import com.restapi.styleswap.utils.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/categories")
@Tag(name = "CategoryController", description = "Operations related to categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryModelAssembler assembler;

    public CategoryController(CategoryService categoryService, CategoryModelAssembler assembler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
    }


    @Operation(summary = "Create a new category, only ADMIN users can perform this operation", description = "Creates a new category and returns the created category.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "name: " + Constant.NAME_VALIDATION_FAILED,
                    content = @Content(schema = @Schema(implementation = String[].class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<EntityModel<CategoryDto>> createCategory(@RequestBody @Valid CategoryEdditDto categoryDto){

        var category = categoryService.createCategory(categoryDto);

        return ResponseEntity.created(getLocation(category.getId())).body(assembler.toModel(category));
    }


    @Operation(summary = "Get all categories", description = "Returns a list of all categories.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDto[].class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoryDto>>> getAllCategories(){
        var categories = categoryService.getAllCategories();
        return ResponseEntity.ok(assembler.toCollectionModel(categories));
    }


    @Operation(summary = "Get category by ID", description = "Returns a category by its ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found with id = 0",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> getCategoryById(@PathVariable long categoryId){
        var category = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(assembler.toModel(category));
    }


    @Operation(summary = "Update category", description = "Updates an existing category by its ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "name: " + Constant.NAME_VALIDATION_FAILED,
                    content = @Content(schema = @Schema(implementation = String[].class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found with id = 0",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> updateCategory(@PathVariable long categoryId,
                                                      @RequestBody @Valid CategoryEdditDto categoryDto){

        var updatedCategory = categoryService.updateCategory(categoryId, categoryDto);
        return ResponseEntity.ok(assembler.toModel(updatedCategory));
    }


    @Operation(summary = "Delete category", description = "Deletes a category by its ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found with id = 0",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long categoryId){
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