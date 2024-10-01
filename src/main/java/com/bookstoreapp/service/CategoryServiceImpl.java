package com.bookstoreapp.service;

import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.dto.category.CategoryRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.CategoryMapper;
import com.bookstoreapp.model.Category;
import com.bookstoreapp.repository.category.CategoryRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find category by id " + id)
                );
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryRequestDto createCategoryRequestDto) {
        Category category = categoryMapper.toEntity(createCategoryRequestDto);
        return categoryMapper.toDto(
                categoryRepository.save(category)
        );
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find category by id " + id)
                );
        categoryMapper.updateFromDto(requestDto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
