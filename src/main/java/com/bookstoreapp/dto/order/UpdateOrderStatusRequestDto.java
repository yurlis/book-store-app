package com.bookstoreapp.dto.order;

import com.bookstoreapp.model.Order;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequestDto(
        @NotNull
        Order.Status status
) {}
