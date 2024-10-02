package com.bookstoreapp.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
        Long id,
        String name,
        String description
) {}
