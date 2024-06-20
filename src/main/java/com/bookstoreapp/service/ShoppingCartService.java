package com.bookstoreapp.service;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;

public interface ShoppingCartService {
    ShoppingCartDto addBookToShoppingCart(Long userId, AddCartItemRequestDto requestDto);

    ShoppingCartDto viewShoppingCart(Long userId);

    ShoppingCartDto update(Long userId, Long cartItemId, UpdateCartItemRequestDto requestDto);

    ShoppingCartDto delete(Long userId, Long cartItemId);
}
