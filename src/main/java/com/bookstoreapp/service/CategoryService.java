package com.bookstoreapp.service;

import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.dto.category.CategoryRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryRequestDto createCategoryRequestDto);

    CategoryDto update(Long id, CategoryRequestDto requestDto);

    void deleteById(Long id);
}
