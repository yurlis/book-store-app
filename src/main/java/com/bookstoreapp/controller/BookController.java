package com.bookstoreapp.controller;

import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.BookSearchParameters;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BookStore. Book management", description = "Endpoints for managing book in BookStore")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
@SecurityRequirement(name = "bearerAuth")
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get all books (USER)",
            description = "Get a list of all available books with optional pagination")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<BookDto> getAll(@ParameterObject Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by Id (USER)",
            description = "Get a specific book by unique Id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Create a new book (ADMIN)", description = "Add a new book to the BookStore")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book (ADMIN)",
            description = "Update an existing book by specifying parameters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookDto updateBook(@PathVariable(name = "id") Long id,
                              @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book (ADMIN)",
            description = "Delete a specific book from the BookStore by unique Id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteByID(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books (USER)",
            description = "Search book list by dynamically specify criteria to filter desired books")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<BookDto> searchBooks(BookSearchParameters searchParameters) {
        return bookService.search(searchParameters);
    }
}
