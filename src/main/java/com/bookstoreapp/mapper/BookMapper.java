package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class)
@Component
public interface BookMapper {
    @Mapping(target = "categoriesIds", ignore = true)
    BookDto toDto(Book book);

    @AfterMapping
    default void setCategoriesIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoriesIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoriesIds(categoriesIds);
    }

    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto requestDto) {
        Set<Category> categories = mapCategoriesIdsToCategories(requestDto.getCategories());
        book.setCategories(categories);
    }

    default Set<Category> mapCategoriesIdsToCategories(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryId -> {
                    Category category = new Category();
                    category.setId(categoryId);
                    return category;
                })
                .collect(Collectors.toSet());
    }

    void updateFromDto(@MappingTarget Book book, CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    // for CartItem mapper
    @Named("bookFromId")
    default Book getBookFromId(Long id) {
        return Optional.ofNullable(id)
                .map(Book::new)
                .orElse(null);
    }
}
