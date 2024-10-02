package com.bookstoreapp.service;

import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.dto.book.UpdateBookRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto update(Long id, UpdateBookRequestDto requestDto);

    void deleteByID(Long id);

    List<BookDto> search(BookSearchParameters params);

    List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id, Pageable pageable);
}
