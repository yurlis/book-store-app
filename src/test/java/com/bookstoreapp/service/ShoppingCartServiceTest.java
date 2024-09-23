package com.bookstoreapp.service;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.CartItemDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.CartItemMapper;
import com.bookstoreapp.mapper.ShoppingCartMapper;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.CartItem;
import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.book.BookRepository;
import com.bookstoreapp.repository.shoppingcart.CartItemRepository;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import com.bookstoreapp.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository cartRepository;
    @Mock
    private CartItemRepository itemRepository;
    @Mock
    private CartItemMapper itemMapper;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    private User user;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setEmail("test@mail.com");

        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
    }

    @Test
    @DisplayName("Add a cart item with valid request DTO should return updated shopping cart DTO")
    void addItemToShoppingCart_ValidRequest_ReturnUpdatedCart() {
        // Given
        AddCartItemRequestDto requestDto = new AddCartItemRequestDto(1L, 2);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        when(cartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart cart = invocation.getArgument(0);
            cart.setId(1L);
            return cart;
        });

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(shoppingCart));

        when(bookRepository.findById(requestDto.bookId()))
                .thenReturn(Optional.of(new Book()));

        when(itemMapper.toModel(requestDto)).thenReturn(new CartItem());
        when(itemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(new ShoppingCartDto(
                1L, user.getId(), Set.of()
        ));

        // When
        ShoppingCartDto result = shoppingCartService.addBookToShoppingCart(user.getId(), requestDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(itemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Update existing cart item with valid request DTO should return updated shopping cart DTO")
    void updateCartItem_ExistingIdValidRequestDto_ReturnsUpdatedShoppingCartDto() {
        // Given
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);
        CartItem existingCartItem = new CartItem();
        existingCartItem.setQuantity(3);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(existingCartItem));
        doNothing().when(itemMapper).updateFromDto(any(), eq(existingCartItem));
        ShoppingCart shoppingCart = new ShoppingCart();
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(shoppingCart));

        CartItemDto cartItemDto = new CartItemDto(1L, 1L, "Book Title", 5); // Встановлюємо ID
        when(itemMapper.toDto(existingCartItem)).thenReturn(cartItemDto);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(1L, 1L, Set.of(cartItemDto));
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(shoppingCartDto);

        // When
        ShoppingCartDto result = shoppingCartService.update(1L, 1L, requestDto);

        // Then
        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.cartItems().size(), "The cart items size should match");
        assertEquals(5, result.cartItems().iterator().next().quantity(), "The quantity should be updated to the new value");
    }

    @Test
    @DisplayName("Update non-existing cart item should throw EntityNotFoundException")
    void updateCartItem_NonExistingId_ThrowsEntityNotFoundException() {
        // Given
        final Long CART_ITEM_ID = 999L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);

        when(itemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.update(user.getId(), CART_ITEM_ID, requestDto), "Expected EntityNotFoundException to be thrown");
    }

}
