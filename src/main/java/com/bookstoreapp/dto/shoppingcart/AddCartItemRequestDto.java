package com.bookstoreapp.dto.shoppingcart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequestDto(
    @NotNull
    @Positive
    Long bookId,
    @NotNull
    @Positive
    int quantity
) {}
