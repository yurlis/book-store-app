package com.bookstoreapp.dto;

public record BookSearchParameters(String[] titles, String[] authors, String isbn) {
}
