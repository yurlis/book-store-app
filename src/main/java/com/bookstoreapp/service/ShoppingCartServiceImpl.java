package com.bookstoreapp.service;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.exception.BookAlreadyExistsException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository itemRepository;
    private final CartItemMapper itemMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto addBookToShoppingCart(Long userId, AddCartItemRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.bookId())
                .orElseThrow(EntityNotFoundException.supplier("Book not found"));

        ShoppingCart cartFromDB = getCartFromDB(userId);

        Optional<CartItem> cartItem = itemRepository
                .findByShoppingCartIdAndBookId(cartFromDB.getId(), requestDto.bookId());

        if (cartItem.isPresent()) {
            BookAlreadyExistsException.supplier("Book already exists in the cart");
        } else {
            CartItem requestedItemToAdd = itemMapper.toModel(requestDto);
            requestedItemToAdd.setShoppingCart(cartFromDB);
            cartFromDB.getCartItems().add(requestedItemToAdd);
            itemRepository.save(requestedItemToAdd);
        }

        return shoppingCartMapper.toDto(cartFromDB);
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
                .orElseGet(() -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    User userFromDb = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "User not found by user id: " + userId));
                    shoppingCart.setUser(userFromDb);
                    return cartRepository.save(shoppingCart);
                });
    }
}
