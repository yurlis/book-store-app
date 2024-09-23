package com.bookstoreapp.service;

import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.BookMapper;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.Category;
import com.bookstoreapp.repository.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookMapper bookMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save book with valid data should return created book with correct details")
    void save_ValidRequestDto_ReturnsCreatedBookDto() {
        // Given
        Long categoryId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Test Book")
                .setAuthor("Test Book Author")
                .setIsbn("1234567890123")
                .setPrice(new BigDecimal("99.9"))
                .setDescription("Test Book Description")
                .setCoverImage("Test Book Cover Image URL")
                .setCategories(Set.of(categoryId));

        Category category = new Category();
        category.setId(categoryId);

        Book book = new Book()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategories(Set.of(category));

        BookDto expectedBookDto = new BookDto()
                .setId(1L)
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoriesIds(Set.of(categoryId));

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        // When
        BookDto result = bookService.save(requestDto);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(expectedBookDto.getId(), result.getId(), "The book ID should match the expected value");
        assertEquals(expectedBookDto.getTitle(), result.getTitle(), "The book title should match the expected value");
        assertEquals(expectedBookDto.getAuthor(), result.getAuthor(), "The book author should match the expected value");
        assertEquals(expectedBookDto.getIsbn(), result.getIsbn(), "The book ISBN should match the expected value");
        assertEquals(expectedBookDto.getPrice(), result.getPrice(), "The book price should match the expected value");
        assertEquals(expectedBookDto.getDescription(), result.getDescription(), "The book description should match the expected value");
        assertEquals(expectedBookDto.getCoverImage(), result.getCoverImage(), "The book cover image URL should match the expected value");
        assertEquals(expectedBookDto.getCategoriesIds(), result.getCategoriesIds(), "The book category IDs should match the expected value");
    }

    @Test
    @DisplayName("Retrieve book by existing ID should return the correct book details")
    void findBookById_ExistingId_ReturnsBookDto() {
        // Given
        Long bookId = 3L;

        Category category = new Category();
        category.setId(1L);

        Book book = new Book()
                .setId(bookId)
                .setTitle("Test Book Title")
                .setAuthor("Test Author")
                .setIsbn("1234567890")
                .setPrice(new BigDecimal("123.45"))
                .setDescription("Test Book Description")
                .setCoverImage("cover.jpg")
                .setCategories(Set.of(category));

        BookDto expectedBookDto = new BookDto()
                .setId(bookId)
                .setTitle("Test Book Title")
                .setAuthor("Test Author")
                .setIsbn("1234567890")
                .setPrice(new BigDecimal("123.45"))
                .setDescription("Test Book Description")
                .setCoverImage("cover.jpg")
                .setCategoriesIds(Set.of(1L));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        // When
        BookDto result = bookService.findById(bookId);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(expectedBookDto.getId(), result.getId(), "The book ID should match the expected value");
        assertEquals(expectedBookDto.getTitle(), result.getTitle(), "The book title should match the expected value");
        assertEquals(expectedBookDto.getAuthor(), result.getAuthor(), "The book author should match the expected value");
        assertEquals(expectedBookDto.getIsbn(), result.getIsbn(), "The book ISBN should match the expected value");
        assertEquals(expectedBookDto.getPrice(), result.getPrice(), "The book price should match the expected value");
        assertEquals(expectedBookDto.getDescription(), result.getDescription(), "The book description should match the expected value");
        assertEquals(expectedBookDto.getCoverImage(), result.getCoverImage(), "The book cover image URL should match the expected value");
        assertEquals(expectedBookDto.getCategoriesIds(), result.getCategoriesIds(), "The book category IDs should match the expected value");
    }

    @Test
    @DisplayName("Find book by non-existing ID should throw Not Found exception")
    void findBookById_NotFoundId_ShouldThrowNotFoundException() {
        // Given
        Long nonExistentBookId = 999L;
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(nonExistentBookId),
                "Should throw BookNotFoundException when book with the ID does not exist");
    }

    @Test
    @DisplayName("Retrieve all books with pagination should return list of book DTOs")
    void findAllBooks_WithPagination_ShouldReturnListOfBookDtos() {
        // Given
        Pageable pageable = Pageable.unpaged();

        Category category = new Category();
        category.setId(1L);

        Book book = new Book()
                .setTitle("Test Book")
                .setAuthor("Test Author")
                .setIsbn("1234567890123")
                .setPrice(new BigDecimal("99.9"))
                .setDescription("Test Book Description")
                .setCoverImage("Test Cover Image")
                .setCategories(Set.of(category));

        List<Book> booksList = Collections.singletonList(book);

        BookDto expectedBookDto = new BookDto()
                .setTitle("Test Book")
                .setAuthor("Test Author")
                .setIsbn("1234567890123")
                .setPrice(new BigDecimal("99.9"))
                .setDescription("Test Book Description")
                .setCoverImage("Test Cover Image")
                .setCategoriesIds(Set.of(1L));

        when(bookRepository.findAllWithCategories(pageable)).thenReturn(booksList);

        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        // When
        List<BookDto> result = bookService.findAll(pageable);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.size(), "The size of the book list should be 1");

        BookDto actualBookDto = result.get(0);
        assertEquals(expectedBookDto.getTitle(), actualBookDto.getTitle(), "The title of the book should match the expected value");
        assertEquals(expectedBookDto.getAuthor(), actualBookDto.getAuthor(), "The author of the book should match the expected value");
        assertEquals(expectedBookDto.getIsbn(), actualBookDto.getIsbn(), "The ISBN of the book should match the expected value");
        assertEquals(expectedBookDto.getPrice(), actualBookDto.getPrice(), "The price of the book should match the expected value");
        assertEquals(expectedBookDto.getDescription(), actualBookDto.getDescription(), "The description of the book should match the expected value");
        assertEquals(expectedBookDto.getCoverImage(), actualBookDto.getCoverImage(), "The cover image URL of the book should match the expected value");
        assertEquals(expectedBookDto.getCategoriesIds(), actualBookDto.getCategoriesIds(), "The categories IDs should match the expected value");
    }

    @Test
    @DisplayName("Update existing book with valid request DTO should return updated book DTO")
    void updateBook_ExistingId_ValidRequestDto_ReturnsUpdatedBookDto() {
        // Given
        final Long BOOK_ID = 1L;
        final String OLD_TITLE = "Old Title";
        final String OLD_AUTHOR = "Old Author";
        final String OLD_ISBN = "1234567890";
        final BigDecimal OLD_PRICE = new BigDecimal("50.00");
        final String OLD_DESCRIPTION = "Old Description";
        final String OLD_COVER_IMAGE = "old_cover.jpg";

        final String UPDATED_TITLE = "Updated Title";
        final String UPDATED_AUTHOR = "Updated Author";
        final String UPDATED_ISBN = "0987654321";
        final BigDecimal UPDATED_PRICE = new BigDecimal("75.00");
        final String UPDATED_DESCRIPTION = "Updated Description";
        final String UPDATED_COVER_IMAGE = "updated_cover.jpg";
        final Set<Long> CATEGORIES = Set.of(1L);

        Book existingBook = new Book()
                .setId(BOOK_ID)
                .setTitle(OLD_TITLE)
                .setAuthor(OLD_AUTHOR)
                .setIsbn(OLD_ISBN)
                .setPrice(OLD_PRICE)
                .setDescription(OLD_DESCRIPTION)
                .setCoverImage(OLD_COVER_IMAGE);

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle(UPDATED_TITLE)
                .setAuthor(UPDATED_AUTHOR)
                .setIsbn(UPDATED_ISBN)
                .setPrice(UPDATED_PRICE)
                .setDescription(UPDATED_DESCRIPTION)
                .setCoverImage(UPDATED_COVER_IMAGE)
                .setCategories(CATEGORIES);

        BookDto expectedUpdatedBookDto = new BookDto()
                .setId(BOOK_ID)
                .setTitle(UPDATED_TITLE)
                .setAuthor(UPDATED_AUTHOR)
                .setIsbn(UPDATED_ISBN)
                .setPrice(UPDATED_PRICE)
                .setDescription(UPDATED_DESCRIPTION)
                .setCoverImage(UPDATED_COVER_IMAGE)
                .setCategoriesIds(CATEGORIES);

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedUpdatedBookDto);

        // When
        BookDto result = bookService.update(BOOK_ID, requestDto);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(expectedUpdatedBookDto.getId(), result.getId(), "The book ID should match the expected value");
        assertEquals(expectedUpdatedBookDto.getTitle(), result.getTitle(), "The book title should match the expected value");
        assertEquals(expectedUpdatedBookDto.getAuthor(), result.getAuthor(), "The book author should match the expected value");
        assertEquals(expectedUpdatedBookDto.getIsbn(), result.getIsbn(), "The book ISBN should match the expected value");
        assertEquals(expectedUpdatedBookDto.getPrice(), result.getPrice(), "The book price should match the expected value");
        assertEquals(expectedUpdatedBookDto.getDescription(), result.getDescription(), "The book description should match the expected value");
        assertEquals(expectedUpdatedBookDto.getCoverImage(), result.getCoverImage(), "The book cover image URL should match the expected value");
        assertEquals(expectedUpdatedBookDto.getCategoriesIds(), result.getCategoriesIds(), "The book category IDs should match the expected value");
    }
}
