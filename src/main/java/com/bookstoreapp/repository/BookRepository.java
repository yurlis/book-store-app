package com.bookstoreapp.repository;

import com.bookstoreapp.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
