package com.bookstoreapp.service;


import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.BookMapper;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.repository.book.BookRepository;
import com.bookstoreapp.repository.book.BookSpecificationBuilder;
import java.util.List;

import com.bookstoreapp.repository.book.CustomBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CustomBookRepository customBookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAllWithCategories(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id)
        );
        bookMapper.updateFromDto(book, requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteByID(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParameters params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        List<Book> books = customBookRepository.findAllWithCategories(bookSpecification);

        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
