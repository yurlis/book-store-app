package com.bookstoreapp.controller;

import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.model.User;
import com.bookstoreapp.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BookStore. Shopping Cart management", description = "Endpoints for managing Shopping Carts in BookStore")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add book to the shopping cart", description = "Add a book to user's shopping cart")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addBook(Authentication authentication,
                                   @RequestBody @Valid AddCartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToShoppingCart(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "View user shopping cart",
            description = "Get all items from user shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.viewShoppingCart(user.getId());
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update the shopping cart item",
            description = "Update a specific item in the shopping cart")
    public ShoppingCartDto updateShoppingCartItems(Authentication authentication,
                                                   @PathVariable Long cartItemId,
                                                   @RequestBody @Valid UpdateCartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.update(user.getId(), cartItemId, requestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Delete the shopping cart item",
            description = "Delete a specific item from the shopping cart")
    public ShoppingCartDto deleteItems(Authentication authentication,
                                       @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.delete(user.getId(), cartItemId);
    }
}
