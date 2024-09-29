package com.bookstoreapp.repository.book;

import com.bookstoreapp.model.Book;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomBookRepository {
    List<Book> findAllWithCategories(Specification<Book> spec);
}
