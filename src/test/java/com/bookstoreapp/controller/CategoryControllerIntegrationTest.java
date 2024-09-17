package com.bookstoreapp.controller;

import com.bookstoreapp.dto.category.CategoryDto;
import com.bookstoreapp.service.CategoryService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest {

    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryService categoryService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
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
                    new ClassPathResource("database/categories/insert-categories.sql"));
        }
    }

    @Test
    @DisplayName("Create category with valid data")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void createCategory_ValidRequestDto_ReturnCategoryDto() throws Exception {
        // Given
        CategoryDto requestDto = new CategoryDto(null, "New Category", "New Category Description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(requestDto.name(), actual.name());
        assertEquals(requestDto.description(), actual.description());
    }

    @Test
    @DisplayName("Get all categories with pagination as user")
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void getAllCategories_WithPagination_ShouldReturnCategoryList() throws Exception {
        // Given
        int page = 0;
        int size = 10;

        // When
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        String jsonResponse = result.andReturn().getResponse().getContentAsString();
        List<CategoryDto> actualCategories = objectMapper.readValue(jsonResponse, new TypeReference<List<CategoryDto>>() {});

        assertNotNull(actualCategories);
        assertEquals(4, actualCategories.size(), "Expected 4 categories");
    }

    @Test
    @DisplayName("Get category by ID as user")
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void getCategoryById_ValidId_ShouldReturnCategoryDto() throws Exception {
        // Given
        Long categoryId = 1L;

        // When
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        result.andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Test Category 1"));
    }

    @Test
    @DisplayName("Update category as admin")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateCategory_ValidRequestDto_ShouldReturnUpdatedCategory() throws Exception {
        // Given
        Long categoryId = 1L;
        CategoryDto requestDto = new CategoryDto(categoryId, "Updated Category Name", "Updated Description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(requestDto.name(), actual.name());
        assertEquals(requestDto.description(), actual.description());
    }

    @Test
    @DisplayName("Delete category as admin")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteCategory_ValidId_ShouldReturnNoContent() throws Exception {
        // Given
        Long categoryIdToDelete = 1L;

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", categoryIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Then
        assertThrows(Exception.class, () -> categoryService.getById(categoryIdToDelete));
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
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("database/clean-up-data.sql"));
        }
    }
}
