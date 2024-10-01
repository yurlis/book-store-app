package com.bookstoreapp.dto.book;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Accessors(chain = true)
public class UpdateBookRequestDto {
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Set<Long> categories;
}
