package com.bookstoreapp.controller;

import com.bookstoreapp.dto.book.BookDto;
import com.bookstoreapp.dto.book.CreateBookRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.service.BookService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookService bookService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-books-data.sql"));
        }
    }

    @Test
    @DisplayName("Create book when valid data is provided")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void createBook_WhenValidRequestProvided_ShouldReturnBookDto() throws Exception {
        // Given
        Long category = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Test Book Title 4")
                .setAuthor("Test Book Author 4")
                .setIsbn("4897984515")
                .setPrice(new BigDecimal("400.00"))
                .setDescription("Test Book Description 4")
                .setCoverImage("Test Book Cover Image URL 4")
                .setCategories(Set.of(category));

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoriesIds(requestDto.getCategories());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);

        // Then
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Delete book by ID as admin when book exists")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteBook_WhenBookExists_ShouldReturnNoContent() throws Exception {
        //Given
        Long bookIdToDelete = 1L;

        // When
        mockMvc.perform(delete("/books/{id}", bookIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        // Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookIdToDelete));
    }

    @Test
    @DisplayName("Delete non-existent book by ID as admin")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteBook_WhenBookDoesNotExist_ShouldReturnNoContent() throws Exception {
        // Given
        Long notExistingBookIdToDelete = 999L;

        // When
        mockMvc.perform(delete("/books/{id}", notExistingBookIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        // Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(notExistingBookIdToDelete));
    }

    @Test
    @DisplayName("Find existing book by ID as user")
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void getBookById_WhenBookExists_ShouldReturnBookDto() throws Exception {
        // Given
        Long bookIdToRetrieve = 3L;
        BookDto expected = new BookDto()
                .setTitle("Test Book Title 3")
                .setId(bookIdToRetrieve);

        // When
        ResultActions result = mockMvc.perform(get("/books/{id}", bookIdToRetrieve)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertNotNull(result);
        result.andExpect(jsonPath("$.title").value(expected.getTitle()))
                .andExpect(jsonPath("$.id").value(bookIdToRetrieve));
    }

    @Test
    @DisplayName("Get all books with pagination as user")
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void getAll_WithPaginationAsUser_ShouldReturnBookList() throws Exception {
        // Given
        int page = 0;
        int size = 10;

        BookDto[] expectedBooks;
        expectedBooks = buildExpectedBookDtosArray();

        // When
        ResultActions result = mockMvc.perform(get("/books")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        String jsonResponse = result.andReturn().getResponse().getContentAsString();
        List<BookDto> actualBooks = objectMapper.readValue(jsonResponse, new TypeReference<List<BookDto>>() {
        });

        // Then
        assertNotNull(actualBooks, "The list of books should not be null");
        assertEquals(expectedBooks.length, actualBooks.size(), "The size of the list should be equal to expected");

        for (int i = 0; i < expectedBooks.length; i++) {
            BookDto expected = expectedBooks[i];
            BookDto actual = actualBooks.get(i);
            assertNotNull(actual, "The book at index " + i + " should not be null");
            assertTrue(expected.getPrice().compareTo(actual.getPrice()) == 0,
                    "The price at index " + i + " should be equal to expected");
            assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "price", "categoriesIds"),
                    "The book at index " + i + " should be equal to expected, except for id, price and categoriesIds");
            assertTrue(expected.getCategoriesIds().equals(new HashSet<>(actual.getCategoriesIds())),
                    "The categoriesIds at index " + i + " should be equal to expected");
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/delete-books-data.sql"));
        }
    }

    private BookDto[] buildExpectedBookDtosArray() {
        BookDto[] expectedBooks = new BookDto[3];
        expectedBooks[0] = new BookDto()
                .setId(1L)
                .setTitle("Test Book Title 1")
                .setAuthor("Test Book Author 1")
                .setIsbn("5678901234")
                .setPrice(new BigDecimal(100.00))
                .setDescription("Test Book Description 1")
                .setCoverImage("Test Book Cover Image URL 1")
                .setCategoriesIds(Collections.singleton(1L));

        expectedBooks[1] = new BookDto()
                .setId(2L)
                .setTitle("Test Book Title 2")
                .setAuthor("Test Book Author 2")
                .setIsbn("3456789012")
                .setPrice(new BigDecimal(200.00))
                .setDescription("Test Book Description 2")
                .setCoverImage("Test Book Cover Image URL 2")
                .setCategoriesIds(Collections.singleton(1L));

        expectedBooks[2] = new BookDto()
                .setId(3L)
                .setTitle("Test Book Title 3")
                .setAuthor("Test Book Author 3")
                .setIsbn("1234567890")
                .setPrice(new BigDecimal(300.00))
                .setDescription("Test Book Description 3")
                .setCoverImage("Test Book Cover Image URL 3")
                .setCategoriesIds(Collections.singleton(2L));
        return expectedBooks;
    }
}
