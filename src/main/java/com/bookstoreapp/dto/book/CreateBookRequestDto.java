package com.bookstoreapp.dto.book;

import com.bookstoreapp.validator.isbn.IsbnConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateBookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @IsbnConstraint
    private String isbn;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotBlank
    private String description;
    @NotNull
    private String coverImage;
    private Set<Long> categories;
}
