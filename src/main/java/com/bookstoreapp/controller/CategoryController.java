package com.bookstoreapp.controller;

import com.bookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.dto.category.CategoryRequestDto;
import com.bookstoreapp.service.BookService;
import com.bookstoreapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "BookStore. Category management", description = "Endpoints for managing categories in BookStore")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new category (ADMIN)", description = "Add a new category to the BookStore")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryRequestDto createCategoryRequestDto) {
        return categoryService.save(createCategoryRequestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all categories (USER)",
            description = "Get a list of all categories with optional pagination")
    public List<CategoryDto> getAll(@ParameterObject Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get category by Id (USER)",
            description = "Get a specific category by unique Id")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a category (ADMIN)",
            description = "Update an existing category by specifying parameters")
    public CategoryDto updateCategory(@PathVariable(name = "id") Long id,
                                      @RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category (ADMIN)",
            description = "Delete a specific category from the BookStore by unique Id")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @GetMapping("/{id}/books")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Search books by category Id (USER)",
            description = "Get books associated with a specific category by id")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id,
                                                                @ParameterObject Pageable pageable) {
        return bookService.findBooksByCategoryId(id, pageable);
    }
}
