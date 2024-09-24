package com.bookstoreapp.service;

import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.CategoryMapper;
import com.bookstoreapp.model.Category;
import com.bookstoreapp.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save category with valid data should return created category with correct details")
    void save_ValidCategoryDto_ReturnsCreatedCategoryDto() {
        // Given
        Long categoryId = 1L;
        CategoryDto requestDto = new CategoryDto(categoryId, "Test Category", "Description");

        Category category = new Category()
                .setId(categoryId)
                .setName(requestDto.name())
                .setDescription(requestDto.description());

        CategoryDto expectedCategoryDto = new CategoryDto(categoryId, requestDto.name(), requestDto.description());

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        // When
        CategoryDto result = categoryService.save(requestDto);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(expectedCategoryDto.id(), result.id(), "The category ID should match the expected value");
        assertEquals(expectedCategoryDto.name(), result.name(), "The category name should match the expected value");
        assertEquals(expectedCategoryDto.description(), result.description(), "The category description should match the expected value");
    }

    @Test
    @DisplayName("Retrieve category by existing ID should return the correct category details")
    void getById_ExistingId_ReturnsCategoryDto() {
        // Given
        Long categoryId = 1L;
        Category category = new Category()
                .setId(categoryId)
                .setName("Test Category")
                .setDescription("Description");

        CategoryDto expectedCategoryDto = new CategoryDto(categoryId, "Test Category", "Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        // When
        CategoryDto result = categoryService.getById(categoryId);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(expectedCategoryDto.id(), result.id(), "The category ID should match the expected value");
        assertEquals(expectedCategoryDto.name(), result.name(), "The category name should match the expected value");
        assertEquals(expectedCategoryDto.description(), result.description(), "The category description should match the expected value");
    }

    @Test
    @DisplayName("Find category by non-existing ID should throw Not Found exception")
    void getById_NotFoundId_ShouldThrowNotFoundException() {
        // Given
        Long nonExistentCategoryId = 999L;
        when(categoryRepository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(nonExistentCategoryId),
                "Should throw EntityNotFoundException when category with the ID does not exist");
    }

    @Test
    @DisplayName("Retrieve all categories with pagination should return list of category DTOs")
    void findAll_WithPagination_ShouldReturnListOfCategoryDtos() {
        // Given
        Pageable pageable = Pageable.unpaged();
        Category category = new Category()
                .setId(1L)
                .setName("Test Category")
                .setDescription("Description");

        CategoryDto expectedCategoryDto = new CategoryDto(1L, "Test Category", "Description");

        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(category), pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        // When
        List<CategoryDto> result = categoryService.findAll(pageable);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.size(), "The size of the category list should be 1");

        CategoryDto actualCategoryDto = result.get(0);
        assertEquals(expectedCategoryDto.id(), actualCategoryDto.id(), "The category ID should match the expected value");
        assertEquals(expectedCategoryDto.name(), actualCategoryDto.name(), "The category name should match the expected value");
        assertEquals(expectedCategoryDto.description(), actualCategoryDto.description(), "The category description should match the expected value");
    }

    @Test
    @DisplayName("Update existing category with valid DTO should return updated category DTO")
    void update_ExistingId_ValidDto_ReturnsUpdatedCategoryDto() {
        // Given
        Long categoryId = 1L;
        Category existingCategory = new Category()
                .setId(categoryId)
                .setName("Old Category Name")
                .setDescription("Old Description");

        CategoryDto requestDto = new CategoryDto(categoryId, "Updated Category Name", "Updated Description");

        CategoryDto expectedUpdatedCategoryDto = new CategoryDto(categoryId, "Updated Category Name", "Updated Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toEntity(requestDto)).thenReturn(existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toDto(existingCategory)).thenReturn(expectedUpdatedCategoryDto);

        // When
        CategoryDto result = categoryService.update(categoryId, requestDto);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(expectedUpdatedCategoryDto.id(), result.id(), "The category ID should match the expected value");
        assertEquals(expectedUpdatedCategoryDto.name(), result.name(), "The category name should match the expected value");
        assertEquals(expectedUpdatedCategoryDto.description(), result.description(), "The category description should match the expected value");
    }

    @Test
    @DisplayName("Delete category by ID should invoke deleteById on repository")
    void delete_ExistingId_ShouldCallDeleteById() {
        // Given
        Long categoryId = 1L;

        // When
        categoryService.deleteById(categoryId);

        // Then
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
