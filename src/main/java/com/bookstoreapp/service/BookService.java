package com.bookstoreapp.service;

import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteByID(Long id);

    List<BookDto> search(BookSearchParameters params);
}
