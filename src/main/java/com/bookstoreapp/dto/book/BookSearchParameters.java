package com.bookstoreapp.dto.book;

public record BookSearchParameters(String[] titles, String[] authors, String isbn) {
}
