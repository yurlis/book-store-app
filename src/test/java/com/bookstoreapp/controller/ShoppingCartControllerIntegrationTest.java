package com.bookstoreapp.controller;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import com.bookstoreapp.repository.user.UserRepository;
import com.bookstoreapp.service.ShoppingCartServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ShoppingCartControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository cartRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ShoppingCart newCart = new ShoppingCart();
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart cart = invocation.getArgument(0);
            return cart;
        });

        ShoppingCartDto expectedResponse = new ShoppingCartDto(1L, 1L, Set.of());

        when(shoppingCartService.addBookToShoppingCart(anyLong(), any(AddCartItemRequestDto.class)))
                .thenReturn(expectedResponse);

        when(shoppingCartService.update(anyLong(), anyLong(), any(UpdateCartItemRequestDto.class)))
                .thenReturn(expectedResponse);
    }

    @Test
    @DisplayName("Add book to the shopping cart - Valid request")
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void addBookToShoppingCart_ValidRequestDto_ReturnsShoppingCartDto() throws Exception {
        Long bookId = 1L;
        int quantity = 2;
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto(bookId, quantity);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.id());
    }

    @Test
    @DisplayName("Update shopping cart item - Valid request")
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void updateShoppingCartItem_ValidRequestDto_ReturnsUpdatedShoppingCartDto() throws Exception {
        Long cartItemId = 1L;
        int newQuantity = 3;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(newQuantity);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/cart/items/{cartItemId}", cartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.id());
    }
}
