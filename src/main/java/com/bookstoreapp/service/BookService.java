package com.bookstoreapp.service;

import com.bookstoreapp.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> getAll();
}
