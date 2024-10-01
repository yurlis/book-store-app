package com.bookstoreapp.service;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.CartItemMapper;
import com.bookstoreapp.mapper.ShoppingCartMapper;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.CartItem;
import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.repository.book.BookRepository;
import com.bookstoreapp.repository.shoppingcart.CartItemRepository;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository itemRepository;
    private final CartItemMapper itemMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto addBookToShoppingCart(Long userId, AddCartItemRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.bookId())
                .orElseThrow(EntityNotFoundException.supplier("Book not found"));

        ShoppingCart cartFromDB = getCartFromDB(userId);

        Optional<CartItem> cartItem = itemRepository
                .findByShoppingCartIdAndBookId(cartFromDB.getUser().getId(), requestDto.bookId());

        if (cartItem.isPresent()) {
            CartItem existingItem = cartItem.get();
            int updatedQuantity = existingItem.getQuantity() + requestDto.quantity();
            UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(updatedQuantity);
            this.update(userId, existingItem.getId(), updateRequestDto);
        } else {
            CartItem requestedItemToAdd = itemMapper.toModel(requestDto);
            requestedItemToAdd.setShoppingCart(cartFromDB);
            cartFromDB.getCartItems().add(requestedItemToAdd);
            itemRepository.save(requestedItemToAdd);
        }
        ShoppingCart updatedCart = getCartFromDB(userId);
        return shoppingCartMapper.toDto(updatedCart);
    }

    @Override
    public ShoppingCartDto viewShoppingCart(Long id) {
        return shoppingCartMapper.toDto(getCartFromDB(id));
    }

    @Override
    public ShoppingCartDto update(
            Long userId, Long cartItemId, UpdateCartItemRequestDto requestDto) {
        CartItem itemFromDb = getItemFromDb(cartItemId);
        itemMapper.updateFromDto(requestDto, itemFromDb);
        itemRepository.save(itemFromDb);
        return shoppingCartMapper.toDto(getCartFromDB(userId));
    }

    @Override
    public ShoppingCartDto delete(Long userId, Long cartItemId) {
        itemRepository.deleteById(cartItemId);
        return shoppingCartMapper.toDto(getCartFromDB(userId));
    }

    private CartItem getItemFromDb(Long cartItemId) {
        return itemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find item by item id: " + cartItemId));
    }

    private ShoppingCart getCartFromDB(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find ShoppingCard for user with id: " + userId));
    }
}
