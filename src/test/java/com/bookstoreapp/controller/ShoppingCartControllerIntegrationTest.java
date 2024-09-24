package com.bookstoreapp.controller;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.CartItemDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.shoppingcart.CartItemRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ShoppingCartControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartItemRepository itemRepository;
    private User user;
    private Authentication authentication;

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

        user = new User();
        user.setId(1L);
        user.setEmail("testuser1@test.com");

        authentication = getAuthorisation(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shopping-carts/controller/add-shopping-carts-data.sql"));
        }
    }

    @Test
    @DisplayName("Add item to shopping cart when valid request")
    void addBook_WhenValidRequest_ShouldReturnCartItemDto() throws Exception {
        // Given
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto(3L, 2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        // Then
        assertNotNull(actual);
        assertEquals(3, actual.cartItems().size());

        boolean bookFound = false;

        for (CartItemDto item : actual.cartItems()) {
            if (item.bookId().equals(requestDto.bookId())) {
                assertEquals(requestDto.quantity(), item.quantity(), "The quantity of the item does not match the expected value.");
                bookFound = true;
                break;
            }
        }

        assertTrue(bookFound, "The book with ID " + requestDto.bookId() + " not found in the cart");
    }

    @Test
    @DisplayName("Add existing item to shopping cart should update quantity")
    void addBook_WhenBookAlreadyInCart_ShouldUpdateQuantity() throws Exception {
        // Given
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto(2L, 2);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        // Then
        assertNotNull(actual);

        boolean bookFound = false;

        for (CartItemDto item : actual.cartItems()) {
            if (item.bookId().equals(requestDto.bookId())) {
                assertEquals(6, item.quantity(), "The quantity of the item was not updated correctly.");
                bookFound = true;
                break;
            }
        }

        assertTrue(bookFound, "The book with ID " + requestDto.bookId() + " was not found in the cart");
    }

    @Test
    @DisplayName("Delete item from cart when existing ID")
    void deleteItems_WhenExistingId_ShouldReturnNoContent() throws Exception {
        // Given
        Long cartItemIdToDelete = 1L;

        // When
        mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemIdToDelete)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertFalse(itemRepository.existsById(cartItemIdToDelete),
                "Item should be deleted and not exist in the repository.");
    }

    @Test
    @DisplayName("Get shopping cart as user")
    void getShoppingCart_WhenCalled_ShouldReturnShoppingCartResponseDto() throws Exception {
        // Given

        // When
        ResultActions result = mockMvc.perform(get("/cart")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertNotNull(result);
        result.andExpect(status().isOk());
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
                    new ClassPathResource("database/shopping-carts/controller/delete-shopping-carts-data.sql"));
        }
    }

    private Authentication getAuthorisation(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                user, "password", authorities);
    }
}
