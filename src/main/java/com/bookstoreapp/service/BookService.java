package com.bookstoreapp.service;

import com.bookstoreapp.dto.BookDto;
import com.bookstoreapp.dto.BookSearchParameters;
import com.bookstoreapp.dto.CreateBookRequestDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteByID(Long id);

    public List<BookDto> search(BookSearchParameters params);
}
