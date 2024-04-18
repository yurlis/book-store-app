package com.bookstoreapp.dto.category;

public record CreateCategoryRequestDto(
        String name,
        String description
) {
}
