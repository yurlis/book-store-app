package com.bookstoreapp.controller;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.CartItemDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import com.bookstoreapp.service.ShoppingCartServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @MockBean
    private ShoppingCartServiceImpl shoppingCartService;
    @MockBean
    private ShoppingCartRepository cartRepository;
    @Autowired
    private ObjectMapper objectMapper;
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
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@test.com");

        authentication = getAuthorisation(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ShoppingCart newCart = new ShoppingCart();
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart cart = invocation.getArgument(0);
            return cart;
        });

        ShoppingCartDto expectedResponse = new ShoppingCartDto(1L, 1L, Set.of());

        when(shoppingCartService.addBookToShoppingCart(eq(1L), any(AddCartItemRequestDto.class)))
                .thenReturn(expectedResponse);

        when(shoppingCartService.update(eq(1L), anyLong(), any(UpdateCartItemRequestDto.class)))
                .thenReturn(expectedResponse);
    }

    @Test
    @DisplayName("Add book to shopping cart with valid request")
    void addBook_WhenValidRequest_ShouldReturnShoppingCartDto()  throws Exception {
        // Given
        Long bookId = 1L;
        int quantity = 2;
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto(bookId, quantity);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        Set<CartItemDto> cartItems = Set.of(new CartItemDto(1L, bookId, "Test Book Title", quantity));
        ShoppingCartDto expectedResponse = new ShoppingCartDto(1L, 1L, cartItems);

        when(shoppingCartService.addBookToShoppingCart(eq(1L), any(AddCartItemRequestDto.class)))
                .thenReturn(expectedResponse);

        // When
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);

        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(expectedResponse.id(), actual.id());
        assertEquals(cartItems.size(), actual.cartItems().size());

        CartItemDto addedItem = actual.cartItems().stream()
                .filter(item -> item.bookId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Added item not found"));

        assertEquals(quantity, addedItem.quantity());
        assertEquals("Test Book Title", addedItem.bookTitle());
    }

    @Test
    @DisplayName("Update shopping cart item with valid request")
    void updateShoppingCartItems_WhenValidRequest_ShouldReturnUpdatedShoppingCartDto() throws Exception {
        // Given
        Long cartItemId = 1L;
        Long bookId = 1L;
        String bookTitle = "Test Book Title";
        int newQuantity = 3;

        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(newQuantity);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        Set<CartItemDto> cartItems = Set.of(new CartItemDto(cartItemId, bookId, bookTitle, newQuantity));
        ShoppingCartDto expectedResponse = new ShoppingCartDto(1L, 1L, cartItems);

        when(shoppingCartService.update(eq(1L), eq(cartItemId), any(UpdateCartItemRequestDto.class)))
                .thenReturn(expectedResponse);

        // When
        MvcResult result = mockMvc.perform(
                        put("/cart/items/{cartItemId}", cartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);

        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(expectedResponse.id(), actual.id());

        CartItemDto updatedItem = actual.cartItems().stream()
                .filter(item -> item.id().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Cart item not found"));

        assertEquals(newQuantity, updatedItem.quantity());
        assertEquals(bookTitle, updatedItem.bookTitle());
    }

    private Authentication getAuthorisation(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                user, "password", authorities);
    }
}
