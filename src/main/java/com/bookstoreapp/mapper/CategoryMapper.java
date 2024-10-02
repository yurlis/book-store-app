package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.dto.category.CategoryRequestDto;
import com.bookstoreapp.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(config = MapperConfig.class)
@Component
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryRequestDto createCategoryRequestDto);

    void updateFromDto(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}
