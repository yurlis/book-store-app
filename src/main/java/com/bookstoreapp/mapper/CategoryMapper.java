package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(config = MapperConfig.class)
@Component
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);

    void updateFromDto(CategoryDto categoryDto, @MappingTarget Category category);
}
