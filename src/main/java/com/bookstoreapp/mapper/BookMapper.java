package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateFromDto(@MappingTarget Book book, CreateBookRequestDto requestDto);
}
