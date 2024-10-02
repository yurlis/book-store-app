package com.bookstoreapp.service;


import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookDtoWithoutCategoryIds;
import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.dto.book.UpdateBookRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.BookMapper;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.Category;
import com.bookstoreapp.repository.book.BookRepository;
import com.bookstoreapp.repository.book.BookSpecificationBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.bookstoreapp.repository.book.CustomBookRepository;
import com.bookstoreapp.repository.category.CategoryRepository;
import com.bookstoreapp.validator.isbn.IsbnCustomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
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
    public BookDto update(Long id, UpdateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id " + id)
        );

        fillMissingFieldsAndValidate(book, requestDto);

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

    private void fillMissingFieldsAndValidate(Book book, UpdateBookRequestDto requestDto) {

        if (requestDto.getTitle() == null || requestDto.getTitle().isEmpty()) {
            requestDto.setTitle(book.getTitle());
        }
        if (requestDto.getAuthor() == null || requestDto.getAuthor().isEmpty()) {
            requestDto.setAuthor(book.getAuthor());
        }
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            requestDto.setDescription(book.getDescription());
        }
        if (requestDto.getCoverImage() == null || requestDto.getCoverImage().isEmpty()) {
            requestDto.setCoverImage(book.getCoverImage());
        }

        if (requestDto.getCategories() == null || requestDto.getCategories().isEmpty()) {
            Set<Long> categoryIds = book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            requestDto.setCategories(categoryIds);
        }

        if (requestDto.getPrice() != null) {
            if (requestDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0.");
            }
        } else {
            requestDto.setPrice(book.getPrice());
        }

        if (requestDto.getIsbn() != null && !requestDto.getIsbn().isEmpty()) {
            IsbnCustomValidator isbnValidator = new IsbnCustomValidator();
            if (!isbnValidator.isValid(requestDto.getIsbn(), null)) {
                throw new IllegalArgumentException("Invalid ISBN format.");
            }
        } else {
            requestDto.setIsbn(book.getIsbn());
        }
    }
}
