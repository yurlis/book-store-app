package com.bookstoreapp.service;

import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.dto.book.CreateBookRequestDto;

import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteByID(Long id);

    public List<BookDto> search(BookSearchParameters params);
}
